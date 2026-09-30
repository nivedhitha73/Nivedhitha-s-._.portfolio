package com.example.bitebox.model;
import java.math.BigDecimal;

/** One line in the cart or in a placed order (name and price are a snapshot). */
public class OrderLine {
    private int itemId, qty; private String name; private BigDecimal price;
    public OrderLine(int itemId, String name, BigDecimal price, int qty) {
        this.itemId = itemId; this.name = name; this.price = price; this.qty = qty;
    }
    public int getItemId() { return itemId; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public int getQty() { return qty; }
    public BigDecimal getTotal() { return price.multiply(BigDecimal.valueOf(qty)); }
}
