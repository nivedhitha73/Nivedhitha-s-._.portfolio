package com.example.bitebox.model;
import java.math.BigDecimal;

public class MenuItem {
    private int id; private String name, category, description, emoji; private BigDecimal price;
    public MenuItem(int id, String name, String category, String description, BigDecimal price, String emoji) {
        this.id = id; this.name = name; this.category = category; this.description = description; this.price = price; this.emoji = emoji;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public String getEmoji() { return emoji; }
}
