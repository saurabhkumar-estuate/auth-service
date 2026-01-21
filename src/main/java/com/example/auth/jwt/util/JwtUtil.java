package com.example.auth.jwt.util;

import java.security.Key;
import java.util.Date;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component; // Add this
import com.example.auth.config.JwtProperties; // Add this

@Component // This tells Spring to manage this class
public class JwtUtil {
    
    private final Key key;
    private final long expiration;

    // Spring will automatically inject JwtProperties here
    public JwtUtil(JwtProperties jwtProperties) {
        if (jwtProperties.getSecret() == null) {
            throw new IllegalStateException("JWT Secret is missing! Check Config Server/Application properties.");
        }
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
        this.expiration = jwtProperties.getExpiration();
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public void validateToken(String token) {
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}