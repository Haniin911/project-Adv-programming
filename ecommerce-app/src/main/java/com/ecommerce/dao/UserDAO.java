package com.ecommerce.dao;
//importing user model
import com.ecommerce.model.User;

//importing the db connection
import com.ecommerce.util.DBConnection;

//importing the logger package
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class UserDAO {
    //define a logger instance
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    //create a new user in the database

    public boolean createUser(User user) {
        //sql for inserting new user row 
        String sql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
        //test connection and prepare sql statement
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                    
            //replace every ? in the sql command with the data got
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());
            //execute query
            ps.executeUpdate();
            //logger info type to inform success of the process
            logger.info("User created", user.getUsername());
            return true;

        } catch (SQLException e) {
            //throw logger error if there is sql or db connection error
            logger.error("Error creating user", e.getMessage());
            return false;
        }
    }

    // find user using their email used for sign in
    public User findByEmail(String email) {
        //return the user match with the email
        String sql = "SELECT * FROM users WHERE email = ?";
        //test connection and prepare sql statement
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            //get the email from the request and replace it with ?

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            //get result them map the user data returned as user object
            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            //logger error type 
            logger.error("Error finding user by email", e.getMessage());
        }
        return null;
    }

    // find user by id
    public User findById(int id) {
        //sql query for finding user id
        String sql = "SELECT * FROM users WHERE id = ?";
        // test connection and prepare sql statement
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            //return id as int and repace ? with it
            ps.setInt(1, id);
            //excute query
            ResultSet rs = ps.executeQuery();
            //return result as user object
            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            //logger error type
            logger.error("Error finding user by id", e.getMessage());
        }
        return null;
    }

    public boolean deleteUser(int id) {
        //define connection object as null
        //out of try statement to be used in the finally statement
        //finally statement will be excuted even if there is an exception or not
        Connection conn = null;
        try {
            //get db connection
            conn = DBConnection.getConnection();
            //excute sql statemenys but do not commit any changes untill I commit manually
            conn.setAutoCommit(false);

            //delete all reviews related to that user
            deleteRelated(conn, "DELETE FROM reviews WHERE user_id = ?", id);

            //delete use , first prepare sql statement
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM users WHERE id = ?")) {
                //replace ? with integer id
                ps.setInt(1, id);
                //excute query
                int rows = ps.executeUpdate();
                //if there is result
                if (rows > 0) {
                    conn.commit();
                    //logger info success state
                    logger.info("User deleted", id);
                    return true;
                } else {
                    //no user found state logger warn type
                    // go back for all executions and don't change anyhing
                    conn.rollback();
                    logger.warn("No user found to delete, id", id);
                    return false;
                }
            }

        } catch (SQLException e) {
            //if there sql or connection error
            logger.error("Error deleting user", id, e.getMessage());
            if (conn != null) {
                try {
                    //roll back on every executed command and don't commit changes
                    conn.rollback();
                } catch (SQLException ex) {
                    //logger error if there is sql or connection error
                    logger.error("Rollback failed: {}", ex.getMessage());
                }
            }
            return false;

        } finally {
            if (conn != null) {
                try {
                    //commit after returning true staet or false state
                    conn.setAutoCommit(true);
                    //close connection
                    conn.close();
                } catch (SQLException e) {
                    //logger error
                    logger.error("Connection close error", e.getMessage());
                }
            }
        }
    }

    //delete related reviews 
    //take the connection and sql statement and user id
    //throes sql exception
    private void deleteRelated(Connection conn, String sql, int userId)
            throws SQLException {
                //prepare sql statement
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            //replace ? with id
            ps.setInt(1, userId);
            //returned rows deleted
            int rows = ps.executeUpdate();
            //logger info to inform success process
            logger.info("Deleted rows ", rows, sql);
        }
    }

    //check if email existis
    public boolean emailExists(String email) {
        //sql command 
        String sql = "SELECT id FROM users WHERE email = ?";
        //test connection
        //prepare sql staement
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            //set ? as email
            ps.setString(1, email);
            //excute query
            ResultSet rs = ps.executeQuery();

            //return true if the email exists 
            //return false if not exists
            return rs.next();

        } catch (SQLException e) {
            //logger error 
            logger.error("Error checking email", e.getMessage());
            return false;
        }
    }

    // feunction to map on every row and turn it as a user object
    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}