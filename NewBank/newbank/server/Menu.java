package newbank.server;

public class Menu {
	
	
	public static String printMenu() {
		String menu = "Welcome to NewBank. The Disruptive Innovative Bank.\n"
				+ "Selection Menu:\n"
				+ "1- Check your accounts balances.\n"
				+ "2- Create new account.\n"
				+ "3- Pay counterparties.\n"
				+ "4- Open a MicroLoan Account.\n"
				+ "5- Create a MicroLoan.\n"
				+ "6- Activate the MicroLoan (meaning putting the MicroLoan account in funds).\n"
				+ "7- Show MicroLoans Available.\n"
				+ "8- Acquire a Micro-Loan.\n"
				+ "9- Show full transaction history.\n"
				+ "10- \n"
				+ "11- Transferring confirmed Microloan to borrower (for lender).\n"
				+ "12- Reimbursing Loan. \n"
				+ "(Select a number or type \"MENU\" to see the Menu again).\n"
				+ "13- Logout";
		return menu;
	}
	
	public static String printAdminMenu(){
		String menu = "Welcome admin!"
				+ "Selection Menu:\n"
				+ "1 - Add a new customer \n"
				+ "2 - Logout";

		return menu;
	}


}
