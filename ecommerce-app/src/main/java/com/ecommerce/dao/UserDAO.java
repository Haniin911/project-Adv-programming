package com.ecommerce.dao;

import com.ecommerce.model.User;
import com.ecommerce.util.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class UserDAO {

    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    // Insert a new user into the database
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());
            ps.executeUpdate();
            logger.info("User created: {}", user.getUsername());
            return true;

        } catch (SQLException e) {
            logger.error("Error creating user: {}", e.getMessage());
            return false;
        }
    }

    // Find a user by their email (used for login)
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            logger.error("Error finding user by email: {}", e.getMessage());
        }
        return null;
    }

    // Find a user by their ID
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            logger.error("Error finding user by id: {}", e.getMessage());
        }
        return null;
    }

    public boolean deleteUser(int id) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Delete reviews that belong to this user
            deleteRelated(conn, "DELETE FROM reviews WHERE user_id = ?", id);

            // 2. Nullify products created by this user (keep the products, just unlink)
            deleteRelated(conn, "UPDATE products SET created_by = NULL WHERE created_by = ?", id);

            // 3. Now safe to delete the user
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM users WHERE id = ?")) {
                ps.setInt(1, id);
                int rows = ps.executeUpdate();

                if (rows > 0) {
                    conn.commit();
                    logger.info("User deleted, id: {}", id);
                    return true;
                } else {
                    conn.rollback();
                    logger.warn("No user found to delete, id: {}", id);
                    return false;
                }
            }

        } catch (SQLException e) {
            logger.error("Error deleting user {}: {}", id, e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.error("Rollback failed: {}", ex.getMessage());
                }
            }
            return false;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Connection close error: {}", e.getMessage());
                }
            }
        }
    }

    // Helper to avoid repeating try/catch for each delete
    private void deleteRelated(Connection conn, String sql, int userId)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            int rows = ps.executeUpdate();
            logger.info("Deleted {} row(s) with: {}", rows, sql);
        }
    }

    // Check if email already exists (for registration validation)
    public boolean emailExists(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            logger.error("Error checking email: {}", e.getMessage());
            return false;
        }
    }

    // Map a ResultSet row to a User object
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