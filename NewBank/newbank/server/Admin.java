package newbank.server;

import java.util.UUID;

public class Admin extends User{

    private String username;
    private String pass;


    public Admin(String username, String pass){
        super(username, pass);
    }

    public String getAccountName() {return username;}



}
