package com.ecommerce.dao;

import com.ecommerce.model.Review;
import com.ecommerce.util.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    private static final Logger logger = LoggerFactory.getLogger(ReviewDAO.class);

    // Add a new review
    public boolean addReview(Review review) {
        String sql = "INSERT INTO reviews (user_id, product_id, rating, comment) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, review.getUserId());
            ps.setInt(2, review.getProductId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());
            ps.executeUpdate();
            logger.info("Review added by user: {}", review.getUserId());
            return true;

        } catch (SQLException e) {
            logger.error("Error adding review: {}", e.getMessage());
            return false;
        }
    }

    // Get all reviews for a specific product (with username joined)
    public List<Review> getReviewsByProduct(int productId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.username " +
                     "FROM reviews r " +
                     "JOIN users u ON r.user_id = u.id " +
                     "WHERE r.product_id = ? " +
                     "ORDER BY r.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                reviews.add(mapRow(rs));
            }

        } catch (SQLException e) {
            logger.error("Error fetching reviews: {}", e.getMessage());
        }
        return reviews;
    }

    // Get all reviews (for home page feedback section)
    public List<Review> getAllReviews() {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.username " +
                     "FROM reviews r " +
                     "JOIN users u ON r.user_id = u.id " +
                     "ORDER BY r.created_at DESC " +
                     "LIMIT 10";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                reviews.add(mapRow(rs));
            }

        } catch (SQLException e) {
            logger.error("Error fetching all reviews: {}", e.getMessage());
        }
        return reviews;
    }

    // Map a ResultSet row to a Review object
    private Review mapRow(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setId(rs.getInt("id"));
        r.setUserId(rs.getInt("user_id"));
        r.setProductId(rs.getInt("product_id"));
        r.setRating(rs.getInt("rating"));
        r.setComment(rs.getString("comment"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        try {
            r.setUsername(rs.getString("username"));
        } catch (SQLException ignored) {}
        return r;
    }
}