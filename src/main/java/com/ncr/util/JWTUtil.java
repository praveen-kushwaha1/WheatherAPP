package com.ncr.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    /*
     * SECRET KEY:
     * Used to sign and verify JWT tokens.
     *
     * IMPORTANT:
     * In a real application, don't hard-code the secret.
     * Store it in environment variables or application.properties.
     */
    private static final String SECRET = "my-super-secret-key-that-is-long-enough-1234567890!@#";

    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    // Token validity: 1 hour
    private static final long EXPIRATION_TIME = 1000L * 60 * 60;

    /*
     * GENERATE JWT:
     *
     * username
     *    ↓
     * JWT Subject
     *    ↓
     * Issued At + Expiration
     *    ↓
     * Sign with Secret Key
     *    ↓
     * JWT Token
     *
     * Example:
     *
     * Header.Payload.Signature
     */
    public String generateToken(String username) {

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /*
     * EXTRACT USERNAME:
     *
     * JWT
     *  ↓
     * Claims
     *  ↓
     * Subject
     *  ↓
     * Username
     */
    public String extractUsername(String token) {

        return extractClaims(token).getSubject();
    }

    /*
     * EXTRACT CLAIMS:
     *
     * 1. Parse the JWT.
     * 2. Verify its signature using the secret key.
     * 3. Read the claims/payload.
     *
     * If the token is invalid or the signature doesn't match,
     * parsing will throw an exception.
     */
    private Claims extractClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token).getBody();
    }

    /*
     * VALIDATE TOKEN:
     *
     * A token is considered valid when:
     *
     * 1. Username from JWT matches the database user.
     * 2. Token has not expired.
     *
     * JWT
     *  ↓
     * Extract username
     *  ↓
     * Compare with UserDetails
     *  ↓
     * Check expiration
     *  ↓
     * true / false
     */
    public boolean validateToken(String username, UserDetails userDetails, String token) {

        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /*
     * CHECK TOKEN EXPIRATION:
     *
     * JWT contains an "exp" claim.
     *
     * Current time > expiration time
     *          ↓
     *       Expired
     *
     * Current time < expiration time
     *          ↓
     *       Valid
     */
    private boolean isTokenExpired(String token) {

        return extractClaims(token).getExpiration().before(new Date());
    }
}

