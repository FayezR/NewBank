package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalTime;

public class NewBankClientHandler extends Thread{
	
	private NewBank bank;
	private BufferedReader in;
	private PrintWriter out;
	
	
	public NewBankClientHandler(Socket s) throws IOException {
		bank = NewBank.getBank();
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		out = new PrintWriter(s.getOutputStream(), true);
	}
	
	public void run() {
		// keep getting requests from the client and processing them
		try {
			out.println("Enter Username");
			String userName = in.readLine();
			out.println("Enter Password");
			String password = in.readLine();
			out.println("Checking Details...");

			// authenticate user - checks if Username exists and password is correct -FR
			if(bank.checkLogInDetails(userName, password)) {
				CustomerID customer = bank.getCustomerID(userName); //get customer ID token from bank for use in subsequent requests
				customer.setTimeAtLastActivity(LocalTime.now());
				if(bank.isCustomer(customer)) {
					out.println(Menu.printMenu());
					while(true) {
						//check for last activity > 2 mins
						Long timeDuration = Duration.between(customer.getTimeAtLastActivity(), LocalTime.now()).toMinutes();
						if (timeDuration >= 1) {
							Thread.currentThread().interrupt();
							this.run();
						}
						customer.setTimeAtLastActivity(LocalTime.now());
						String userInput = in.readLine();
						System.out.println("Request from " + customer.getKey() + " - " + userInput);
						// splits 'userInput' into separate words and stores them in a String array 'request' -FR
						String[] request = userInput.split(" ");
						String responce = bank.processRequest(customer, request);
						out.println(responce);
					}
				}
				else if(bank.isAdmin(customer)) {
						out.println("Welcome Admin!");
						while (true) {
							String userInput = in.readLine();
							System.out.println("Request from " + customer.getKey() + " - " + userInput);
							// splits 'userInput' into separate words and stores them in a String array 'request' -FR
							String[] request = userInput.split(" ");
							String response = bank.processAdminRequest(customer, request);
							out.println(response);
						}
				}

			}


			else{
				out.println("Username or password is incorrect. Try entering your username and password again \n");
				Thread.currentThread().interrupt();
				run();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}

	
	
}
