import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class tests {

    @Test void loginUser() {
        String[] credentials = {"1", "Mr.Hippo", "hippos"};
        User login = new User(credentials);
        Assertions.assertFalse(login == null);
    }

    @Test void AccountBalance() {
        //String[] credentials = {"100.00", "1", "checking"};
        Account Acc = new Account(100.00, 1, "checking");
        Assertions.assertFalse(Acc.getBalance() != 100);
    }

}
