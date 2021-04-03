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
			// ask for user name
			out.println("Enter Username");
			String userName = in.readLine();
			// ask for password
			out.println("Enter Password");
			String password = in.readLine();
			out.println("Checking Details...");
			// authenticate user and get customer ID token from bank for use in subsequent requests
			CustomerID customer = bank.checkLogInDetails(userName, password);
			// if the user is authenticated then get requests from the user and process them 
			if(customer != null) {
				customer.setTimeAtLastActivity(LocalTime.now());
				out.println(Menu.printMenu());
				while(true) {
					//check for last activity > 2 mins
					Long timeDuration = Duration.between(customer.getTimeAtLastActivity(), LocalTime.now() ).toMinutes();
					if(timeDuration>=1) {
						Thread.currentThread().interrupt();
						this.run();
					}
					customer.setTimeAtLastActivity(LocalTime.now());
					String userInput = in.readLine();
					System.out.println("Request from " + customer.getKey() + " - " + userInput);
					// splits 'userInput' into separate words and stores them in a String array 'request'
					String [] request = userInput.split(" ");
					String responce = bank.processRequest(customer, request);
					out.println(responce);
				}
			}
			else {
				out.println("Log In Failed");
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
