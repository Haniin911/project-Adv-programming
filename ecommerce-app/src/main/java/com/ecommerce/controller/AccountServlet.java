package com.ecommerce.controller;

import com.ecommerce.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/account/delete")
public class AccountServlet extends HttpServlet {

    private static final Logger logger      = LoggerFactory.getLogger(AccountServlet.class);
    private final UserService   userService = new UserService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int    userId   = (int)   session.getAttribute("userId");
        String username = (String) session.getAttribute("username");

        boolean success = userService.deleteAccount(userId);

        if (success) {
            logger.info("Account deleted for user: {}", username);
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login?deleted=true");
        } else {
            resp.sendRedirect(req.getContextPath() + "/home?error=delete_failed");
        }
    }
}