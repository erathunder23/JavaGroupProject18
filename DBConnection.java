import java.sql.*;

public class DBConnection {
    private static Connection con;

    public static Connection getConnection() throws Exception {
        if (con == null) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tecmis_db",
                    "root",
                    ""
            );
        }
        return con;
    }
}