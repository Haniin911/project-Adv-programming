package com.ecommerce.service;

import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.Product;
import com.ecommerce.util.RedisConnection;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.List;

public class ProductService {

    private static final Logger logger       = LoggerFactory.getLogger(ProductService.class);
    private static final String CACHE_KEY    = "all_products";
    private static final int    CACHE_EXPIRY = 300; 

    private final ProductDAO   productDAO   = new ProductDAO();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Get all products — checks Redis cache first
    public List<Product> getAllProducts() {

        // 1. Try to get from Redis cache
        try (Jedis jedis = RedisConnection.getJedis()) {
            String cached = jedis.get(CACHE_KEY);
            if (cached != null) {
                logger.info("Cache HIT — returning products from Redis");
                return objectMapper.readValue(cached,
                        new TypeReference<List<Product>>() {});
            }
        } catch (Exception e) {
            logger.warn("Redis cache read failed, falling back to DB: {}", e.getMessage());
        }

        // 2. Cache miss — fetch from database
        logger.info("Cache MISS — fetching products from MySQL");
        List<Product> products = productDAO.getAllProducts();

        // 3. Store result in Redis for next time
        try (Jedis jedis = RedisConnection.getJedis()) {
            String json = objectMapper.writeValueAsString(products);
            jedis.setex(CACHE_KEY, CACHE_EXPIRY, json);
            logger.info("Products cached in Redis");
        } catch (Exception e) {
            logger.warn("Redis cache write failed: {}", e.getMessage());
        }

        return products;
    }

    // Get a single product by ID
    public Product getProductById(int id) {
        return productDAO.getProductById(id);
    }

    // Add a new product (admin only — enforced in servlet)
    public boolean addProduct(String name, String description,
                              String price, String stock, int createdBy) {
        try {
            BigDecimal productPrice = new BigDecimal(price);
            int productStock        = Integer.parseInt(stock);

            Product product = new Product(name, description,
                                          productPrice, productStock, createdBy);

            boolean success = productDAO.addProduct(product);

            // Invalidate cache so new product appears immediately
            if (success) {
                invalidateCache();
            }

            return success;

        } catch (NumberFormatException e) {
            logger.error("Invalid price or stock value: {}", e.getMessage());
            return false;
        }
    }

    // Delete a product (admin only — enforced in servlet)
    public boolean deleteProduct(int id) {
        boolean success = productDAO.deleteProduct(id);

        // Invalidate cache
        if (success) {
            invalidateCache();
        }

        return success;
    }

    // Clear the Redis product cache
    private void invalidateCache() {
        try (Jedis jedis = RedisConnection.getJedis()) {
            jedis.del(CACHE_KEY);
            logger.info("Product cache invalidated");
        } catch (Exception e) {
            logger.warn("Failed to invalidate cache: {}", e.getMessage());
        }
    }
}