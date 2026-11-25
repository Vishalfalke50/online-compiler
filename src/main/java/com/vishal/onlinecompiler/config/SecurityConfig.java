package com.vishal.onlinecompiler.config;

import com.vishal.onlinecompiler.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Central security settings: password hashing, public endpoints and the JWT filter
@Configuration      // tells Spring this class contains bean definitions
@EnableWebSecurity  // turns on Spring Security for the web layer
public class SecurityConfig {

    // Our JWT filter, added to the security chain below
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Spring injects the filter automatically
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // BCrypt hashes passwords before they are stored in the database
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Used by the login service to check the username and password
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    // Defines which endpoints are public and which need a token
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF protection is not needed because we use tokens, not browser sessions
                .csrf(csrf -> csrf.disable())

                // No server-side session; every request must carry its own token
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Register and login are public
                        .requestMatchers("/api/auth/**").permitAll()
                        // Allows Spring's error page, so real error messages are not hidden behind 403
                        .requestMatchers("/error").permitAll()
                        // Frontend files are public
                        .requestMatchers("/", "/*.html", "/css/**", "/js/**").permitAll()
                        // Everything else needs a valid token
                        .anyRequest().authenticated())

                // Run our JWT check before Spring's default login filter
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        // Build and return the final security configuration
        return http.build();
    }
}
/*
-->1. Purpose
   SecurityConfig is the control room of security. It decides how passwords are
   hashed, which URLs are public, and where the JWT filter runs.

-->2. How it works
   /api/auth/register and /api/auth/login → public, no token needed
   /index.html, /css, /js                 → public, so the frontend loads
   every other endpoint                   → needs a valid JWT token
   JwtAuthenticationFilter                → checks the token before each request

-->3. Why STATELESS?
   The server does not remember logged-in users. The token itself proves who
   the user is, which keeps the backend simple and scalable.

-->4. Why BCrypt?
   It is a one-way hash. Even if the database is leaked, the original passwords
   cannot be read.
*/