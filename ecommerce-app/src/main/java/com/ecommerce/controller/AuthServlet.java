package com.ecommerce.controller;

import com.ecommerce.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

//import logger package
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet(urlPatterns = { "/login", "/register", "/logout" })
public class AuthServlet extends HttpServlet {

    //logger instance
    private static final Logger logger = LoggerFactory.getLogger(AuthServlet.class);
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        

        //servlet path
        String path = req.getServletPath();

        if ("/logout".equals(path)) {
            //get the current user session
            //if false don't create a new one
            HttpSession session = req.getSession(false);
            if (session != null) {
                logger.info("User logged out");
                //terminate the current session
                session.invalidate();
            }
            //redirect to home after logging out
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        //after logout
        if ("/login".equals(path)) {
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        } else {
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
        }
    }

    //handle login and register pages post request
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        //get path
        String path = req.getServletPath();

        if ("/login".equals(path)) {
            handleLogin(req, resp);
        } else if ("/register".equals(path)) {
            handleRegister(req, resp);
        }
    }

    //login function
    private void handleLogin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        //request parameters
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        //input validation
        if (email == null || email.isBlank() ||
                password == null || password.isBlank()) {
            req.setAttribute("error", "please fill all feilds");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }
        //return user token
        String token = userService.login(email, password);

        //if token is null then no email or passwprd matched
        //redirect again to login page
        if (token == null) {
            req.setAttribute("error", "Invalid email or password.");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }

        try {
            //extarct user information
            int userId = com.ecommerce.util.JwtUtil.extractUserId(token);
            String username = com.ecommerce.util.JwtUtil.extractUsername(token);
            String role = com.ecommerce.util.JwtUtil.extractRole(token);

            //delete old sessions if exist
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null)
                oldSession.invalidate();

            //create new session and store the user data in it
            //true means if sasssion not exist then create a new one
            HttpSession session = req.getSession(true);
            session.setAttribute("userId", userId);
            session.setAttribute("username", username);
            session.setAttribute("role", role);
            session.setAttribute("token", token);

            //logger info type sucess process 
            logger.info("session created for user", username, role);

            //redirect to home page
            resp.sendRedirect(req.getContextPath() + "/home");

        } catch (Exception e) {
            logger.error("Login session error", e.getMessage());
            req.setAttribute("error", "Login failed Please try again later");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        
        //get request parameters
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        //input validation
        if (username == null || username.isBlank() ||
                email == null || email.isBlank() ||
                password == null || password.isBlank()) {
            req.setAttribute("error", "please fill all feilds");
            //redirect to register
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }
        //password validation
        if (password.length() < 6) {
            req.setAttribute("error", "password must be at least 6 characters.");
            //redirect to register
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }
        //user service
        boolean success = userService.register(username, email, password);

        //test process for email not exist
        if (!success) {
            req.setAttribute("error", "Email already exist");
            //redirect to register
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }
        //logger info of sucess process
        logger.info("New user registered", username);
        //redirect to login after regiteration process
        resp.sendRedirect(req.getContextPath() + "/login?registered=true");
    }
}