package koneksi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Koneksi {

    private static final String URL  = "jdbc:mysql://localhost:3306/db_bikantin_pbo";
    private static final String USER = "root"; // username default XAMPP
    private static final String PASS = "";     // password default XAMPP (kosong)

    private static Connection conn;

    public static Connection configDB() throws SQLException {
        if (conn == null || conn.isClosed() || !conn.isValid(2)) {
            conn = DriverManager.getConnection(URL, USER, PASS);
        }
        return conn;
    }
}
