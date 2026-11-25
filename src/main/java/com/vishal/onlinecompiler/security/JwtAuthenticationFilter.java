package com.vishal.onlinecompiler.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Filter that runs once for every request and checks the JWT token
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Used to validate the token and read the username from it
    private final JwtUtil jwtUtil;

    // Used to load the user from the database
    private final CustomUserDetailsService userDetailsService;

    // Spring injects both objects automatically
    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    // Runs before the controller for every request
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Read the Authorization header sent by the frontend
        String header = request.getHeader("Authorization");

        // The token arrives in this format: Authorization: Bearer <token>
        if (header != null && header.startsWith("Bearer ")) {
            // Remove the word "Bearer " to get only the token
            String token = header.substring(7);

            // Continue only if the token is valid and nobody is logged in for this request yet
            if (jwtUtil.isTokenValid(token)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Find out who the token belongs to
                String username = jwtUtil.extractUsername(token);

                // Load that user from the database
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Create the login object for Spring Security (no password needed, the token is the proof)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                // Attach request details such as the IP address
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                // Mark the user as logged in for this request only
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Pass the request on to the next filter or the controller
        filterChain.doFilter(request, response);
    }
}
/*
-->1. Purpose
   This filter checks the JWT token on every request, so protected endpoints like
   /api/code/run only work for logged-in users.

-->2. How it works
   Request arrives with header "Authorization: Bearer <token>"
   → filter extracts the token
   → JwtUtil checks the signature and expiry
   → the username is read from the token and the user is loaded from the database
   → the user is marked as logged in
   → the request continues to the controller

-->3. If there is no token or it is invalid
   The user stays not logged in, and Spring Security rejects the request
   on any protected endpoint.

-->4. Why OncePerRequestFilter?
   It guarantees the check runs exactly one time for each request.
*/