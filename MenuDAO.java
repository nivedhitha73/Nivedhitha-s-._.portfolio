package com.example.bitebox.dao;
import com.example.bitebox.model.MenuItem;
import java.sql.*;
import java.util.*;

public class MenuDAO {
    private MenuItem map(ResultSet r) throws SQLException {
        return new MenuItem(r.getInt("id"), r.getString("name"), r.getString("category"),
                r.getString("description"), r.getBigDecimal("price"), r.getString("emoji"));
    }

    public List<MenuItem> find(String category, String q) throws SQLException {
        String sql = "SELECT * FROM menu_items WHERE (? = '' OR category = ?) AND (LOWER(name) LIKE ? OR LOWER(description) LIKE ?) ORDER BY category, name";
        String cat = category == null ? "" : category, like = "%" + (q == null ? "" : q.toLowerCase()) + "%";
        List<MenuItem> out = new ArrayList<>();
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cat); ps.setString(2, cat); ps.setString(3, like); ps.setString(4, like);
            try (ResultSet r = ps.executeQuery()) { while (r.next()) out.add(map(r)); }
        }
        return out;
    }

    public List<String> categories() throws SQLException {
        List<String> out = new ArrayList<>();
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT DISTINCT category FROM menu_items ORDER BY category")) {
            while (r.next()) out.add(r.getString(1));
        }
        return out;
    }

    public MenuItem findById(int id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM menu_items WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet r = ps.executeQuery()) { return r.next() ? map(r) : null; }
        }
    }
}
