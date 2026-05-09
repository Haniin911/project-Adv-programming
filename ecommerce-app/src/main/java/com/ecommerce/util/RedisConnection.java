package com.ecommerce.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisConnection {

    private static final Logger logger = LoggerFactory.getLogger(RedisConnection.class);

    private static final String HOST     = "redis-15106.c53.west-us.azure.cloud.redislabs.com";   
    private static final int    PORT     = 15106;               
    private static final String PASSWORD = "ktikL8xY39mhBRqLoG37BJFYMNSkzpBc"; 

    private static JedisPool pool;

    static {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(10);
            config.setMaxIdle(5);
            pool = new JedisPool(config, HOST, PORT, 2000, PASSWORD);
            logger.info("Redis connection pool initialized");
        } catch (Exception e) {
            logger.error("Failed to initialize Redis pool", e);
        }
    }

    public static Jedis getJedis() {
        return pool.getResource();
    }

    public static void close() {
        if (pool != null) pool.close();
    }
}