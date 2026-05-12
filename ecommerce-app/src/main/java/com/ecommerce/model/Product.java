package com.ecommerce.model;

//for the created at column
import java.sql.Timestamp;

public class Product {
    // the column names we created in the database code 
    private int id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private int createdBy;     
    private Timestamp createdAt;

   //first build an empty object then but the data in it
    public Product() {}

    //assign data using the argumented constructor
    //created by already set to only the admin (oonnnnlllyyy)
    public Product(String name, String description, double price, int stock, int createdBy) {
        this.name        = name;
        this.description = description;
        this.price       = price;
        this.stock       = stock;
        this.createdBy   = createdBy;
    }

    //getters and setters
    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }

    public String getName()                 { return name; }
    public void setName(String n)           { this.name = n; }

    public String getDescription()          { return description; }
    public void setDescription(String d)    { this.description = d; }

    public double getPrice()            { return price; }
    public void setPrice(double p)      { this.price = p; }

    public int getStock()                   { return stock; }
    public void setStock(int s)             { this.stock = s; }

    public int getCreatedBy()               { return createdBy; }
    public void setCreatedBy(int c)         { this.createdBy = c; }

    public Timestamp getCreatedAt()         { return createdAt; }
    public void setCreatedAt(Timestamp t)   { this.createdAt = t; }


    //return the data of the product clean with override toString method
    @Override
    public String toString() {
        return "Product{id=" + id + ", name=" + name + ", price=" + price + "}";
    }
}