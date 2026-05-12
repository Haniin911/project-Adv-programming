package com.ecommerce.model;
//for the created at column
import java.sql.Timestamp; 

public class User {
    // the column names we created in the database code 
    private int id;
    private String username;
    private String email;
    private String password;
    private String role;      
    private Timestamp createdAt;

    //first build an empty object then but the data in it
    public User() {}

    //constructor of the user model class
    //when we call User u = new User("haneen", "h@test.com", "123", "admin"); it assignes every value to it's column in the database
    public User(String username, String email, String password, String role) {
        this.username  = username;
        this.email     = email;
        this.password  = password;
        this.role      = role;
    }

    //getters and setters of every private variable in the model
    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }

    public String getUsername()             { return username; }
    public void setUsername(String u)       { this.username = u; }

    public String getEmail()                { return email; }
    public void setEmail(String e)          { this.email = e; }

    public String getPassword()             { return password; }
    public void setPassword(String p)       { this.password = p; }

    public String getRole()                 { return role; }
    public void setRole(String r)           { this.role = r; }

    public Timestamp getCreatedAt()         { return createdAt; }
    public void setCreatedAt(Timestamp t)   { this.createdAt = t; }

    //returning user information as a string
    // toString is already method in the object and we override it to change its behavior to return readable user data
    @Override
    public String toString() {
        return "User{id=" + id + ", username=" + username + ", role=" + role + "}";
    }
}