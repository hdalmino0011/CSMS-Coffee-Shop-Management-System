package coffeeshop;

import java.sql.*;
import javax.swing.*;

public class DBConnection {
    private static Connection connection = null;
    
    // Database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/hanz?autoReconnect=true&useSSL=false&serverTimezone=Asia/Manila";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Babylovelovecherry123**";
    
    public static Connection getConnection() {
        try {
            // Check if connection is null, closed, or invalid
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Database connected successfully!");
            }
            return connection;
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, 
                "MySQL JDBC Driver not found!\nPlease add mysql-connector jar file.",
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Database connection failed!\nError: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        }
    }
    
    // Method to reset connection
    public static void resetConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection = null;
        getConnection(); // Re-establish connection
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Test connection method
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed() && conn.isValid(2);
        } catch (Exception e) {
            return false;
        }
    }
}

