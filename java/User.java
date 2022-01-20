import static java.lang.Integer.parseInt;

public class User {

    private int UserID;
    private String Name;
    private String password;
    private Account[] accounts;

    public User (String[] credentials){
        UserID = parseInt(credentials[0]);
        Name = credentials[1];
        password = credentials[2];

    }

    public String getName() {
        return Name;
    }

    public int getUserID() {
        return UserID;
    }

    public Account[] getAccounts() {
        return accounts;
    }

    public void setAccounts(Account[] accounts) {

        this.accounts = accounts;
    }


}
