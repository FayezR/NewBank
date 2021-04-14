package newbank.server;

import java.util.ArrayList;

public class MicroLoanMarket {
	
	public static ArrayList<MicroLoan> microLoansAvailable = new ArrayList<>();

	
	public static String showMicroLoansAvailable() {
		if(microLoansAvailable==null && microLoansAvailable.isEmpty()) {
			return "There is no available MicroLoan to borrow at this moment.";
		}else {
			return microLoansAvailable.toString();
		}
		
	}


	@Override
	public String toString() {
		return "MicroLoanMarket [getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}


	
	
	
	
	
}
