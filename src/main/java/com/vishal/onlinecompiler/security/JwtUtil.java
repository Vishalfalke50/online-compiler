package com.vishal.onlinecompiler.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// Utility class that creates and validates JWT tokens used for login
@Component
public class JwtUtil {

    // Secret key used to sign tokens, built from the secret in application.properties
    private final SecretKey key;

    // Token lifetime in milliseconds
    private final long expirationMs;

    // Reads the secret and expiry from application.properties and builds the signing key
    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    // Generates a signed token that carries the username
    public String generateToken(String username) {
        // Current time, used as the issued time and to calculate the expiry time
        Date now = new Date();
        return Jwts.builder()
                .subject(username)                                      // who the token belongs to
                .issuedAt(now)                                          // when it was created
                .expiration(new Date(now.getTime() + expirationMs))     // when it stops working
                .signWith(key)                                          // sign with our secret key
                .compact();                                             // build the final token string
    }

    // Reads the username stored inside a token
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // A token is valid if its signature is correct and it has not expired
    public boolean isTokenValid(String token) {
        try {
            getClaims(token);   // throws an exception if the token is fake or expired
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Verifies the signature and returns the data (claims) stored in the token
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
/*
-->1. Purpose
   JwtUtil creates a JWT token after login and checks that token on every later request.

-->2. How it works
   Login success      → generateToken("vishal") creates a signed token
   Frontend           → stores the token and sends it with each request
   Next request       → isTokenValid(token) checks the signature and expiry
   Valid token        → extractUsername(token) tells us who the user is

-->3. Why a secret key?
   Only our server knows the secret, so nobody can create or change a token
   without the server detecting it.

-->4. Settings used (application.properties)
   jwt.secret=...          (at least 32 characters)
   jwt.expiration-ms=86400000   (24 hours)
*/