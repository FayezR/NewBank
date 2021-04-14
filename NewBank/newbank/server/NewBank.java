package newbank.server;

import java.util.HashMap;
import java.util.Map;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	
	private HashMap<String,User> users;
	
	private String tempLoanAmount="0"; 
	
	private Connection connection;
	private static final String updateAllAccountInfo = "INSERT INTO account_info(id, username, name, main, savings, checking, microloan) VALUES(?, ?, ?, ?, ?, ?, ?)";


	private NewBank() {
		users = new HashMap<>();
		connectDB();
		addTestData();
	}
		
	private void addTestData() {
		Customer bhagy = new Customer("Bhagy", "12345");
		bhagy.addAccount(new Account("Main", 1000.0));
		users.put("Bhagy", bhagy);
		updateAccountInfo("Bhagy", "Bhagy", "Bhagy", String.valueOf(1000.0), "-", "-", "-");
		
		Customer christina = new Customer("Christina", "12345");
		christina.addAccount(new Account("Main", 1500.0));
		users.put("Christina", christina);
		updateAccountInfo("Christina", "Christina", "Christina", String.valueOf(1500.0), "-", "-", "-");
		
		Customer john = new Customer("John", "12345");
		john.addAccount(new Account("Main", 250.0));
		users.put("John", john);
		updateAccountInfo("John", "John", "John", String.valueOf(250.0), "-", "-", "-");

		Admin admin = new Admin("Admin", "12345");
		users.put("Admin", admin);
	}


	public static NewBank getBank() {
		return bank;
	}

	public synchronized Boolean checkLogInDetails(String userName, String password) {
		if (users.containsKey(userName)){ //checks if username exists
			User user = users.get(userName);
			if (user.getPass().equals(password)){ //checks if password is correct - FR
				 UserID cID = new UserID(userName);
				 return true;
			}
			else {return false;}
		}
		return false;
	}

	public synchronized UserID getUserID (String userName){
		UserID uID = new UserID(userName);
		return uID;
	}
	//Check if user is a customer - FR
	public synchronized Boolean isCustomer (UserID user){ return users.get(user.getKey()).getClass() == newbank.server.Customer.class;}

	//Checks if user is an admin - FR
	public synchronized Boolean isAdmin (UserID user){ return users.get(user.getKey()).getClass() == newbank.server.Admin.class;}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(UserID customer, String [] request) {
		if(users.containsKey(customer.getKey())) {

			switch(request [0]) {
			
			//Show the Menu at request (RT)
			case "MENU" : return Menu.printMenu();
			//Showing the accounts
			case "1" : return showMyAccounts(customer);
			//Newaccount functionality added by Fayez FR
			case "2" :
				case "NEWACCOUNT" :
					try { return newAccount(customer, request[1]);}
						catch (ArrayIndexOutOfBoundsException e) {return "Please enter the NEWACCOUNT command in the form: NEWACCOUNT <name>.\n";}
			//External Money Transfer FR1.5 Added by Abhinav
			case "3" :
				case "PAY" : return payOthers(customer, request);
			//Adding MicroLoan functionality (added by Raymond (RT))
			//Create a MicroLoan account
			case "4":  try { return openMicroLoanAccount(customer);}
			//error is caught if user doesn't specify a name for the new account
			catch (ArrayIndexOutOfBoundsException e) {return "Please enter the OpenMicroLoanAccount command in the form: OpenMicroLoanAccount.\n";}
			//Create a MicroLoan A/C
			case "OpenMicroLoanAccount":  try { return openMicroLoanAccount(customer);}
			//error is caught if user doesn't specify a name for the new account
			catch (ArrayIndexOutOfBoundsException e) {return "Please enter the OpenMicroLoanAccount command in the form: OpenMicroLoanAccount.\n";}
			
			case "5": return "To create a MicroLoan, please enter command in the form:\n "
						+ "PRINCIPLE <amount> INTEREST RATE <amount> \n";
			case "PRINCIPLE": try {
				tempLoanAmount=request[1];
				return users.get(customer.getKey()).createMicroLoan(Integer.parseInt(request[1]), Integer.parseInt(request[4]),customer) ;
				
				
			}catch(ArrayIndexOutOfBoundsException e) {return "To create a MicroLoan, please enter command in the form: \n "
					+ "PRINCIPLE <amount> INTEREST RATE <amount> \n";
			}

			case "6":
			String[] request1 = {"PAY", "FROM", "Main", "TO", customer.getKey() , "MicroLoan",tempLoanAmount};
			return payOthers(customer, request1);

			case "7": try{return "Following MicroLoans are available to take:\n"+ MicroLoanMarket.showMicroLoansAvailable() +"\n";
			}catch(ArrayIndexOutOfBoundsException e) {
				return "No available MicroLoans at this Moment";
			}
			
			
			
			case "8": return "To take up a MicroLoan, please enter command in the form:\n"
			 + "CONFIRMING TAKING UP THE LOAN <The number of the loan starting by counting 0>";
			case "CONFIRMING": try {
				Integer loanNumber = Integer.parseInt(request[5]);
				//update the requested loan with the borrower name
				MicroLoanMarket.microLoansAvailable.get(loanNumber).setBorrower(customer);
					return "The loan is confirmed to you (borrower's field updated), the lender shall initiate the payment:\n"+  
				MicroLoanMarket.microLoansAvailable.get(loanNumber).toString() + "\n";
				}catch(ArrayIndexOutOfBoundsException e) {
					return "To take up a MicroLoan, please enter command in the form:\n"
					 + "CONFIRMING TAKING UP THE LOAN <The number of the loan starting by counting 0>";
				}
			
			
			case "9": return  showTransactionHistory((Customer) users.get(customer.getKey()));


			//Internal Money Transfer FR1.5 Added by Abhinav
			case "10" : return paySelf(customer, request);
			case "TRANSFER" : return paySelf(customer, request);

			
			//To transfer the loan from MicroLoan a/c to MicroLoan a/c. (RT)
			case "11": return "To tranfer your MicroLoan to borrower, please enter command in the form:\n"
			 + "TRANSFERRING LOAN <The number of the loan starting by counting 0>";
			case "TRANSFERRING": try {
				Integer loanNumber = Integer.parseInt(request[2]);
				UserID borrower = MicroLoanMarket.microLoansAvailable.get(loanNumber).getBorrower();
				String loanAmount = MicroLoanMarket.microLoansAvailable.get(loanNumber).getPrinciple().toString();
				String[] payInstructions = {"PAY", "FROM", "MicroLoan", "TO", borrower.getKey() , "MicroLoan", loanAmount};	
				MicroLoanMarket.microLoansAvailable.get(loanNumber).setWired(true);
				return payOthers(customer, payInstructions);
			}catch(ArrayIndexOutOfBoundsException e) {
				 return "To tranfer your MicroLoan to borrower, please enter command in the form:\n"
						 + "TRANSFER LOAN <The number of the loan starting by counting 0>";
			}
			
			//To reimburse loan with interest from Main a/c to Main a/c. (RT)
			case "12": return "to reimburse your MicroLoan, please enter the command in the form:\n"
			+ "REIMBURSE LOAN <The number of the loan starting by counting 0>";
			case "REIMBURSE": try {
				Integer loanNumber = Integer.parseInt(request[2]);
				UserID lender = MicroLoanMarket.microLoansAvailable.get(loanNumber).getLender();
				int loanAmount = MicroLoanMarket.microLoansAvailable.get(loanNumber).getPrinciple();
				int interestDue = MicroLoanMarket.microLoansAvailable.get(loanNumber).calculateInterestDue();
				int reimbursementAmount = loanAmount + interestDue;
				String totalDue = String.valueOf(reimbursementAmount);
				String[] payInstructions = {"PAY", "FROM", "Main", "TO", lender.getKey().toString() , "Main", totalDue.toString()};
				MicroLoanMarket.microLoansAvailable.get(loanNumber).setReimbursed(true);
				return payOthers(customer, payInstructions);
			}catch(ArrayIndexOutOfBoundsException e) {
				return "to reimburse your MicroLoan, please enter the command in the form:\n"
				+ "REIMBURSE LOAN <The number of the loan starting by counting 0>";
			}
			
			
			case "13" : return "Logout";
			case "LOGOUT" : return "Logout";


			default : return "FAIL - Please enter a number from the Menu or Type 'Menu' to see the Menu again.\n";
			}
		}
		return "FAIL";
	}

	public synchronized String processAdminRequest(UserID admin, String [] request) {
		if (users.containsKey(admin.getKey())) {
			switch (request[0]) {

				case "MENU":
					return Menu.printAdminMenu();
				case "1" : return "Please enter the NEWCUSTOMER command in the form: NEWCUSTOMER <name>.\n";
				case "NEWCUSTOMER": try{ return AddNewCustomer(request[1]);} // -FR
									catch (ArrayIndexOutOfBoundsException e) {return "Please enter the NEWCUSTOMER command in the form: NEWCUSTOMER <name>.\n";}

				case "2" : return "Logout";
				case "LOGOUT" : return "Logout";
				default: return "FAIL - Please enter a number from the Menu or Type 'Menu' to see the Menu again.\n";
			}
		}
		return "FAIL";
	}

	private String AddNewCustomer(String username){
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder b = new StringBuilder();
		Random rnd = new Random();
		while (b.length() < 7) { // length of the random string.
				int index = (int) (rnd.nextFloat() * chars.length());
				b.append(chars.charAt(index));
			}
			String tempPass = b.toString();
		Customer customer = new Customer(username, tempPass);
		customer.addAccount(new Account("Main", 00.0));
		users.put(username, customer);
		return "Success - Account with username '" + username + "' and temporary password '" + tempPass + "' has been created!";
	}

	private String showMyAccounts(UserID customer) {
		return "Available accounts:\n" + (users.get(customer.getKey())).accountsToString();
	}

	private String newAccount (UserID customer, String name) {
		String notes = name + " account added";
		users.get(customer.getKey()).addAccount(new Account (name, 0.00));
		addNewAccount((Customer) users.get(customer.getKey()), name);
		updateTransactionHistory((Customer) users.get(customer.getKey()), "new account added", true, notes);
		return "SUCCESS- New account created.\n";
	}
	
	//Method when "PAY" Keyword is used
	private String payOthers (UserID customer, String[] request) {
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
	
	//Method when "item-9 pay self is selected" Keyword is used
	private String paySelf (UserID customer, String[] request) {
		if(request.length==1) { //only TRANSFER mentioned
			return "You have following accounts" + "\n" + showMyAccounts(customer)+ "Please select the account type for payment in the form:" +
				"TRANSFER FROM <YourAccountType> TO <RecepientAccountType> <Amount>";
		} else if (request.length==6 && request[1].equals("FROM") && request[3].equals("TO")) { //if the length of request matches the format
			String [] requestUpdated = new String[] {"PAY", request[1], request[2], request[3], customer.getKey(), request[4], request[5] };
			return makePayment(customer, requestUpdated);			
		} else { // for all other cases
			return "You have following accounts" + "\n" + showMyAccounts(customer)+ "Please select the account type for payment in the form:" +
				   "TRANSFER FROM <AccountType> TO <RecepientAccountType>  <Amount>";
		}
		
	}
	
	//Method when "PAY FROM <AccountType> TO <Person/Company> <RecepientAccountType> <Amount>" is used
	private String makePayment (UserID customer, String[] request) {
		//check if donor's account type is correct

		Customer donorCustomer  = (Customer) users.get(customer.getKey());
		String notes;
		
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
			recepientCustomer = (Customer) users.get(request[4]);
			if (recepientCustomer ==null) {
				notes = "Recipient does not have an account in the bank";
				updateTransactionHistory(donorCustomer, "pay to others", false, notes);
				return "Entered Customer: " + request[4] + " does not have an account in the bank.";
			}
		}catch(Exception e) {
			return "Entered Customer: " + request[4] + " does not have an account in the bank.";
		}
		
		//check if the account type of person/company to pay is in the database
		if(!recepientCustomer.checkAccountType(request[5])) {
			notes = "Recipient does not have the account indicated in transaction";
			updateTransactionHistory(donorCustomer, "pay to others", false, notes);
			return "Entered <RecepientAccountType> " + request[5] + " is incorrect";
		}
		
		//if all the checks passed transfer the money
		donorCustomer.accountType(request[2]).changeBalance(-amountToTransfer);
		recepientCustomer.accountType(request[5]).changeBalance(amountToTransfer);
		updateAccountBalance(donorCustomer, request[2], donorCustomer.accountType(request[2]).getBalance());
		updateAccountBalance(recepientCustomer, request[5], recepientCustomer.accountType(request[5]).getBalance());
		notes = "Transferred " + getKeyFromValue(users, recepientCustomer) + " " + amountToTransfer;

		updateTransactionHistory(donorCustomer, "pay to others", true, notes);
		return "SUCCESS - " + "Your account balance:" + "\n" + showMyAccounts(customer);
	}

	//Methods related to MicroLoan

	private String openMicroLoanAccount (UserID customer) {
		String name="MicroLoan";
		String notes = "Microloan account added";
		users.get(customer.getKey()).addAccount(new Account (name, 0.00));
		addNewAccount((Customer) users.get(customer.getKey()), name);
		updateTransactionHistory((Customer) users.get(customer.getKey()), "new account added", true, notes);
		return "SUCCESS- New MicroLoan account created.\n";
	}
	
	
	//all transactions
	private String showTransactionHistory(Customer customer)
	{
	
		String query = "SELECT date, type, result, notes FROM transactions WHERE username = '"+getKeyFromValue(users, customer)+"';";
		String transactions = "Your transaction history: \n";
		Statement statement;

		ResultSet rs;
		try 
		{	
			statement = connection.createStatement();
			rs = statement.executeQuery(query);
			
			while (rs.next()) 
			{
				String transaction = rs.getString("date") + " " + rs.getString("type") + " " + rs.getString("result") + " " + rs.getString("notes");
				transactions = transactions + transaction + "\n";
			}
			return transactions;
		} 
		catch (SQLException e)
		{
			
			e.printStackTrace();
		}
		
		return "Fail";
	}
	
	
		
	//Database methods
	public static <Key, Value> Key getKeyFromValue(Map<Key, User> map, Value value)
	{
		for (Map.Entry<Key, User> entry : map.entrySet()) {
			if (value.equals(entry.getValue())) {
				return entry.getKey();
				}
	        }
	    return null;
	}
	
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
	                + "	main text,\n"
	                + "	savings text,\n"
	                + "	checking text,\n"
	                + "	microloan text\n"
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
	                + "	username text NOT NULL,\n"
	                + "	day text NOT NULL,\n"
	                + "	month text NOT NULL,\n"
	                + "	year text NOT NULL,\n"
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
	
	//to be added
	private void loadAccountBalancesFromDatabase(HashMap<String,User> customers)
	{
		
		
	}
	
	private void updateTransactionHistory(Customer customer, String transactionType, boolean result, String notes)
	{
		PreparedStatement ps;
		String statement = "INSERT INTO transactions (username, day, month, year, date, type, result, notes) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
		
		String outcome = "FAILURE";
		if(result = true)
		{
			outcome ="SUCCESS";
		}
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		DateTimeFormatter day = DateTimeFormatter.ofPattern("dd"); 
		DateTimeFormatter month = DateTimeFormatter.ofPattern("MM");  
		DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy");  
		LocalDateTime now = LocalDateTime.now();  
		
		try
		{
			ps = connection.prepareStatement(statement);
		    ps.setString(1, getKeyFromValue(users, customer));
		    ps.setString(2, day.format(now));
		    ps.setString(3, month.format(now));
		    ps.setString(4, year.format(now));
		    ps.setString(5, dtf.format(now));
		    ps.setString(6, transactionType);
		    ps.setString(7, outcome);
		    ps.setString(8, notes);
		    ps.executeUpdate();
		    System.out.println("Transactions updated!");
		}
		catch (Exception e)
		{
			System.out.println("Transactions couldn't be updated");
			e.printStackTrace();
		}
	}
	
	public void updateAccountInfo(String id, String username, String name, String main, String savings, String checking, String microloan)
	{
		PreparedStatement ps;
		try 
		{
			ps = this.connection.prepareStatement(updateAllAccountInfo);
			ps.setString(1, id);
	        ps.setString(2, username);
	        ps.setString(3, name);
	        ps.setString(4, main);
	        ps.setString(5, savings);
	        ps.setString(6, checking);
	        ps.setString(7, microloan);
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
			    ps.setString(2, getKeyFromValue(users, customer));
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
			    ps.setString(2, getKeyFromValue(users, customer));
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
			    ps.setString(2, getKeyFromValue(users, customer));
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
		
	public void addNewAccount (Customer customer, String accountType)
	{
		PreparedStatement ps;
		String statement;
		String amount = "0.0";
		switch(accountType) 
		{
			case "Savings":
				statement = "UPDATE account_info SET savings=? WHERE username = ?";
				try
				{
					ps = connection.prepareStatement(statement);
				    ps.setString(1, amount);
				    ps.setString(2, getKeyFromValue(users, customer));
				    ps.executeUpdate();
				}
				catch (Exception e)
				{
					System.out.println("Savings couldn't be added");
					e.printStackTrace();
				}
				break;
			case "Checking":
				statement = "UPDATE account_info SET checking=? WHERE username = ?";
				try
				{
					ps = connection.prepareStatement(statement);
				    ps.setString(1, amount);
				    ps.setString(2, getKeyFromValue(users, customer));
				    ps.executeUpdate();
				}
				catch (Exception e)
				{
					System.out.println("Checking couldn't be added");
					e.printStackTrace();
				}
				break;
			case "Microloan":
				statement = "UPDATE account_info SET microloan=? WHERE username = ?";
				try
				{
					ps = connection.prepareStatement(statement);
				    ps.setString(1, amount);
				    ps.setString(2, getKeyFromValue(users, customer));
				    ps.executeUpdate();
				}
				catch (Exception e)
				{
					System.out.println("Microloan couldn't be added");
					e.printStackTrace();
				}
				break;
			default:
				break;
		}
	}
		
		
		

}
