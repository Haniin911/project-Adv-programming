package com.ecommerce.filter;

import com.ecommerce.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
//logger package
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebFilter(urlPatterns = {
    "/products/add",
    "/products/delete",
    "/account/delete",
    "/reviews/add"
})
public class AuthFilter implements Filter {
    //logger instance
    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;

        //session validation
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            //session exist for user
            logger.info("AuthFilter: session valid for {}",
                        session.getAttribute("username"));
            //ok user validated
            chain.doFilter(request, response);
            return;
        }

        //if user not logged in with browser and used client api (postman) 
        //validate using bearer token
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            //if valid token then extract user data
            if (JwtUtil.validateToken(token)) {
                req.setAttribute("userId",   JwtUtil.extractUserId(token));
                req.setAttribute("username", JwtUtil.extractUsername(token));
                req.setAttribute("role",     JwtUtil.extractRole(token));
                logger.info("JWT valid", JwtUtil.extractUsername(token));
                chain.doFilter(request, response);
                return;
            }
        }
        //if no access
        logger.warn("unauthorized access");
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}