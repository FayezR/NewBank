package newbank.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	//for database
	private Connection connection;
	private static final String updateAllAccountInfo = "INSERT INTO account_info(id, username, name, main, savings, checking) VALUES(?, ?, ?, ?, ?, ?)";
	
	private NewBank() {
		customers = new HashMap<>();
		connectDB();
		addTestData();
	}
	
	public static <Key, Value> Key getKeyFromValue(Map<Key, Customer> map, Value value) 
	{
        for (Map.Entry<Key, Customer> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
	
	private void addTestData() {
		Customer bhagy = new Customer();
		bhagy.addAccount(new Account("Main", 1000.0));
		customers.put("Bhagy", bhagy);
		updateAccountInfo("Bhagy", "Bhagy", "Bhagy", 1000.0, 0.0, 0.0);

		Customer christina = new Customer();
		christina.addAccount(new Account("Savings", 1500.0));
		customers.put("Christina", christina);
		updateAccountInfo("Christina", "Christina", "Christina", 0.0, 1500.0, 0.0);

		
		Customer john = new Customer();
		john.addAccount(new Account("Checking", 250.0));
		customers.put("John", john);
		updateAccountInfo("John", "John", "John", 0.0, 0.0, 250.0);
	}
	
	//please don't change anything in the method below unless you consult me first- mm
		public void connectDB()
		{
			try 
			{
				Class.forName("org.sqlite.JDBC");
				connection = DriverManager.getConnection("jdbc:sqlite:newbankdb.db");
				System.out.println("DB connected");
			}
			catch(Exception e)
			{
				System.out.print("DB not connected");
				e.printStackTrace();
			}
			
			//account_info table
			//needs to be edited as main, savings, and checking type of accounts exist right away
			//CONSULT TEAM
			
			try 
			{
				String createTable1 = "CREATE TABLE IF NOT EXISTS account_info (\n"
		                + "	id text PRIMARY KEY,\n"
		                + "	username text NOT NULL,\n"
		                + "	name text NOT NULL,\n"
		                + "	main real,\n"
		                + "	savings real,\n"
		                + "	checking real\n"
		                + ");";
				
				Statement stat = connection.createStatement();
				stat.execute(createTable1);
				System.out.println("Table account_info has been added");

			}
			catch (Exception e)
			{
				System.out.println("Table account_info already exists");
				//e.printStackTrace();
			}
			
			//account passwords
			//stores usernames and passwords
			//CONSULT TEAM - passwords need to be encrypted later
			
			try 
			{
				String createTable2 = "CREATE TABLE IF NOT EXISTS passwords (\n"
		                + "	username text PRIMARY KEY,\n"
		                + "	password text\n"
		                + ");";
				
				Statement stat = connection.createStatement();
				stat.execute(createTable2);
				System.out.println("Table passwords has been added");

			}
			catch (Exception e)
			{
				System.out.println("Table passwords already exists");
				e.printStackTrace();
			}
			
			//transactions table
			//stores username, transactions, and the date of the transaction
			//have to define transaction types??
			try 
			{
				String createTable3 = "CREATE TABLE IF NOT EXISTS transactions (\n"
		                + "	username text PRIMARY KEY,\n"
		                + "	date text NOT NULL,\n"
		                + "	type text NOT NULL,\n"
		                + "	result text NOT NULL,\n"
		                + "	notes test\n"
		                + ");";
				
				Statement stat = connection.createStatement();
				stat.execute(createTable3);
				System.out.println("Table transactions has been added");

			}
			catch (Exception e)
			{
				System.out.println("Table transactions already exists");
				e.printStackTrace();
			}				
		}
		
			
		public void updateAccountInfo(String id, String username, String name, double main, double savings, double checking)
		{
			PreparedStatement ps;
			try 
			{
				ps = this.connection.prepareStatement(updateAllAccountInfo);
				ps.setString(1, id);
		        ps.setString(2, username);
		        ps.setString(3, name);
		        ps.setDouble(4, main);
		        ps.setDouble(5, savings);
		        ps.setDouble(6, checking);
		        ps.executeUpdate();
		        System.out.println("Table account_info updated, new values added: ");
		        System.out.print(id+"\t" + username+"\t" + name+"\t" + main+"\t" + savings+"\t" + checking+"\t");
		        System.out.println();
		         
			} 
			catch (SQLException e) 
			{
				// TODO Auto-generated catch block
				System.out.println("Table account_info NOT updated, new values NOT added");
				System.out.print(id+"\t" + username+"\t" + name+"\t" + main+"\t" + savings+"\t" + checking+"\t");
				System.out.println();
				e.printStackTrace();
			}
		}
		
		public void updateAccountBalance (Customer customer, String accType, double amount)
		{
			PreparedStatement ps;
			String query;
			
			switch(accType)
			{
			case "Main":
				query = "UPDATE account_info SET main=? WHERE username = ?";
				try
				{
					ps = connection.prepareStatement(query);
				    ps.setDouble(1, amount);
				    ps.setString(2, getKeyFromValue(customers, customer));
				    ps.executeUpdate();
				}
				catch (Exception e)
				{
					System.out.println("Main couldn't be updated");
					e.printStackTrace();
				}
				break;
			case "Savings":
				query  = "UPDATE account_info SET savings=? WHERE username = ?";
				try
				{
					ps = connection.prepareStatement(query);
				    ps.setDouble(1, amount);
				    ps.setString(2, getKeyFromValue(customers, customer));
				    ps.executeUpdate();
				}
				catch (Exception e)
				{
					System.out.println("Savings couldn't be updated");
					e.printStackTrace();
				}
				break;
			case "Checking":
				query = "UPDATE account_info SET checking=? WHERE username = ?";
				try
				{
					ps = connection.prepareStatement(query);
				    ps.setDouble(1, amount);
				    ps.setString(2, getKeyFromValue(customers, customer));
				    ps.executeUpdate();
				}
				catch (Exception e)
				{
					System.out.println("Checking couldn't be updated");
					e.printStackTrace();
				}
				break;
			default:
				System.out.println("Account update failed.");
				break;
			}
		}
	
	public static NewBank getBank() {
		return bank;
	}
	
	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		if(customers.containsKey(userName)) {
			return new CustomerID(userName);
		}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String [] request) {
		if(customers.containsKey(customer.getKey())) {
			switch(request [0]) {
			case "SHOWMYACCOUNTS" : return showMyAccounts(customer);

			case "NEWACCOUNT" : try { return newAccount(customer, request[1]);}
								//error is caught if user doesn't specify a name for the new account
								catch (ArrayIndexOutOfBoundsException e) {return "Please enter the NEWACCOUNT command in the form: NEWACCOUNT <name>.";}
			
			//External Money Transfer FR1.5 Added by Abhinav
			case "PAY" : return payOthers(customer, request);
			
			default : return "FAIL";
			}
		}
		return "FAIL";
	}
	
	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

	private String newAccount (CustomerID customer, String name) {
		customers.get(customer.getKey()).addAccount(new Account (name, 0.00));
		return "SUCCESS";
	}
	
	//Method when "PAY" Keyword is used
	private String payOthers (CustomerID customer, String[] request) {
		if(request.length==1) { //only PAY mentioned
			return "You have following accounts" + "\n" + showMyAccounts(customer)+ "Please select the account type for payment in the form:" +
				"PAY FROM <YourAccountType> TO <Person/Company> <RecepientAccountType> <Amount>";
		} else if (request.length==7 && request[1].equals("FROM") && request[3].equals("TO")) { //if the length of request matches the format
			return makePayment(customer, request);			
		} else { // for all other cases
			return "You have following accounts" + "\n" + showMyAccounts(customer)+ "Please select the account type for payment in the form:" +
					"PAY FROM <AccountType> TO <Person/Company> <RecepientAccountType>  <Amount>";
		}

	}
	
	//Method when "PAY FROM <AccountType> TO <Person/Company> <RecepientAccountType> <Amount>" is used
	private String makePayment (CustomerID customer, String[] request) {
		//check if donor's account type is correct
		Customer donorCustomer  = customers.get(customer.getKey());
		
		if(!donorCustomer.checkAccountType(request[2])) {
			return "Entered <YourAccountType> " + request[2] + "is incorrect"+ "\n" + "You have following accounts" + "\n" + showMyAccounts(customer);
		}
		
		//check if the <Amount> entered in number
		double amountToTransfer;
		try {
			amountToTransfer =  Double.parseDouble(request[6]);	
			//check for negative numbers
			if(amountToTransfer <=0) {
				return "Please enter <Amount> greater than zero";
			}
			
		}catch (NumberFormatException e) {
			return "Please enter numbers only for <Amount>";
		}
		
		//check if the amount can be parsed as double & it is less than the amount in the balance
		double donorAccountBalance = donorCustomer.accountType(request[2]).getBalance(); 
		if(amountToTransfer > donorAccountBalance) {
			return "Entered transfered amount " + amountToTransfer +",  is greater than the balance in your selected account";
		}
		
		//check if the person/company to pay is in the bank database 
		Customer recepientCustomer;
		try {
			recepientCustomer = customers.get(request[4]);	
			if (recepientCustomer ==null) {
				return "Entered Customer: " + request[4] + " does not have an account in the bank.";
			}
		}catch(Exception e) {
			return "Entered Customer: " + request[4] + " does not have an account in the bank.";
		}
		
		//check if the account type of person/company to pay is in the database
		if(!recepientCustomer.checkAccountType(request[5])) {
			return "Entered <RecepientAccountType> " + request[5] + " is incorrect";
		}
		
		//if all the checks passed transfer the money
		donorCustomer.accountType(request[2]).changeBalance(-amountToTransfer);
		recepientCustomer.accountType(request[5]).changeBalance(amountToTransfer);
		
		//updating the table
		updateAccountBalance(donorCustomer, request[2], donorCustomer.accountType(request[2]).getBalance());
		updateAccountBalance(recepientCustomer, request[5], recepientCustomer.accountType(request[5]).getBalance());
		
		return "SUCCESS - " + "Your account balance:" + "\n" + showMyAccounts(customer);
	}
	
	
}
