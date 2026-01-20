package hu_hospital.management.system.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database configuration and connection management for PostgreSQL
 */
public class DatabaseConfig {
    
    // Database connection parameters - UPDATE THESE TO MATCH YOUR SETUP
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "5432";
    private static final String DB_NAME = "HU_Hospital_Management_System"; // Change this to match your actual database name
    private static final String DB_USER = "postgres"; // Change to your PostgreSQL username
    private static final String DB_PASSWORD = "123321"; // Change to your PostgreSQL password
    
    private static final String DB_URL = "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
    
    private static Connection connection = null;
    
    /**
     * Get database connection
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load PostgreSQL JDBC driver
                Class.forName("org.postgresql.Driver");
                
                // Connection properties
                Properties props = new Properties();
                props.setProperty("user", DB_USER);
                props.setProperty("password", DB_PASSWORD);
                props.setProperty("ssl", "false");
                props.setProperty("autoReconnect", "true");
                
                // Create connection
                connection = DriverManager.getConnection(DB_URL, props);
                
                System.out.println("✅ Connected to PostgreSQL database: " + DB_NAME);
                
            } catch (ClassNotFoundException e) {
                throw new SQLException("PostgreSQL JDBC Driver not found. Add postgresql-xx.x.x.jar to classpath", e);
            } catch (SQLException e) {
                System.err.println("❌ Failed to connect to database: " + e.getMessage());
                System.err.println("💡 Make sure PostgreSQL is running and credentials are correct");
                throw e;
            }
        }
        
        return connection;
    }
    
    /**
     * Close database connection
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("🔌 Database connection closed");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Test database connection
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            boolean isValid = conn != null && !conn.isClosed();
            if (isValid) {
                System.out.println("✅ Database connection test successful");
            }
            return isValid;
        } catch (SQLException e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
            System.err.println("💡 Check if PostgreSQL is running and database exists");
            return false;
        }
    }
    
    /**
     * Get database configuration info
     */
    public static void printConnectionInfo() {
        System.out.println("📊 Database Configuration:");
        System.out.println("   Host: " + DB_HOST);
        System.out.println("   Port: " + DB_PORT);
        System.out.println("   Database: " + DB_NAME);
        System.out.println("   User: " + DB_USER);
        System.out.println("   URL: " + DB_URL);
    }
}