package com.ecommerce.controller;

import com.ecommerce.service.ReviewService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
//logger package
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/reviews/add")
public class ReviewServlet extends HttpServlet {
    //logger instance
    private static final Logger logger        = LoggerFactory.getLogger(ReviewServlet.class);

    //review service 
    private final ReviewService reviewService = new ReviewService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        //get current user session if null then redirect to login
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        //get user data
        int    userId    = (int)   session.getAttribute("userId");
        String productId = req.getParameter("productId");
        String rating    = req.getParameter("rating");
        String comment   = req.getParameter("comment");

        //input validation
        if (productId == null || rating == null || comment == null || comment.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/home?error=invalid_review");
            return;
        }

        //use service to add review
        boolean success = reviewService.addReview(userId,
                Integer.parseInt(productId), rating, comment);

        //show success state
        if(success){
             logger.info("Review added");
        }
        //redirect to product details page
        resp.sendRedirect(req.getContextPath() + "/products/detail?id=" + productId);
    }
}