package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/flight_management_database";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connection established!");

            } catch (ClassNotFoundException e) {
                System.out.println("Database connection failed!");
                System.err.println("JDBC Driver not found");
                e.printStackTrace();
            }
        }
        return connection;
    }
}