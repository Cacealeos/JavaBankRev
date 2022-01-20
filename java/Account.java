public class Account {

    private double balance;
    private int accountID;
    private String type;

    public Account (double balance, int ID, String type) {
        this.balance = balance;
        accountID = ID;
        this.type = type;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getAccountID() {
        return accountID;
    }

    public String getType() {
        return type;
    }
}
