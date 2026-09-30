package com.example.bitebox.dao;
import java.math.BigDecimal;
import java.sql.*;

/** JDBC connection + schema + sample menu. Embedded H2 file DB (created in your home folder). */
public class DBUtil {
    // For MySQL: change URL/USER/PASS, swap the H2 dependency in pom.xml for mysql-connector-j,
    // and replace AUTO_INCREMENT/TIMESTAMP syntax only if your server needs it.
    private static final String URL = "jdbc:h2:~/bitebox_db;AUTO_SERVER=TRUE", USER = "sa", PASS = "";

    static {
        try {
            Class.forName("org.h2.Driver");
            try (Connection c = getConnection(); Statement s = c.createStatement()) {
                s.execute("CREATE TABLE IF NOT EXISTS menu_items(id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(80) NOT NULL, " +
                        "category VARCHAR(40) NOT NULL, description VARCHAR(200), price DECIMAL(8,2) NOT NULL, emoji VARCHAR(8))");
                s.execute("CREATE TABLE IF NOT EXISTS orders(id INT AUTO_INCREMENT PRIMARY KEY, customer VARCHAR(80) NOT NULL, " +
                        "phone VARCHAR(20), note VARCHAR(200), total DECIMAL(10,2) NOT NULL, status VARCHAR(20) DEFAULT 'PLACED', " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
                s.execute("CREATE TABLE IF NOT EXISTS order_items(id INT AUTO_INCREMENT PRIMARY KEY, order_id INT NOT NULL, item_id INT NOT NULL, " +
                        "name VARCHAR(80) NOT NULL, price DECIMAL(8,2) NOT NULL, qty INT NOT NULL, FOREIGN KEY(order_id) REFERENCES orders(id))");
                ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM menu_items"); rs.next();
                if (rs.getInt(1) == 0) seed(c);
            }
        } catch (Exception e) { throw new ExceptionInInitializerError(e); }
    }

    private static void seed(Connection c) throws SQLException {
        String[][] d = {
            {"Margherita Pizza","Mains","Tomato, mozzarella, fresh basil","8.50","🍕"},
            {"Chicken Burger","Mains","Crispy chicken, lettuce, house sauce","7.25","🍔"},
            {"Veg Noodles","Mains","Wok-tossed noodles with seasonal vegetables","6.50","🍜"},
            {"Paneer Wrap","Mains","Grilled paneer, mint chutney, onions","5.75","🌯"},
            {"French Fries","Starters","Salted, crisp and hot","3.25","🍟"},
            {"Garden Salad","Starters","Cucumber, tomato, olives, lemon dressing","4.00","🥗"},
            {"Tomato Soup","Starters","Slow-cooked with herbs and cream","3.75","🍅"},
            {"Chocolate Brownie","Desserts","Warm, with a fudge centre","4.50","🍫"},
            {"Ice Cream Cup","Desserts","Vanilla, strawberry or chocolate","3.00","🍨"},
            {"Fresh Lime Soda","Drinks","Sweet, salted or mixed","2.50","🍋"},
            {"Cold Coffee","Drinks","Iced, blended, lightly sweet","3.50","☕"},
            {"Mango Smoothie","Drinks","Ripe mango and yogurt","3.95","🥭"}};
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO menu_items(name,category,description,price,emoji) VALUES(?,?,?,?,?)")) {
            for (String[] r : d) {
                ps.setString(1, r[0]); ps.setString(2, r[1]); ps.setString(3, r[2]);
                ps.setBigDecimal(4, new BigDecimal(r[3])); ps.setString(5, r[4]); ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public static Connection getConnection() throws SQLException { return DriverManager.getConnection(URL, USER, PASS); }
}
