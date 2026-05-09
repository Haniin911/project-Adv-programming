package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.model.Review;
import com.ecommerce.model.User;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.ReviewService;
import com.ecommerce.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(HomeServlet.class);
    private final ProductService productService = new ProductService();
    private final ReviewService reviewService = new ReviewService();
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            // Get logged-in user from session
            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute("userId") != null) {
                int userId = (Integer) session.getAttribute("userId");
                User user = userService.getUserById(userId);
                req.setAttribute("user", user);
            }

            // Load products and reviews
            List<Product> products = productService.getAllProducts();
            List<Review> reviews = reviewService.getAllReviews();

            req.setAttribute("products", products);
            req.setAttribute("reviews", reviews);

            logger.info("Home page loaded with {} products", products.size());

        } catch (Exception e) {
            logger.error("Failed to load home page data", e);
            req.setAttribute("error", "Could not load products. Please try again.");
        }

        // forward() must ALWAYS be the last line — after try/catch
        req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, resp);
    }
}