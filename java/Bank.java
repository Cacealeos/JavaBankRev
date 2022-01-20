

import java.text.DecimalFormat;
import java.util.Scanner;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class Bank {

    static PostGresConnection PGC = new PostGresConnection();

    public static void main(String[] args)  {

        Scanner input = new Scanner(System.in);
        String passwd, UserName;
        String[] credentials = null;

        System.out.println("Welcome to the Bank! Enter your UserID to login or hit Enter to Create an account:");
        UserName = input.nextLine();
//checks if user exists
        if(UserName.isEmpty())
            UserName = createUser(input);

        System.out.println("Please Enter Password:");
        passwd = input.nextLine();
//authenticates users
        //fetches userID of new created or logged in user
        User login = PGC.getUser(UserName, passwd);

        if(login==null)
            for(int i = 0; i < 3; i++) {
                if (i == 3) {
                    System.out.println("Failure. Login limit exceeded...exiting:");
                    return;
                }
            System.out.println("Incorrect Password...please try again:");
            passwd = input.nextLine();
            //fetches userID of new created or logged in user
                login = PGC.getUser(UserName, passwd);
            }

//starts dashboard
        //after loggin in, the rest of the program takes place in the dashboard. This allow primarily consists of
        //cycling though display acounts which then links to view accounts which will
        // trigger endlessly with bad input/cancel operations.
        //Finally, once log out is selected, the dashboard will terminate
        System.out.println("\nHello " + login.getName() + ".\n");

        dashBoard (login, input);

        input.close();
        System.out.println("Goodbye " + login.getName() + ".");

    }

    public static String createUser (Scanner input) {
        System.out.println("Create a UserID:");
        String UserID = input.nextLine();
        String passwd = "";
        String cPass = "";

        while (true) {

            System.out.println("Now add a password:");
            passwd = input.nextLine();
            System.out.println("Confirm password:");
            cPass = input.nextLine();

            if(!cPass.equals(passwd) || cPass == "" || passwd == "")
                System.out.println("Passwords must match and cannot be blank");
            else
                break;
        } ;

        PGC.addUser(UserID, passwd);
        //write to file with scanner

        System.out.println("User Account has been created...");

        return UserID;

    }

    public static void createAccount (User login, Scanner input) {

        int aType;
        boolean loop = true;

        while (loop) {
            System.out.println("Specify account type:\n" +
                    "#1: Checking\n" +
                    "#2: Saving\n" +
                    "#3: Cancel");

            aType = input.nextInt();

            switch (aType) {
                case 1:
                    System.out.println("Checking Created...\n");
                    PGC.makeAccount(login.getUserID(), "savings");
                    loop = false;
                    break;
                case 2:
                    System.out.println("Savings Created...\n");
                    PGC.makeAccount(login.getUserID(), "checking");
                    loop = false;
                    break;
                case 3:
                    loop = false;
                    break;
                default:
                    System.out.println("Please select valid input");
            }
        }

    }

    public static void displayAccounts (User login, Scanner input){

        // takes data from user to display accounts before showing view account
        try {
            if( login.getAccounts() == null || login == null) {
                System.out.println("No accounts found. Add an account.");
                return;
            }

            for (Account A : login.getAccounts()) {
                System.out.println("============\n" +
                        "ID: " + A.getAccountID() + "||" +
                        "Balance: " + (A.getBalance()%.1 != 0 ? A.getBalance() : A.getBalance() + "0") + "||" +
                        "Type: " + A.getType());
            }

            System.out.println("Select an account ID");

            String test = input.nextLine();

//            if(test.isEmpty())
//                return;

            int ID = parseInt(test);
            boolean validID = false;
            Account selected = null;

            for (Account A : login.getAccounts()) {
                if (A.getAccountID() == ID) {
                    validID = true;
                    selected = A;
                }

            }

            if (validID) {
                viewAccount(login, selected, input, ID);
            } else {
                System.out.println("ID not found");
                displayAccounts(login, input);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public static void viewAccount (User login, Account selected, Scanner input, int ID) throws InterruptedException {

        System.out.println("" +
                "#1: Deposit\n" +
                "#2: Withdraw\n" +
                "#3: Transfer\n" +
                "#4: Cancel");

        int option = input.nextInt();
        String pattern = "#.##";
        DecimalFormat decimalFormat =  new DecimalFormat(pattern);
        String amount;
        String formattedInput;
        input.nextLine();

        switch (option){
            case 1: //Deposits
                System.out.print("Enter dollar amount:");
                amount = input.nextLine();
                formattedInput = decimalFormat.format(parseDouble(amount));
                selected.setBalance(selected.getBalance() + parseDouble(formattedInput));
                System.out.println("Processed");
                //updates data
                PGC.updateBalance(selected.getBalance(), ID);
                // fetches data again

                Thread.sleep(2000);
                //display accounts again
                break;
            case 2: //Withdraws
                System.out.print("Enter dollar amount:");
                amount = input.nextLine();
                formattedInput = decimalFormat.format(parseDouble(amount));
                if(selected.getBalance() < parseDouble(formattedInput))
                    System.out.println("Insufficient funds.");
                else {
                    selected.setBalance(selected.getBalance() - parseDouble(formattedInput));
                    System.out.println("Processed");
                    //updates data
                    PGC.updateBalance(selected.getBalance(), ID);
                    // fetches data again
                    Thread.sleep(2000);
                //display accounts again
                }
                break;
            case 3: //Transfers
                System.out.print("Enter dollar amount:");
                amount = input.nextLine();
                formattedInput = decimalFormat.format(parseDouble(amount));
                if(selected.getBalance() < parseDouble(formattedInput))
                    System.out.println("Insufficient funds.");
                else {
                    selected.setBalance(selected.getBalance() - parseDouble(formattedInput));
                    System.out.println("Select recipent");
                    System.out.println("Processed");
                    Thread.sleep(2000);
                }
                    break;
            case 4: //Cancel
                System.out.println("");
                break;
            default:
                System.out.println("Invalid option");
                viewAccount(login, selected, input, ID);
                break;
        }

    }

    public static void dashBoard (User login, Scanner input) {

        System.out.println("Please select a function(1-5):\n" +
                "#1: View Accounts\n" +
                "#2: Make Account\n" +
                "#3: Share Account\n" +
                "#4: Change login\n" +
                "#5: Log out\n");

        int action = input.nextInt();
        input.nextLine();

        switch (action) {
            case 1:
                login.setAccounts(PGC.getAccounts(login.getUserID()));
                displayAccounts(login, input);
                dashBoard(login, input);
                break;
            case 2:
                createAccount(login, input);
                dashBoard(login, input);
                break;
            case 3:
                System.out.println("Not available...");
                dashBoard(login, input);
            case 4:
                System.out.println("Not available...");
                dashBoard(login, input);
                break;
            case 5:

                break;
            default:
                System.out.println("Please choose a valid option...");
                dashBoard(login, input);
        }
    }


}

