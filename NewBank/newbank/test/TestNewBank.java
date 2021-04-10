package newbank.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import newbank.server.UserID;
import newbank.server.NewBank;

class TestNewBank {
	
	private NewBank bank;
	
	/*String menu = "Welcome to NewBank. The Disruptive Innovative Bank.\n"
	*/
	
	@Test
	//+ "1- Check your accounts balances.\n"
	void testCheckAccount1() {
		bank = NewBank.getBank();
		bank.checkLogInDetails("Bhagy", "12345");
		UserID customer = bank.getUserID("Bhagy");
		String [] request = new String [] {"1"};
		String output =  bank.processRequest(customer, request);
		//String expectedOutput = "SUCCESS - " + "Your account balance:" + "\n" + showMyAccounts(customer);

		assertEquals(output.contains("Main"), true); //show the account type
		assertEquals(output.contains("1000.0"), true); //show remaining balance
	}
	
	@Test
	//+ "2- Create new account.\n"
	void testCreateAccount2() {
		bank = NewBank.getBank();
		bank.checkLogInDetails("Bhagy", "12345");
		UserID customer = bank.getUserID("Bhagy");
		String [] request = new String [] {"NEWACCOUNT", "Savings"};
		String output =  bank.processRequest(customer, request);
		//show account balance now
		request = new String [] {"1"};
		output =  bank.processRequest(customer, request);

		assertEquals(output.contains("Savings"), true); //show the account type
		assertEquals(output.contains("0.0"), true); //show remaining balance
	}
	
	@Test
	//+ "3- Pay counterparties.\n"
	void testPayOthers3() {
		bank = NewBank.getBank();
		bank.checkLogInDetails("Bhagy", "12345");
		UserID customer = bank.getUserID("Bhagy");
		String [] request = new String [] {"PAY", "FROM", "Main", "TO", "John", "Main", "100"};
		String output =  bank.processRequest(customer, request);
		//String expectedOutput = "SUCCESS - " + "Your account balance:" + "\n" + showMyAccounts(customer);

		assertEquals(output.contains("SUCCESS"), true); // show success
		assertEquals(output.contains("Main"), true); //show the account type
		//assertEquals(output.contains("900"), true); //show remaining balance
	}
	
	@Test
	//+ "4- Open a MicroLoan Account.\n"
	//+ "5- Create a MicroLoan.\n"
	void testOpenMicroLoan45() {
		bank = NewBank.getBank();
		bank.checkLogInDetails("Bhagy", "12345");
		UserID customer = bank.getUserID("Bhagy");
		String [] request = new String [] {"4"};
		String output =  bank.processRequest(customer, request);
		request = new String [] {"PRINCIPLE", "100", "INTEREST", "RATE", "5"};
		output =  bank.processRequest(customer, request);
		//Expected Output Success: MicroLoan created: MicroLoan [principle=100, interestRate=5, lender=Bhagy, borrower=null]

		assertEquals(output.contains("Success: MicroLoan created:"), true); // show success
		assertEquals(output.contains("principle=100"), true); //show the account type
		assertEquals(output.contains("interestRate=5"), true); //show remaining balance
	}
	
	@Test
	//+ "6- Activate the MicroLoan (meaning putting the MicroLoan account in funds).\n"
	//+ "7- Show MicroLoans Available.\n"
	//+ "8- Acquire a Micro-Loan.\n"
	void testOpenMicroLoan678() {
		bank = NewBank.getBank();
		bank.checkLogInDetails("Bhagy", "12345");
		UserID customer = bank.getUserID("Bhagy");
		String [] request = new String [] {"4"};
		String output =  bank.processRequest(customer, request);
		request = new String [] {"PRINCIPLE", "100", "INTEREST", "RATE", "5"};
		output =  bank.processRequest(customer, request);


		
		request = new String [] {"PAY", "FROM", "Main", "TO", "Bhagy" , "MicroLoan", "100"};
		output =  bank.processRequest(customer, request);
		//Expected Output Success: MicroLoan created: MicroLoan [principle=100, interestRate=5, lender=Bhagy, borrower=null]

		//assertEquals(output.contains("Main: 900.0"), true); // show success
		//assertEquals(output.contains("MicroLoan: 100.0"), true); // show success

		//[MicroLoan [principle=100, interestRate=5, lender=Bhagy, borrower=null]]
		request = new String[] {"7"};
		output =  bank.processRequest(customer, request);		
		
		assertEquals(output.contains("[MicroLoan [principle=100, interestRate=5, lender=Bhagy, borrower=null]]"), true); //show remaining balance
		
		
	}
	
	
	@Test
	//+ "10-Transfer internally within accounts. \n"

	void testTransferInternally10() {
		bank = NewBank.getBank();
		bank.checkLogInDetails("Bhagy", "12345");
		UserID customer = bank.getUserID("Bhagy");
		String [] request = new String [] {"NEWACCOUNT", "Savings"};
		String output =  bank.processRequest(customer, request);
		request = new String[] {"TRANSFER", "FROM", "Main", "TO", "Savings", "100"};
		output =  bank.processRequest(customer, request);
		//Expected Output Success: MicroLoan created: MicroLoan [principle=100, interestRate=5, lender=Bhagy, borrower=null]

		assertEquals(output.contains("Savings: 100.0"), true); // show success

				
	}
	
	@Test
	//New Customer
	//Success - Account with username 'Abhi' 
	void testNewCustomer() {
		bank = NewBank.getBank();
		bank.checkLogInDetails("Admin", "12345");
		UserID customer = bank.getUserID("Admin");
		String[] request = {"NEWCUSTOMER", "Abhi"};
		String output =  bank.processAdminRequest(customer, request);
		//Expected Output Success: MicroLoan created: MicroLoan [principle=100, interestRate=5, lender=Bhagy, borrower=null]
	
		assertEquals(output.contains("Success - Account with username 'Abhi'"), true); // show success
	}
	
}
