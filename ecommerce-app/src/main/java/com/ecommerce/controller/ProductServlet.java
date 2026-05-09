package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.ReviewService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet(urlPatterns = {"/products/add", "/products/delete", "/products/detail"})
public class ProductServlet extends HttpServlet {

    private static final Logger  logger         = LoggerFactory.getLogger(ProductServlet.class);
    private final ProductService productService = new ProductService();
    private final ReviewService  reviewService  = new ReviewService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        if ("/products/add".equals(path)) {
            // Only admin can see the add product page
            if (!isAdmin(req)) {
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }
            req.getRequestDispatcher("/WEB-INF/views/addProduct.jsp").forward(req, resp);

        } else if ("/products/detail".equals(path)) {
            // Show product details page
            String idParam = req.getParameter("id");
            if (idParam == null) {
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }
            int     productId = Integer.parseInt(idParam);
            Product product   = productService.getProductById(productId);

            if (product == null) {
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }

            req.setAttribute("product", product);
            req.setAttribute("reviews", reviewService.getReviewsByProduct(productId));
            req.getRequestDispatcher("/WEB-INF/views/productDetail.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        if ("/products/add".equals(path)) {
            handleAddProduct(req, resp);
        } else if ("/products/delete".equals(path)) {
            handleDeleteProduct(req, resp);
        }
    }

    private void handleAddProduct(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        if (!isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        String name        = req.getParameter("name");
        String description = req.getParameter("description");
        String price       = req.getParameter("price");
        String stock       = req.getParameter("stock");
        int    createdBy   = (int) req.getSession().getAttribute("userId");

        // Input validation
        if (name == null || name.isBlank() ||
            price == null || price.isBlank() ||
            stock == null || stock.isBlank()) {
            req.setAttribute("error", "Name, price and stock are required.");
            req.getRequestDispatcher("/WEB-INF/views/addProduct.jsp").forward(req, resp);
            return;
        }

        boolean success = productService.addProduct(name, description, price, stock, createdBy);

        if (success) {
            logger.info("Product added by admin userId: {}", createdBy);
            resp.sendRedirect(req.getContextPath() + "/home?added=true");
        } else {
            req.setAttribute("error", "Failed to add product. Check price and stock values.");
            req.getRequestDispatcher("/WEB-INF/views/addProduct.jsp").forward(req, resp);
        }
    }

    private void handleDeleteProduct(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        if (!isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        int     productId = Integer.parseInt(idParam);
        boolean success   = productService.deleteProduct(productId);

        logger.info("Product delete attempt id={} success={}", productId, success);
        resp.sendRedirect(req.getContextPath() + "/home");
    }

    // Helper — checks if logged-in user is ADMIN
    private boolean isAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return false;
        String role = (String) session.getAttribute("role");
        return "ADMIN".equals(role);
    }
}