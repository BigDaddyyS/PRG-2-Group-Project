package greenpasteures.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    // database connection details
    private static final String DB_URL  = "jdbc:mysql://localhost:3306/green_pastures_farm";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root123";

    // singleton instance - only one connection exists at a time
    private static DatabaseManager instance;
    private Connection connection;

    // private constructor - cannot be called from outside
    private DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Database connected successfully.");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver not found. Add mysql-connector-j.jar to your project.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // this method returns the single instance of DatabaseManager
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // this method returns the connection to use in DAO classes
    public Connection getConnection() {
        return connection;
    }

    // this method checks if we are still connected
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // this static method is used by the reports package
    public static Connection getStaticConnection() throws Exception {
        return getInstance().getConnection();
    }
}