package com.ecommerce.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBConnection {

    private static final Logger logger = LoggerFactory.getLogger(DBConnection.class);

    private static final String URL      = "jdbc:mysql://127.0.0.1:3306/ecommerce_db?useSSL=false&serverTimezone=UTC";
    private static final String USER     = "root";
    private static final String PASSWORD = "root"; 

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            logger.error("MySQL Driver not found", e);
            throw new SQLException("MySQL Driver not found");
        }
    }
}