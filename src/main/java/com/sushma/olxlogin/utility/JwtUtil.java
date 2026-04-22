package com.sushma.olxlogin.utility;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
 
/**
 * JwtTokenUtil handles:
 *  - Token generation from a username
 *  - Parsing / extracting claims
 *  - Token validation
 *
 * Secret key configured via jwt.secret in application.properties.
 * JJWT 0.11.x requires a Key object, so the plain-text secret is
 * expanded with HMAC-SHA256 via Keys.hmacShaKeyFor().
 */
@Component
public class JwtUtil {
 
    @Value("${jwt.secret}")
    private String secretString;
 
    @Value("${jwt.expiration.ms}")
    private long expirationMs;
 
    private Key signingKey;
 
    /**
     * Build the Key after Spring injects the property value.
     * Keys.hmacShaKeyFor() pads short secrets to the required 256-bit minimum.
     */
    @PostConstruct
    public void init() {
        // Pad the secret to at least 32 bytes (256 bits) as required by HS256
        byte[] keyBytes = secretString.getBytes();
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
            signingKey = Keys.hmacShaKeyFor(padded);
        } else {
            signingKey = Keys.hmacShaKeyFor(keyBytes);
        }
    }
 
    // ──────────────────────────────────────────────
    //  Token generation
    // ──────────────────────────────────────────────
 
    /**
     * Generates a signed JWT containing the username as the subject.
     *
     * @param userName authenticated user's username
     * @return compact JWT string (the auth-token returned to the client)
     */
    public String generateToken(String userName) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
 
        return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
 
    // ──────────────────────────────────────────────
    //  Claims extraction
    // ──────────────────────────────────────────────
 
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }
 
    public Date getExpirationFromToken(String token) {
        return parseClaims(token).getExpiration();
    }
 
    // ──────────────────────────────────────────────
    //  Validation
    // ──────────────────────────────────────────────
 
    /**
     * Returns true if the token is structurally valid, properly signed,
     * not expired, and belongs to the supplied UserDetails subject.
     */
    public boolean validateToken(String token, String username) {
        try {
            String tokenUsername = getUsernameFromToken(token);
            return tokenUsername.equals(username) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
 
    /**
     * Lightweight check used by /user/token/validate – does not require a username.
     * Returns true when the token parses and is not expired.
     */
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
 
    // ──────────────────────────────────────────────
    //  Helpers
    // ──────────────────────────────────────────────
 
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
 
    private boolean isTokenExpired(String token) {
        return getExpirationFromToken(token).before(new Date());
    }
    
    public String extractBearerToken(String authHeader) {
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
 