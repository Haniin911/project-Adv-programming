package com.ecommerce.service;

import com.ecommerce.dao.ReviewDAO;
import com.ecommerce.model.Review;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ReviewService {

    private static final Logger logger    = LoggerFactory.getLogger(ReviewService.class);
    private final ReviewDAO     reviewDAO = new ReviewDAO();

    // Add a review — validates rating range
    public boolean addReview(int userId, int productId,
                             String ratingStr, String comment) {
        try {
            int rating = Integer.parseInt(ratingStr);

            if (rating < 1 || rating > 5) {
                logger.warn("Invalid rating value: {}", rating);
                return false;
            }

            Review review = new Review(userId, productId, rating, comment);
            return reviewDAO.addReview(review);

        } catch (NumberFormatException e) {
            logger.error("Invalid rating format: {}", e.getMessage());
            return false;
        }
    }

    // Get all reviews for a product
    public List<Review> getReviewsByProduct(int productId) {
        return reviewDAO.getReviewsByProduct(productId);
    }

    // Get latest reviews for home page
    public List<Review> getAllReviews() {
        return reviewDAO.getAllReviews();
    }
}