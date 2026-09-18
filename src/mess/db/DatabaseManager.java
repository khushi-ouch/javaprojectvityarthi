package mess.db;

import java.sql.*;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:mess.db";
    private static Connection conn;

    static {
        // load the driver class explicitly instead of relying on ServiceLoader
        // auto-registration, which doesn't always kick in depending on how
        // the classpath is set up (some IDE run configs, older JVMs, etc.)
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("sqlite-jdbc driver not found on classpath: " + e.getMessage());
        }
    }

    // simple singleton-ish connection, good enough for a CLI app
    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL);
        }
        return conn;
    }

    public static void initSchema() {
        String items = "CREATE TABLE IF NOT EXISTS items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "category TEXT," +
                "price REAL NOT NULL," +
                "quantity INTEGER NOT NULL," +
                "low_stock_threshold INTEGER DEFAULT 5" +
                ")";

        String orders = "CREATE TABLE IF NOT EXISTS orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "student_name TEXT," +
                "created_at TEXT," +
                "total REAL" +
                ")";

        String orderItems = "CREATE TABLE IF NOT EXISTS order_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "order_id INTEGER," +
                "item_id INTEGER," +
                "item_name TEXT," +
                "qty INTEGER," +
                "price_each REAL," +
                "FOREIGN KEY(order_id) REFERENCES orders(id)" +
                ")";

        try (Statement st = getConnection().createStatement()) {
            st.execute(items);
            st.execute(orders);
            st.execute(orderItems);
        } catch (SQLException e) {
            System.out.println("could not set up db tables: " + e.getMessage());
        }
    }

    public static void close() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            // not a big deal at shutdown, just move on
        }
    }
}
