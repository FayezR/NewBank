package newbank.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import newbank.server.CustomerID;
import newbank.server.NewBank;

class TestNewBank {
	
	private NewBank bank;

	@Test
	void testPayOthers() {
		bank = NewBank.getBank();
		bank.checkLogInDetails("Bhagy", "12345");
		CustomerID customer = bank.getCustomerID("Bhagy");
		String [] request = new String [] {"PAY", "FROM", "Main", "TO", "John", "Checking", "100"};
		String output =  bank.processRequest(customer, request);
		//String expectedOutput = "SUCCESS - " + "Your account balance:" + "\n" + showMyAccounts(customer);

		assertEquals(output.contains("SUCCESS"), true); // show success
		assertEquals(output.contains("Main"), true); //show the account type
		assertEquals(output.contains("900"), true); //show remaining balance
	}

}
