package com.ecommerce.dao;

import com.ecommerce.model.Product;
import com.ecommerce.util.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    private static final Logger logger = LoggerFactory.getLogger(ProductDAO.class);

    // Get all products
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                products.add(mapRow(rs));
            }

        } catch (SQLException e) {
            logger.error("Error fetching products: {}", e.getMessage());
        }
        return products;
    }

    // Get a single product by ID
    public Product getProductById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            logger.error("Error fetching product by id: {}", e.getMessage());
        }
        return null;
    }

    // Add a new product
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products (name, description, price, stock, created_by) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setInt(4, product.getStock());
            ps.setInt(5, product.getCreatedBy());
            ps.executeUpdate();
            logger.info("Product added: {}", product.getName());
            return true;

        } catch (SQLException e) {
            logger.error("Error adding product: {}", e.getMessage());
            return false;
        }
    }

    // Delete a product by ID
    public boolean deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            logger.info("Product deleted, id: {}", id);
            return rows > 0;

        } catch (SQLException e) {
            logger.error("Error deleting product: {}", e.getMessage());
            return false;
        }
    }

    // Map a ResultSet row to a Product object
    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setStock(rs.getInt("stock"));
        p.setCreatedBy(rs.getInt("created_by"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }
}