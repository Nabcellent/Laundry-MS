package sample.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySql {
    private static final int PORT = 3306;
    private static final String HOST = "localhost";
    private static final String USERNAME = "red";
    private static final String PASSWORD = "M1gu3l.!";
    private static final String DATABASE = "laundry";

    public static Connection dbConnect() {
        try {
            return DriverManager.getConnection(String.format("jdbc:mysql://%s:%d/%s", HOST, PORT, DATABASE), USERNAME, PASSWORD);
        } catch (SQLException ex) {
            Logger.getLogger(MySql.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
