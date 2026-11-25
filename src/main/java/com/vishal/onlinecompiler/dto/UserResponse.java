package com.vishal.onlinecompiler.dto;

import java.time.LocalDateTime;

// Response DTO used to send safe user profile details to the frontend
public class UserResponse {

    // Stores the unique user ID
    private Long id;

    // Stores the username
    private String username;

    // Stores the email address
    private String email;

    // Stores the user role, such as ROLE_USER
    private String role;

    // Stores the date and time when the account was created
    private LocalDateTime createdAt;

    // Constructor to initialize all user response fields
    public UserResponse(Long id, String username, String email,
                        String role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Returns the user ID
    public Long getId() {
        return id;
    }

    // Returns the username
    public String getUsername() {
        return username;
    }

    // Returns the email address
    public String getEmail() {
        return email;
    }

    // Returns the user role
    public String getRole() {
        return role;
    }

    // Returns the account creation time
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
/*
-->This DTO sends safe user profile details to the frontend.
-->2. Example JSON response
{
  "id": 1,
  "username": "vishal",
  "email": "vishal@example.com",
  "role": "ROLE_USER",
  "createdAt": "2026-11-24T10:30:00"
}
-->Important: The password field is intentionally left out, so the BCrypt hash
   never leaves the server.
*/