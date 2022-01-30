import org.postgresql.util.PSQLException;

import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class PostGresConnection {

    Properties props = new Properties();
    //props.load(new FileReader"");


    private static final String endpoint = "revaturedb.cefq4dtr6ufo.us-east-1.rds.amazonaws.com";
    ///jdbc:postgresql://<endpoint>/<database>
    //private static final String url = "jdbc:postgresql://localhost:5432/JavaBank";
    private static final String url = "jdbc:postgresql://" + endpoint + "/JavaBankAWS";
    private static final String user = "";
    private static final String passwd = "";

    private static java.sql.Connection conn;

    static {
        try {
            conn = DriverManager.getConnection(url, user, passwd);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addUser(String Name, String password) {

        String sql = "INSERT INTO users (passwd, name) VALUES (?,?)";

        try {
            PreparedStatement CreateUser = this.conn.prepareStatement(sql);

            CreateUser.setString(1, password);
            CreateUser.setString(2, Name);
            CreateUser.executeQuery();

        } catch (PSQLException e) {
            //e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUser(String Name, String password) {

        String sql = "SELECT userid FROM users WHERE name = ? AND passwd = ?";

        try {
            PreparedStatement fetchUser = this.conn.prepareStatement(sql);

            fetchUser.setString(1, Name);
            fetchUser.setString(2, password);

            ResultSet rs = fetchUser.executeQuery();
            rs.next();
            //gets first match
            if(rs.getInt("UserID")==0)
                return null;

            String[] Results = {rs.getString("UserID"), password, Name};
            User login = new User(Results);
            login.setAccounts(getAccounts(rs.getInt("UserID")));

            return login;

        } catch (SQLException e) {
            e.printStackTrace();
        }
            return null;
        }

    public void updateBalance (double amount, int ID) {
        String sql = "UPDATE accounts SET balance = ? WHERE accountid = ?";

        try {
            PreparedStatement update = this.conn.prepareStatement(sql);

            update.setDouble(1,amount);
            update.setInt(2,ID);

            update.executeQuery();

        } catch (PSQLException e) {
            //e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void transferFund (double amount, int recipent, int donor) {



    }

    public void makeAccount(int id, String type) {

        String sql = "INSERT INTO accounts (balance, acctype) VALUES (0.00, ?) RETURNING accountid";
        String junctionSql = "INSERT INTO useraccountjunction (userid, accountid) VALUES (?,?)";

        try {
            PreparedStatement makeAccount = this.conn.prepareStatement(sql);
            makeAccount.setString(1, type);
            ResultSet rs = makeAccount.executeQuery();

            rs.next();
            //adds new account to list of accounts
            //login.setAccountIDs(rs.getInt("accountid"));

            makeAccount = this.conn.prepareStatement(junctionSql);
            makeAccount.setInt(1, id);
            makeAccount.setInt(2, rs.getInt(1));
            makeAccount.executeQuery();

        } catch (PSQLException e) {
            //e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void changeLogin (User login) {

    }

    public Account[] getAccounts (int userID){

        String sql = "SELECT accountid FROM useraccountjunction WHERE userid = ?";

        try {
            PreparedStatement getAccountID = this.conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
            getAccountID.setInt(1,userID);
            ResultSet rs = getAccountID.executeQuery();

            //moves to last row
            rs.last();
            //gets number of rows
            int i = rs.getRow();
            if(i==0)
            return null;
            //initialize array size
            int[] size = new int[i];
            //brings query back to beginning
            rs.beforeFirst();

            int x =0;
            while(rs.next()){
                size[x] = rs.getInt("accountid");
                        x++;
            }

            //sets up new query to retrieve data about each account
            //since we can't know how many arguments to structure the query with its WHERE clause,
            //the queries have to be made one at a time with a loop
            sql = "SELECT * FROM accounts WHERE accountid = ?";
            Account[] accounts = new Account[size.length];
            x =0;

            for(int id : size){
                getAccountID = this.conn.prepareStatement(sql);
                getAccountID.setInt(1, id);
                rs = getAccountID.executeQuery();
                rs.next();

                accounts[x] = new Account(rs.getDouble("balance"), id, rs.getString("acctype"));
                x++;
            }
            return accounts;

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
