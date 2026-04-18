import java.sql.*;

public class DBManager {
    
    private static final String URL = "jdbc:mysql://localhost:3306/tecmis_db";
    private static final String USER = "root";
    private static final String PASS = "Apu1723";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
