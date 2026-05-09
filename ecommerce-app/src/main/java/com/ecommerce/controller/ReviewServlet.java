package com.ecommerce.controller;

import com.ecommerce.service.ReviewService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/reviews/add")
public class ReviewServlet extends HttpServlet {

    private static final Logger logger        = LoggerFactory.getLogger(ReviewServlet.class);
    private final ReviewService reviewService = new ReviewService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int    userId    = (int)   session.getAttribute("userId");
        String productId = req.getParameter("productId");
        String rating    = req.getParameter("rating");
        String comment   = req.getParameter("comment");

        // Input validation
        if (productId == null || rating == null || comment == null || comment.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/home?error=invalid_review");
            return;
        }

        boolean success = reviewService.addReview(userId,
                Integer.parseInt(productId), rating, comment);

        logger.info("Review add attempt by userId={} success={}", userId, success);

        // Redirect back to product detail page
        resp.sendRedirect(req.getContextPath() + "/products/detail?id=" + productId);
    }
}