package com.ecommerce.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET_KEY = 
        "myEcommerceSecretKeyThatIsLongEnoughForHS256!!";

    private static final Key SIGNING_KEY =
        Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    //generate JWT token
    public static String generateToken(int id, String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("id", id)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    //validate token
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SIGNING_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Extract all claims
    public static Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SIGNING_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract username
    public static String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Extract role
    public static String extractRole(String token) {
        return (String) extractClaims(token).get("role");
    }

    // Extract user id
    public static Integer extractUserId(String token) {
        return (Integer) extractClaims(token).get("id");
    }
}