package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * @author : H V A S Udayanga
 * @index : AS2022359
 **/

public class DBConnection {
    private static DBConnection dbConnection;
    private Connection connection;

    private static final String URL = "jdbc:mysql://srv1289.hstgr.io:3306/u315264116_XYZ_Restaurant?useSSL=true&requireSSL=true&autoReconnect=true";
    private static final String USERNAME = "u315264116_shaveen";
    private static final String PASSWORD = "Shav@2001";

    private DBConnection(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL,USERNAME,PASSWORD);

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized DBConnection getInstance(){
        return (dbConnection == null) ? dbConnection = new DBConnection() : dbConnection;
    }

    public synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                reconnect();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    private void reconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to reconnect to the database", e);
        }
    }
}
