package com.example.bitebox.model;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class Order {
    private int id; private String customer, phone, note, status; private BigDecimal total; private Timestamp createdAt;
    private List<OrderLine> lines = new ArrayList<>();
    public int getId() { return id; }            public void setId(int v) { id = v; }
    public String getCustomer() { return customer; } public void setCustomer(String v) { customer = v; }
    public String getPhone() { return phone; }   public void setPhone(String v) { phone = v; }
    public String getNote() { return note; }     public void setNote(String v) { note = v; }
    public String getStatus() { return status; } public void setStatus(String v) { status = v; }
    public BigDecimal getTotal() { return total; } public void setTotal(BigDecimal v) { total = v; }
    public Timestamp getCreatedAt() { return createdAt; } public void setCreatedAt(Timestamp v) { createdAt = v; }
    public List<OrderLine> getLines() { return lines; }
    public int getItemCount() { int n = 0; for (OrderLine l : lines) n += l.getQty(); return n; }
}
