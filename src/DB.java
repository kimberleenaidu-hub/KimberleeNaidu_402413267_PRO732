import java.sql.*;

public class DB {
    private static final String URL = "jdbc:mysql://localhost:3306/healthfirst_pims";
    private static final String USER = "root";
    private static final String PASSWORD = "NewPassword123!";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
