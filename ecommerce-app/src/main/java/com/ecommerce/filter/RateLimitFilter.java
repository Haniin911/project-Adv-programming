package com.ecommerce.filter;

import com.ecommerce.util.RedisConnection;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.io.IOException;

@WebFilter(urlPatterns = {
    "/login",
    "/register",
    "/products/add",
    "/products/delete"
})
public class RateLimitFilter implements Filter {

    private static final Logger logger      = LoggerFactory.getLogger(RateLimitFilter.class);
    private static final int    MAX_REQUEST = 10; 
    private static final int    WINDOW_SEC  = 60; 

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // use client ip as key
        String ip      = req.getRemoteAddr();
        String uri     = req.getRequestURI();
        String redisKey = "rate:" + ip + ":" + uri;

        try (Jedis jedis = RedisConnection.getJedis()) {

            String countStr = jedis.get(redisKey);
            int    count    = (countStr == null) ? 0 : Integer.parseInt(countStr);

            if (count >= MAX_REQUEST) {
                //too many requests
                logger.warn("Rate limit exceeded for IP: {} on {}", ip, uri);
                resp.setStatus(429);
                resp.setContentType("application/json");
                resp.getWriter().write(
                    "{\"error\": \"Too many requests. Please wait and try again.\"}"
                );
                return;
            }

            //increment counter
            jedis.incr(redisKey);

            //set expiry only on first request
            if (count == 0) {
                jedis.expire(redisKey, WINDOW_SEC);
            }

        } catch (Exception e) {
            //if Redis is down don't block the request
            logger.error("RateLimitFilter Redis error: {}", e.getMessage());
        }

        chain.doFilter(request, response);
    }
}