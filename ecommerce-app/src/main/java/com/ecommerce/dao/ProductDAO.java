//instead of typing sql code everywhere the data access object is for limit the sql codes distributed
package com.ecommerce.dao;

//product schema
import com.ecommerce.model.Product;

//db connection file
import com.ecommerce.util.DBConnection;

//logger libs
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    // defining the logger of the product model
    // used to show the errors orwhat happen in the server with the product database
    private static final Logger logger = LoggerFactory.getLogger(ProductDAO.class);

    // get all products sql code
    public List<Product> getAllProducts() {
        // empty list to fill with products
        List<Product> products = new ArrayList<>();
        // return all products orderd by the created at column in the decending order
        String sql = "SELECT * FROM products ORDER BY created_at DESC";

        // throw exception if the connection is down
        // try and close connection
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            // return every row as product object
            while (rs.next()) {
                products.add(mapRow(rs));
            }

        } catch (SQLException e) {
            // using the logger type error to show the error of fetching the products
            logger.error("Error fetching products: {}", e.getMessage());
        }
        return products;
    }

    public Product getProductById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            logger.error("Error fetching product by id: {}", e.getMessage());
        }
        return null;
    }

    // add new product
    public boolean addProduct(Product product) {

        // sql row to insert product data
        String sql = "INSERT INTO products (name, description, price, stock, created_by) VALUES (?, ?, ?, ?, ?)";
        // test connection
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            // replace each ? in order with the add request value
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setDouble(3, product.getPrice());
            ps.setInt(4, product.getStock());
            ps.setInt(5, product.getCreatedBy());
            // execute the sql
            ps.executeUpdate();
            logger.info("Product added", product.getName());
            return true;

        } catch (SQLException e) {
            logger.error("Error adding product", e.getMessage());
            return false;
        }
    }
    //delete product reviews before product
    public boolean deleteProduct(int id) {
        //sql statements
        String deleteReviews = "DELETE FROM reviews WHERE product_id = ?";
        String deleteProduct = "DELETE FROM products WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement ps1 = conn.prepareStatement(deleteReviews)) {
                ps1.setInt(1, id);
                //excute
                int reviewsDeleted = ps1.executeUpdate();
                logger.info("reviews deleted for product id {}: {}", id, reviewsDeleted);
            }

            try (PreparedStatement ps2 = conn.prepareStatement(deleteProduct)) {
                ps2.setInt(1, id);
                int rows = ps2.executeUpdate();
                logger.info("product deleted id: {}, rows affected: {}", id, rows);
                return rows > 0;
            }

        } catch (SQLException e) {
            logger.error("Error: {}", e.getMessage());
            return false;
        }
    }

    // map every result row returned as object
    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getDouble("price"));
        p.setStock(rs.getInt("stock"));
        p.setCreatedBy(rs.getInt("created_by"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }
}