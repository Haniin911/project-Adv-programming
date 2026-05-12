//data access object
package com.ecommerce.dao;
// get review schema
import com.ecommerce.model.Review;
//db connection
import com.ecommerce.util.DBConnection;
//logger 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    //logger define
    private static final Logger logger = LoggerFactory.getLogger(ReviewDAO.class);

    // add new review
    public boolean addReview(Review review) {
        //add new review
        String sql = "INSERT INTO reviews (user_id, product_id, rating, comment) VALUES (?, ?, ?, ?)";
        //test connection
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            //replace every ? with review model variables

            ps.setInt(1, review.getUserId());
            ps.setInt(2, review.getProductId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());
            //execute sql row
            ps.executeUpdate();

            //logger type info to inform user that is the user already added
            logger.info("Review added by user: {}", review.getUserId());
            return true;

        } catch (SQLException e) {
            //logger type error to inform the user about the error
            logger.error("Error adding review: {}", e.getMessage());
            return false;
        }
    }

    // get all reviews 
    public List<Review> getReviewsByProduct(int productId) {
        //create empty array list to fill it with the reviews objects related to specific product
        List<Review> reviews = new ArrayList<>();
        //sql (select all reviews and join every user id with its match with the user id in the users table)
        String sql = "SELECT r.*, u.username " +
                     "FROM reviews r " +
                     "JOIN users u ON r.user_id = u.id " +
                     "WHERE r.product_id = ? " +
                     "ORDER BY r.created_at DESC";
        //test connection
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            //replace ? with the id of the product sent with request
            ps.setInt(1, productId);
            //execute the sql query
            ResultSet rs = ps.executeQuery();

            //map on every row returned and turn it into object 
            while (rs.next()) {
                reviews.add(mapRow(rs));
            }

        } catch (SQLException e) {
            //logger message to display the error
            logger.error("Error fetching reviews: {}", e.getMessage());
        }
        return reviews;
    }

    // get all reviews
    public List<Review> getAllReviews() {
        //create an empty list to put reviews in it
        List<Review> reviews = new ArrayList<>();

        //select all reviews
        //join it with the user table using the user id 
        //orderd by created at
        //return only 10
        String sql = "SELECT r.*, u.username " +
                     "FROM reviews r " +
                     "JOIN users u ON r.user_id = u.id " +
                     "ORDER BY r.created_at DESC " +
                     "LIMIT 10";
        
        //test connention
        //prepare statement
        //execute query and return reslt in rs
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            //map row function to convert every row to review object
            while (rs.next()) {
                reviews.add(mapRow(rs));
            }
        //return logger error
        } catch (SQLException e) {
            logger.error("Error fetching all reviews: {}", e.getMessage());
        }
        return reviews;
    }

    // 
    private Review mapRow(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setId(rs.getInt("id"));
        r.setUserId(rs.getInt("user_id"));
        r.setProductId(rs.getInt("product_id"));
        r.setRating(rs.getInt("rating"));
        r.setComment(rs.getString("comment"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        //skips carshing if we want to use the reviews data without need of username
        try {
            r.setUsername(rs.getString("username"));
        } catch (SQLException ignored) {}
        return r;
    }
}