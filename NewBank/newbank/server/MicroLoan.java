package newbank.server;

public class MicroLoan {
	
	//properties
	private Integer principle; //The amount of the loan
	private Integer interestRate; //The annual interest rate
	private UserID lender;
	private UserID borrower;
	private boolean isWired;
	private boolean isReimbursed;
	
	//constructor
		
	public MicroLoan(Integer principle, Integer interestRate, UserID lender) {
		super();
		this.principle = principle;
		this.interestRate = interestRate;
		this.lender = lender;
	}
	
	
	//Getters and setters
	public Integer getPrinciple() {
		return principle;
	}
	
	public void setPrinciple(Integer principle) {
		this.principle = principle;
	}

	public Integer getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(Integer interestRate) {
		this.interestRate = interestRate;
	}

	public UserID getLender() {
		return lender;
	}
	
	public void setLender(UserID lender) {
		this.lender = lender;
	}
	
	public UserID getBorrower() {
		return borrower;
	}

	public void setBorrower(UserID borrower) {
		this.borrower = borrower;
	}
	
	
	public boolean isWired() {
		return isWired;
	}


	public void setWired(boolean isWired) {
		this.isWired = isWired;
	}


	public boolean isReimbursed() {
		return isReimbursed;
	}


	public void setReimbursed(boolean isReimbursed) {
		this.isReimbursed = isReimbursed;
	}


	public Integer calculateInterestDue() {
		Integer interestDue;
		interestDue =(int)Math.round(getInterestRate() * getPrinciple()/100);//rounding the amount to $
		interestDue = interestDue +1; //adding $1 for admin fee;
		return interestDue;
	}
	
	
	@Override
	public String toString() {
		if(this.borrower==null) {
			return "MicroLoan [principle=" + principle + ", interestRate=" + interestRate + ", lender=" + lender.getKey()
			+ ", borrower=" + borrower + ", isWired=" + isWired + ", isReimbursed="+ isReimbursed +"]";
		}else {
			return "MicroLoan [principle=" + principle + ", interestRate=" + interestRate + ", lender=" + lender.getKey()
			+ ", borrower=" + borrower.getKey() + ", isWired=" + isWired + ", isReimbursed="+ isReimbursed + "]";
		}
	}

	
}







