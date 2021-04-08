package newbank.server;

import java.util.ArrayList;

public class MicroLoanMarket {
	
	public static ArrayList<MicroLoan> microLoansAvailable = new ArrayList<>();

	
	public static String showMicroLoansAvailable() {
		String s = "";
		if(microLoansAvailable==null && microLoansAvailable.isEmpty()) {
			s = "There is no available MicroLoan to borrow at this moment.";
		}else {
			for(MicroLoan loan: microLoansAvailable) {
				s = s + loan.toString() + "\n";
			}
		}
		return s;
	}


	@Override
	public String toString() {
		return "MicroLoanMarket [getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}


	
	
	
	
	
}
