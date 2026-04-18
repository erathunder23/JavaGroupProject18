import java.sql.*;

public class DBConnection {
   
    private static final String DB_URL = "jdbc:mysql://localhost:3306/tecmis_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Apu1723";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}

