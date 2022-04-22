import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Main
 */
public class Main {
    static Scanner in;
    static Connection con;
    static String curUsername;

    public static void main(String[] args) {
        in = new Scanner(System.in);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "Pass@123");
            while (true) {
                System.out.println("1 - Login \n2 - Register \n3 - exit");
                int ch = in.nextInt();
                in.nextLine();
                switch (ch) {
                    case 1:
                        boolean loginResult = login();
                        if (loginResult == false) {
                            System.out.println("Incorrect information, try again");
                            continue;
                        }
                        System.out.println("Logged in successfully");
                        menu();
                        break;
                    case 2:
                        boolean regResult = register();
                        if (regResult == false) {
                            System.out.println("Unable to regiter, try again");
                        } else {
                            System.out.println("Registered in succesfully");
                        }
                        continue;
                    default:
                        con.close();
                        in.close();
                        System.exit(0);
                }
            }
        } catch (SQLException sqlException) {
            System.out.println("There is a problem with sql");
            System.out.println(sqlException.getMessage());
        } catch (ClassNotFoundException cnf) {
            System.out.println("Class not found");
        } catch (Exception e) {
            System.out.println("Unkown error");
        }
    }

    static boolean login() throws SQLException {
        System.out.println("Enter username");
        String username = in.nextLine();
        System.out.println("Enter password");
        String password = in.nextLine();
        PreparedStatement p = con.prepareStatement("SELECT * FROM bank WHERE username = ? AND password = ?");
        p.setString(1, username);
        p.setString(2, password);
        ResultSet rs = p.executeQuery();
        int count = 0;
        while (rs.next()) {
            count += 1;
        }
        if (count == 0) {
            return false;
        }
        curUsername = username;
        return true;
    }

    static boolean register() {
        System.out.println("Enter username");
        String username = in.nextLine();
        System.out.println("Enter password");
        String password = in.nextLine();
        System.out.println("Enter first name");
        String firstName = in.nextLine();
        System.out.println("Enter last name");
        String lastName = in.nextLine();
        System.out.println("Enter phone");
        String phoneNo = in.nextLine();
        boolean res = true;
        try {
            PreparedStatement p = con.prepareStatement("INSERT INTO BANK VALUES(?, ?, ?, ?, ?)");
            p.setString(1, username);
            p.setString(2, firstName);
            p.setString(3, lastName);
            p.setString(4, phoneNo);
            p.setString(5, password);
            p.execute();
        } catch (Exception e) {
            res = false;
        }
        return res;
    }

    static void menu() throws SQLException {
        while (true) {
            System.out.println(
                    "1 - Create bank account \n2 - Check balance \n3 - Set new balance \n4 - Transfer amount \n5 - exit");
            int ch = in.nextInt();
            in.nextLine();
            if (ch == 5) {
                return;
            }
            switch (ch) {
                case 1:
                    boolean createRes = createBankAccount();
                    if (createRes) {
                        System.out.println("Created bank account successfully");
                    } else {
                        System.out.println("Unable to create bank account, try again");
                    }
                    break;
                case 2:
                    System.out.println("Enter bank account");
                    String accountNo = in.nextLine();
                    int bal = checkBalance(accountNo);
                    System.out.println("Balance is: " + bal);
                    break;
                case 3:
                    boolean setRes = setBalance();
                    if (setRes) {
                        System.out.println("Set balance successfully");
                    } else {
                        System.out.println("Unable to set balance, try again");
                    }
                    break;
                case 4:
                    boolean transferRes = transferAmount();
                    if (transferRes) {
                        System.out.println("Transferred balance successfully");
                    } else {
                        System.out.println("Unable to transfer, try again");
                    }
                    break;
                default:
                    return;
            }
        }
    }

    static boolean createBankAccount() throws SQLException {
        System.out.println("Enter account number");
        String accountNo = in.nextLine();
        System.out.println("Enter IFSC code");
        String IFSC = in.nextLine();
        System.out.println("Enter initial balance");
        int initialBalance = in.nextInt();
        in.nextLine();
        boolean res = true;
        try {
            PreparedStatement p = con
                    .prepareStatement("INSERT INTO account (account_no, ifsc, balance, username) VALUES (?, ?, ?, ?)");
            p.setString(1, accountNo);
            p.setString(2, IFSC);
            p.setInt(3, initialBalance);
            p.setString(4, curUsername);
            p.execute();
        } catch (Exception e) {
            res = false;
        }
        return res;
    }

    static int checkBalance(String accountNo) throws SQLException {
        PreparedStatement p = con.prepareStatement("SELECT balance FROM account WHERE account_no = ?");
        p.setString(1, accountNo);
        ResultSet rs = p.executeQuery();
        rs.next();
        return rs.getInt(1);

    }

    static boolean setBalance() {
        System.out.println("Enter bank account");
        String accountNo = in.nextLine();
        System.out.println("Set new balance");
        int newBalance = in.nextInt();
        in.nextLine();
        boolean res = true;
        try {
            PreparedStatement p = con.prepareStatement("UPDATE account SET balance = ? WHERE account_no = ?");
            p.setInt(1, newBalance);
            p.setString(2, accountNo);
            p.execute();
        } catch (Exception e) {
            res = false;
        }
        return res;
    }

    static boolean transferAmount() throws SQLException {
        System.out.println("Enter bank account to transfer to");
        String transferAccount = in.nextLine();
        System.out.println("Enter your bank account");
        String curAccount = in.nextLine();
        System.out.println("Set amount to transfer");
        int amount = in.nextInt();
        int curAccountBalance = checkBalance(curAccount);
        int transferAccountBalance = checkBalance(transferAccount);
        if (curAccountBalance < amount) {
            return false;
        }
        boolean res = true;
        try {
            PreparedStatement p = con.prepareStatement("UPDATE account SET balance = ? WHERE account_no = ?");
            p.setInt(1, transferAccountBalance + amount);
            p.setString(2, transferAccount);
            p.execute();
        } catch (Exception e) {
            res = false;
        }
        return res;
    }
}