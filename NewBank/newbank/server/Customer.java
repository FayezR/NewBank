package newbank.server;

import java.util.ArrayList;

public class Customer {
	
	private ArrayList<Account> accounts;
	
	public Customer() {
		accounts = new ArrayList<>();
	}
	
	public String accountsToString() {
		String s = "";
		for(Account a : accounts) {
			s += a.toString();
		}
		return s;
	}

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
	
		
	public String createMicroLoan(Integer principle, Integer interestRate) {
		MicroLoan ml = new MicroLoan(principle, interestRate);
		MicroLoanMarket.microLoansAvailable.add(ml);
		return "Success: MicroLoan created: " + ml.toString() + MicroLoanMarket.showMicroLoansAvailable(); 
	}
		
		
		
		
}
