
//use the service package
package com.ecommerce.service;

//import the user dao 
import com.ecommerce.dao.UserDAO;
//import the user model
import com.ecommerce.model.User;
//use json web token
import com.ecommerce.util.JwtUtil;
//use bcrypt to make password encryption
import org.mindrot.jbcrypt.BCrypt;
//logger package
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
    //logger instance
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    //user dao class object 
    private final UserDAO userDAO = new UserDAO();

    // create a new user
    public boolean register(String username, String email, String password) {

        // check if the email already exist for no account repition
        if (userDAO.emailExists(email)) {
            //logger warning message if the email already exists
            //retun false and cancel registeration
            logger.warn("Registration failed", email);
            return false;
        }

        //hashing passwords using bcrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        //user object creation
        User user = new User(username, email, hashedPassword, "USER");
        //send the new object to the dao and create it with the sql command
        return userDAO.createUser(user);
    }
    //login process
    public String login(String email, String password) {
        //find the user by the email and return user object with its data
        User user = userDAO.findByEmail(email);

        //if user is not exist
        if (user == null) {
            logger.warn("Login failed", email);
            return null;
        }

        
        //encrypt the password and test 
        if (!BCrypt.checkpw(password, user.getPassword())) {
            logger.warn("Login failed", email);
            return null;
        }
        //if user exist & password is correct thenshow logger info 
        logger.info("User logged in", email);
        //generate user token to be used in his session of login process
        return JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
    }

    //get user by id
    public User getUserById(int id) {
        User user = userDAO.findById(id);
        return user;
    }

    //delete user 
    public boolean deleteAccount(int userId) {
        logger.info("Deleting accont...", userId);
        return userDAO.deleteUser(userId);
    }
}