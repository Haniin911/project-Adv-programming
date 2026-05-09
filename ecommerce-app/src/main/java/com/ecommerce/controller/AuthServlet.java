package com.ecommerce.controller;

import com.ecommerce.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet(urlPatterns = { "/login", "/register", "/logout" })
public class AuthServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AuthServlet.class);
    private final UserService userService = new UserService();

    // Show login or register pages
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        if ("/logout".equals(path)) {
            // Invalidate session and redirect home
            HttpSession session = req.getSession(false);
            if (session != null) {
                logger.info("User logged out: {}", session.getAttribute("username"));
                session.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        if ("/login".equals(path)) {
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        } else {
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
        }
    }

    // Handle login and register form submissions
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        if ("/login".equals(path)) {
            handleLogin(req, resp);
        } else if ("/register".equals(path)) {
            handleRegister(req, resp);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        // Input validation
        if (email == null || email.isBlank() ||
                password == null || password.isBlank()) {
            req.setAttribute("error", "Email and password are required.");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }

        String token = userService.login(email, password);

        if (token == null) {
            req.setAttribute("error", "Invalid email or password.");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }

        try {
            int userId = com.ecommerce.util.JwtUtil.extractUserId(token);
            String username = com.ecommerce.util.JwtUtil.extractUsername(token);
            String role = com.ecommerce.util.JwtUtil.extractRole(token);

            // Invalidate old session first
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null)
                oldSession.invalidate();

            // Create fresh session
            HttpSession session = req.getSession(true);
            session.setAttribute("userId", userId);
            session.setAttribute("username", username);
            session.setAttribute("role", role);
            session.setAttribute("token", token);

            logger.info("Session created for user: {} role: {}", username, role);
            resp.sendRedirect(req.getContextPath() + "/home");

        } catch (Exception e) {
            logger.error("Login session error: {}", e.getMessage());
            req.setAttribute("error", "Login failed. Please try again.");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        // Input validation
        if (username == null || username.isBlank() ||
                email == null || email.isBlank() ||
                password == null || password.isBlank()) {
            req.setAttribute("error", "All fields are required.");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }

        if (password.length() < 6) {
            req.setAttribute("error", "Password must be at least 6 characters.");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }

        boolean success = userService.register(username, email, password);

        if (!success) {
            req.setAttribute("error", "Email already registered. Please login.");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }

        logger.info("New user registered: {}", username);
        resp.sendRedirect(req.getContextPath() + "/login?registered=true");
    }
}