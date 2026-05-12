package com.ecommerce.service;
//import reviews dao
import com.ecommerce.dao.ReviewDAO;
//import reviews model
import com.ecommerce.model.Review;
//import logger package
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ReviewService {
    //create logger instance
    private static final Logger logger    = LoggerFactory.getLogger(ReviewService.class);
    private final ReviewDAO     reviewDAO = new ReviewDAO();

    //create new reviews
    public boolean addReview(int userId, int productId,
                             String ratingStr, String comment) {
        try {
            //convert rating string to integer
            int rating = Integer.parseInt(ratingStr);

            //rating validation

            if (rating < 1 || rating > 5) {
                //logger type warn
                logger.warn("Invalid rating", rating);
                return false;
            }
            //create a new review object
            Review review = new Review(userId, productId, rating, comment);
            //add the new review using sql query
            return reviewDAO.addReview(review);

        } catch (NumberFormatException e) {
            //logger error
            logger.error("Invalid rating", e.getMessage());
            return false;
        }
    }

    //get all reviews of product by product id
    public List<Review> getReviewsByProduct(int productId) {
        return reviewDAO.getReviewsByProduct(productId);
    }

    //get all reviews
    public List<Review> getAllReviews() {
        return reviewDAO.getAllReviews();
    }
}