package com.vishal.onlinecompiler.controller;

import com.vishal.onlinecompiler.dto.AuthResponse;
import com.vishal.onlinecompiler.dto.LoginRequest;
import com.vishal.onlinecompiler.dto.RegisterRequest;
import com.vishal.onlinecompiler.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// REST controller that exposes the registration and login endpoints
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Service that contains the registration and login logic
    private final AuthService authService;

    // Spring injects the service automatically
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/auth/register: creates a new user account
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        // 201 Created means a new resource was made
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    // POST /api/auth/login: checks credentials and returns a JWT token
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }
}
/*
-->1. Purpose
   AuthController receives HTTP requests for registration and login and passes
   them to AuthService.

-->2. How it works
   Frontend sends JSON → Jackson converts it into RegisterRequest or LoginRequest
   → @Valid checks the validation rules → controller calls the service
   → service result is sent back as JSON

-->3. Why @Valid?
   It runs the annotations we wrote in the DTOs, like @NotBlank and @Size,
   before the service is called. Invalid input is rejected with 400.

-->4. Layers
   Controller → Service → Repository → Oracle
*/