package com.ecommerce.model;
//for the created at column
import java.sql.Timestamp;

public class Review {
    // the column names we created in the database code 
    private int id;
    private int userId;
    private int productId;
    private int rating;        
    private String comment;
    private Timestamp createdAt;
    private String username;

    //first build an empty object then but the data in it
    public Review() {}
    //get the user id and product id and the comment +rating
    //the username will be assigned using the user id
    public Review(int userId, int productId, int rating, String comment) {
        this.userId    = userId;
        this.productId = productId;
        this.rating    = rating;
        this.comment   = comment;
    }
    //getters and setters
    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }

    public int getUserId()                  { return userId; }
    public void setUserId(int u)            { this.userId = u; }

    public int getProductId()               { return productId; }
    public void setProductId(int p)         { this.productId = p; }

    public int getRating()                  { return rating; }
    public void setRating(int r)            { this.rating = r; }

    public String getComment()              { return comment; }
    public void setComment(String c)        { this.comment = c; }

    public Timestamp getCreatedAt()         { return createdAt; }
    public void setCreatedAt(Timestamp t)   { this.createdAt = t; }

    public String getUsername()             { return username; }
    public void setUsername(String u)       { this.username = u; }

    //returning readable data using override toString method
    @Override
    public String toString() {
        return "Review{id=" + id + ", userId=" + userId + ", rating=" + rating + "}";
    }
}