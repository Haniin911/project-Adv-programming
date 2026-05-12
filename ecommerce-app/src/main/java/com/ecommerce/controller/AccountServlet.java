package com.ecommerce.controller;

//import user service
import com.ecommerce.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.*;

//import logger class
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/account/delete")
public class AccountServlet extends HttpServlet {
    //logger instance create
    private static final Logger logger      = LoggerFactory.getLogger(AccountServlet.class);

    //user service object
    private final UserService   userService = new UserService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        //test if user already logged in if not then redirect to login page
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        //ftech user data from the session object
        int    userId   = (int)   session.getAttribute("userId");
        String username = (String) session.getAttribute("username");

        //use the service delte method
        boolean success = userService.deleteAccount(userId);

        if (success) {
            logger.info("Account deleted", username);
            //end session
            session.invalidate();
            //send response of deleted user and redirect to login
            resp.sendRedirect(req.getContextPath() + "/login?deleted=true");
        } else {
            //send response with deletion failure
            resp.sendRedirect(req.getContextPath() + "/home?error=delete_failed");
        }
    }
}