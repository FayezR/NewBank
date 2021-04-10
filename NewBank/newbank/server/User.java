package newbank.server;

public class User {
    private String username;
    private String pass;

    public User (String username, String pass){
        this.username = username;
        this.pass = pass;
    }

    public void addAccount(Account account) {
    }

    public String accountsToString() {
        return null;
    }

    public String createMicroLoan(Integer principle, Integer interestRate, UserID lender) {
		MicroLoan ml = new MicroLoan(principle, interestRate, lender);
		MicroLoanMarket.microLoansAvailable.add(ml);
		return "Success: MicroLoan created: " + ml.toString();
    }

    public String getUsername(){
        return username;
    }

    public String getPass(){
        return pass;
    }
}

