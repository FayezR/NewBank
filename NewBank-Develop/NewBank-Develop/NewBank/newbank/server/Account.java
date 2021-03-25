package newbank.server;

public class Account {
	
	private String accountName;
	private double openingBalance;

	public Account(String accountName, double openingBalance) {
		this.accountName = accountName;
		this.openingBalance = openingBalance;
	}
	
	public String toString() {
		return (accountName + ": " + openingBalance + "\n");
	}
	
	//getter for AccountName
	public String getAccountName() {
		return accountName;
	}
	
	//getter for balance
	public double getBalance() {
		return openingBalance;
	}
	
	//adjust for balance
	public void changeBalance(double changeAmount) {
		openingBalance += changeAmount;
	}
}
