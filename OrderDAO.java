package com.example.bitebox.dao;
import com.example.bitebox.model.*;
import java.sql.*;
import java.util.*;

public class OrderDAO {

    /** Saves the order and its lines in ONE transaction (all or nothing). */
    public int place(Order o) throws SQLException {
        try (Connection c = DBUtil.getConnection()) {
            c.setAutoCommit(false);
            try {
                int id;
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO orders(customer,phone,note,total) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, o.getCustomer()); ps.setString(2, o.getPhone());
                    ps.setString(3, o.getNote()); ps.setBigDecimal(4, o.getTotal());
                    ps.executeUpdate();
                    try (ResultSet k = ps.getGeneratedKeys()) { k.next(); id = k.getInt(1); }
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO order_items(order_id,item_id,name,price,qty) VALUES(?,?,?,?,?)")) {
                    for (OrderLine l : o.getLines()) {
                        ps.setInt(1, id); ps.setInt(2, l.getItemId()); ps.setString(3, l.getName());
                        ps.setBigDecimal(4, l.getPrice()); ps.setInt(5, l.getQty()); ps.addBatch();
                    }
                    ps.executeBatch();
                }
                c.commit();
                return id;
            } catch (SQLException e) { c.rollback(); throw e; }
        }
    }

    public List<Order> recent(int limit) throws SQLException {
        List<Order> out = new ArrayList<>();
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM orders ORDER BY id DESC LIMIT ?")) {
            ps.setInt(1, limit);
            try (ResultSet r = ps.executeQuery()) { while (r.next()) out.add(map(r)); }
            for (Order o : out) loadLines(c, o);
        }
        return out;
    }

    public Order findById(int id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM orders WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet r = ps.executeQuery()) {
                if (!r.next()) return null;
                Order o = map(r); loadLines(c, o); return o;
            }
        }
    }

    public void cancel(int id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(
                "UPDATE orders SET status='CANCELLED' WHERE id = ? AND status = 'PLACED'")) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }

    private Order map(ResultSet r) throws SQLException {
        Order o = new Order();
        o.setId(r.getInt("id")); o.setCustomer(r.getString("customer")); o.setPhone(r.getString("phone"));
        o.setNote(r.getString("note")); o.setTotal(r.getBigDecimal("total"));
        o.setStatus(r.getString("status")); o.setCreatedAt(r.getTimestamp("created_at"));
        return o;
    }

    private void loadLines(Connection c, Order o) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT * FROM order_items WHERE order_id = ? ORDER BY id")) {
            ps.setInt(1, o.getId());
            try (ResultSet r = ps.executeQuery()) {
                while (r.next()) o.getLines().add(new OrderLine(r.getInt("item_id"), r.getString("name"), r.getBigDecimal("price"), r.getInt("qty")));
            }
        }
    }
}
