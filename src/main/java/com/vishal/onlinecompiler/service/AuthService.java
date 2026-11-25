package com.vishal.onlinecompiler.service;

import com.vishal.onlinecompiler.dto.AuthResponse;
import com.vishal.onlinecompiler.dto.LoginRequest;
import com.vishal.onlinecompiler.dto.RegisterRequest;
import com.vishal.onlinecompiler.entity.User;
import com.vishal.onlinecompiler.repository.UserRepository;
import com.vishal.onlinecompiler.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// Service class that contains the business logic for registration and login
@Service
public class AuthService {

    // Used to save and find users in the app_users table
    private final UserRepository userRepository;

    // Used to hash passwords with BCrypt
    private final PasswordEncoder passwordEncoder;

    // Used to check the username and password during login
    private final AuthenticationManager authenticationManager;

    // Used to create the JWT token after a successful login
    private final JwtUtil jwtUtil;

    // Spring injects all four objects automatically
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    // Registers a new user after checking that the username and email are not already used
    public AuthResponse register(RegisterRequest request) {

        // Reject duplicate username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        }

        // Reject duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        // Build the entity; the password is hashed, never stored as plain text
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Save the new user in Oracle
        userRepository.save(user);

        // No token yet; the user must log in after registering
        return new AuthResponse(null, user.getUsername(), "Registration successful");
    }

    // Checks the credentials and returns a JWT token if they are correct
    public AuthResponse login(LoginRequest request) {
        try {
            // Spring loads the user, then compares the typed password with the BCrypt hash
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()));
        } catch (AuthenticationException e) {
            // Same message for wrong username and wrong password, so attackers learn nothing
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        // Credentials are correct, so create the token
        String token = jwtUtil.generateToken(request.getUsername());

        return new AuthResponse(token, request.getUsername(), "Login successful");
    }
}
/*
-->1. Purpose
   AuthService holds the logic for registering and logging in users.
   The controller only receives requests; this class does the actual work.

-->2. How register works
   RegisterRequest → check duplicate username and email
   → hash the password with BCrypt → save the User in app_users
   → return "Registration successful"

-->3. How login works
   LoginRequest → AuthenticationManager checks the password against the hash
   → if correct, JwtUtil creates a token → return the token and username
   → if wrong, return 401 "Invalid username or password"

-->4. Note
   ResponseStatusException is used for now. In the exception handling step we will
   replace it with custom exceptions handled by GlobalExceptionHandler.
*/