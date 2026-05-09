package com.ecommerce.model;

import java.sql.Timestamp;

public class User {

    private int id;
    private String username;
    private String email;
    private String password;
    private String role;      
    private Timestamp createdAt;

    public User() {}

    public User(String username, String email, String password, String role) {
        this.username  = username;
        this.email     = email;
        this.password  = password;
        this.role      = role;
    }

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

    @Override
    public String toString() {
        return "User{id=" + id + ", username=" + username + ", role=" + role + "}";
    }
}