package newbank.server;

import java.util.ArrayList;

public class Customer extends User {
	
	private ArrayList<Account> accounts;
	
	public Customer(String username, String pass) {
		super(username, pass);
		accounts = new ArrayList<>();
	}
	
	public String accountsToString() {
		String s = "";
		for(Account a : accounts) {
			s += a.toString();
		}
		return s;
	}

	@Override
	public void addAccount(Account account) {
		accounts.add(account);		
	}

	//check if the type of account exist
	public boolean checkAccountType(String accountName) {
		for(Account account: accounts) {
			if(account.getAccountName().equals(accountName)) {
				return true;
			}
		}
		return false;
	}
	
	//return the account name
		public Account accountType(String accountName) {
			for(Account account: accounts) {
				if(account.getAccountName().equals(accountName)) {
					return account;
				}
			}
			return null;
		}
	
		
	public String createMicroLoan(Integer principle, Integer interestRate, UserID lender) {
		MicroLoan ml = new MicroLoan(principle, interestRate, lender);
		MicroLoanMarket.microLoansAvailable.add(ml);
		return "Success: MicroLoan created: " + ml.toString();
	}

	@Override
	public String toString() {
		return "Customer [accounts=" + accounts + ", accountsToString()=" + accountsToString() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
		
		
	
		
	
		
}
