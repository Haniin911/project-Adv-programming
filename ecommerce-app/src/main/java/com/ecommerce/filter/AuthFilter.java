package com.ecommerce.filter;

import com.ecommerce.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
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

    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // ── 1. Check session first (stateful) ──────────────────────────────
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            logger.info("AuthFilter: session valid for {}",
                        session.getAttribute("username"));
            chain.doFilter(request, response);
            return;
        }

        // ── 2. Fall back to JWT in Authorization header (stateless) ────────
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (JwtUtil.validateToken(token)) {
                Claims claims = JwtUtil.extractClaims(token);
                // Inject user info into request so servlets can read it
                req.setAttribute("userId",   JwtUtil.extractUserId(token));
                req.setAttribute("username", JwtUtil.extractUsername(token));
                req.setAttribute("role",     JwtUtil.extractRole(token));
                logger.info("AuthFilter: JWT valid for {}", JwtUtil.extractUsername(token));
                chain.doFilter(request, response);
                return;
            }
        }

        // ── 3. Not authenticated — redirect to login ───────────────────────
        logger.warn("AuthFilter: unauthorized access attempt to {}",
                    req.getRequestURI());
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}