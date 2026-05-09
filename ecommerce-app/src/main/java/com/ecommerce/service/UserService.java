package com.ecommerce.service;

import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.User;
import com.ecommerce.util.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserDAO userDAO = new UserDAO();

    // Register a new user
    public boolean register(String username, String email, String password) {

        // Check if email already taken
        if (userDAO.emailExists(email)) {
            logger.warn("Registration failed - email already exists: {}", email);
            return false;
        }

        // Hash the password before storing
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        User user = new User(username, email, hashedPassword, "USER");
        return userDAO.createUser(user);
    }

    public String login(String email, String password) {

        User user = userDAO.findByEmail(email);

        if (user == null) {
            logger.warn("Login failed - user not found: {}", email);
            return null;
        }

        if (!BCrypt.checkpw(password, user.getPassword())) {
            logger.warn("Login failed - wrong password for: {}", email);
            return null;
        }

        logger.info("User logged in: {}", email);
        return JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
    }

    // Get user info by ID (safe — no password returned)
    public User getUserById(int id) {
        User user = userDAO.findById(id);
        if (user != null) {
            user.setPassword(null); // never expose password
        }
        return user;
    }

    // Delete a user account
    public boolean deleteAccount(int userId) {
        logger.info("Deleting account for user id: {}", userId);
        return userDAO.deleteUser(userId);
    }
}