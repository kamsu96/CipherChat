package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Łukasz Sus
 */
public class UserContext {

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.sqlite.JDBC";
    static String dbUrl = "jdbc:sqlite:chat.db";

    public UserContext(String dbUrl) {
        this.dbUrl = String.format("jdbc:sqlite:%s", dbUrl);
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean isLoginUnique(String login){
        try (Connection connection = DriverManager.getConnection(dbUrl))
        {
            PreparedStatement stmt = connection.prepareStatement("SELECT Login from Users WHERE Login = ?;");
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                return false;
            }
        }
        catch(SQLException e) {
            System.err.println(e.getMessage());
        }
        return true;
    }

    public boolean isPasswordCorrect(String login, String password){
        try (Connection connection = DriverManager.getConnection(dbUrl))
        {
            PreparedStatement stmt = connection.prepareStatement("SELECT Login from Users WHERE Login = ? AND Password = ?;");
            stmt.setString(1, login);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                return true;
            }
        }
        catch(SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public void addUser(String login, String password){
        try (Connection connection = DriverManager.getConnection(dbUrl))
        {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO Users (Login, Password) VALUES (?, ?)");
            stmt.setString(1, login);
            stmt.setString(2, password);
            stmt.execute();
        }
        catch(SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void removeAllUsers(){
        try (Connection connection = DriverManager.getConnection(dbUrl))
        {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM Users;");
            stmt.execute();
        }
        catch(SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public List<String> getAllUserLogins(){
        List<String> logins = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(dbUrl))
        {
            PreparedStatement stmt = connection.prepareStatement("SELECT Login from Users;");
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                String val = rs.getString(1);
                logins.add(val);
                System.out.println(val);
            }
        }
        catch(SQLException e) {
            System.err.println(e.getMessage());
        }
        return logins;
    }

    public List<String> getAllUserLogins(String phrase){
        List<String> logins = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(dbUrl))
        {
            phrase = new String("%").concat(phrase).concat(new String("%"));
            PreparedStatement stmt = connection.prepareStatement("SELECT Login FROM Users WHERE Login LIKE ?;");
            stmt.setString(1, phrase);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                String val = rs.getString(1);
                logins.add(val);
                System.out.println(val);
            }
        }
        catch(SQLException e) {
            System.err.println(e.getMessage());
        }
        return logins;
    }
}
