package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.ReviewService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

//logger package
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet(urlPatterns = {"/products/add", "/products/delete", "/products/detail"})
public class ProductServlet extends HttpServlet {

    //logger instance
    private static final Logger  logger         = LoggerFactory.getLogger(ProductServlet.class);

    //product and review services
    private final ProductService productService = new ProductService();
    private final ReviewService  reviewService  = new ReviewService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        //get current path
        String path = req.getServletPath();

        if ("/products/add".equals(path)) {
            //admin role restriction on adding product
            if (!isAdmin(req)) {
                //redirect to home page not allowed to access add product if you are not admin
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }
            //else redirect to add product page
            req.getRequestDispatcher("/WEB-INF/views/addProduct.jsp").forward(req, resp);

        } else if ("/products/detail".equals(path)) {
            // show product details page
            String idParam = req.getParameter("id");

            //get product id
            int     productId = Integer.parseInt(idParam);
            Product product   = productService.getProductById(productId);

            //send product data and reviews
            req.setAttribute("product", product);
            req.setAttribute("reviews", reviewService.getReviewsByProduct(productId));

            //redirect to product details page
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
        //validate admin
        if (!isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        //get parameters
        String name        = req.getParameter("name");
        String description = req.getParameter("description");
        String price       = req.getParameter("price");
        String stock       = req.getParameter("stock");
        int    createdBy   = (int) req.getSession().getAttribute("userId");

        //input validation
        if (name == null || name.isBlank() ||
            price == null || price.isBlank() ||
            stock == null || stock.isBlank()) {
            req.setAttribute("error", "Name, price and stock are required.");
            req.getRequestDispatcher("/WEB-INF/views/addProduct.jsp").forward(req, resp);
            return;
        }
        //sucess state test
        boolean success = productService.addProduct(name, description, price, stock, createdBy);

        if (success) {
            logger.info("Product added by admin");
            //redirect to home with new product
            resp.sendRedirect(req.getContextPath() + "/home?added=true");
        } else {
            req.setAttribute("error", "Failed to add product. Check price and stock values.");
            req.getRequestDispatcher("/WEB-INF/views/addProduct.jsp").forward(req, resp);
        }
    }

    private void handleDeleteProduct(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        //validate admin
        if (!isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        //get id parameter
        String idParam = req.getParameter("id");
       
        //get request parameters
        int     productId = Integer.parseInt(idParam);
        boolean success   = productService.deleteProduct(productId);

        logger.info("Product deleted", productId, success);
        //redirect to home
        resp.sendRedirect(req.getContextPath() + "/home");
    }
    //validate admin
    private boolean isAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return false;
        String role = (String) session.getAttribute("role");
        return "ADMIN".equals(role);
    }
}