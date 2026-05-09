package com.ecommerce.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Product {

    private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
    private int createdBy;     
    private Timestamp createdAt;

   
    public Product() {}

    public Product(String name, String description, BigDecimal price, int stock, int createdBy) {
        this.name        = name;
        this.description = description;
        this.price       = price;
        this.stock       = stock;
        this.createdBy   = createdBy;
    }

   
    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }

    public String getName()                 { return name; }
    public void setName(String n)           { this.name = n; }

    public String getDescription()          { return description; }
    public void setDescription(String d)    { this.description = d; }

    public BigDecimal getPrice()            { return price; }
    public void setPrice(BigDecimal p)      { this.price = p; }

    public int getStock()                   { return stock; }
    public void setStock(int s)             { this.stock = s; }

    public int getCreatedBy()               { return createdBy; }
    public void setCreatedBy(int c)         { this.createdBy = c; }

    public Timestamp getCreatedAt()         { return createdAt; }
    public void setCreatedAt(Timestamp t)   { this.createdAt = t; }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name=" + name + ", price=" + price + "}";
    }
}