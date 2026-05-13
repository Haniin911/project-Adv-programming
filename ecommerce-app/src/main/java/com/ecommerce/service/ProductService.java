
package com.ecommerce.service;

//get product dao
import com.ecommerce.dao.ProductDAO;

//import product model
import com.ecommerce.model.Product;

//get redis connection end point
import com.ecommerce.util.RedisConnection;
import com.fasterxml.jackson.core.type.TypeReference;

//redis only store text but this convert it into objects
import com.fasterxml.jackson.databind.ObjectMapper;

//get logger package
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.List;

public class ProductService {
    // logger instance
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    // name of redis key
    private static final String CACHE_KEY = "all_products";
    // expiration key of redis retrival
    private static final int CACHE_EXPIRY = 300;

    // product dao
    private final ProductDAO productDAO = new ProductDAO();

    private final ObjectMapper objectMapper = new ObjectMapper();

    // get all products
    public List<Product> getAllProducts() {

        // use redis to return all products
        try (Jedis jedis = RedisConnection.getJedis()) {
            // use of cache key
            String cached = jedis.get(CACHE_KEY);

            // first explore redis
            if (cached != null) {
                logger.info("Cache HIT");
                return objectMapper.readValue(cached,
                        new TypeReference<List<Product>>() {
                        });
            }
        } catch (Exception e) {
            logger.warn("Redis cache read failed", e.getMessage());
        }

        // logger info to inform about redis explore nullity and use database
        logger.info("fetching products from MySQL");

        // empty list to put products in
        // use dao
        List<Product> products = productDAO.getAllProducts();

        // put fteched products in redis
        try (Jedis jedis = RedisConnection.getJedis()) {
            String json = objectMapper.writeValueAsString(products);
            jedis.setex(CACHE_KEY, CACHE_EXPIRY, json);
            logger.info("Products cached in Redis");
        } catch (Exception e) {
            // logger warning of exception
            logger.warn("Redis cache write", e.getMessage());
        }

        return products;
    }

    // get product by id
    public Product getProductById(int id) {
        return productDAO.getProductById(id);
    }
    //delete product
    public boolean deleteProduct(int id) {
        //first empty cache
        emptyCache();

        boolean success = productDAO.deleteProduct(id);

        if (!success) {
            logger.warn("Delete failed for product id: {}", id);
        }

        return success;
    }
    //add new product
    public boolean addProduct(String name, String description,
            String price, String stock, int createdBy) {
        try {
            double productPrice = Double.parseDouble(price);
            int productStock = Integer.parseInt(stock);

            Product product = new Product(name, description,
                    productPrice, productStock, createdBy);
            emptyCache();

            boolean success = productDAO.addProduct(product);
            return success;

        } catch (NumberFormatException e) {
            logger.error("Invalid product: {}", e.getMessage());
            return false;
        }
    }

    private void emptyCache() {
        try (Jedis jedis = RedisConnection.getJedis()) {
            Long deleted = jedis.del(CACHE_KEY);
            logger.info("Cache emptied, keys deleted: {}", deleted);
        } catch (Exception e) {
            logger.warn("Failed to empty cache: {}", e.getMessage());
        }
    }
}