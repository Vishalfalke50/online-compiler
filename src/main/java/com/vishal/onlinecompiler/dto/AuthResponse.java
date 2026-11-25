
package com.vishal.onlinecompiler.dto;

// Response DTO used to send authentication details to the frontend
public class AuthResponse {

    // Stores the JWT token generated after successful authentication
    private String token;

    // Stores the username of the authenticated user
    private String username;

    // Stores the authentication success message
    private String message;

    // Constructor to initialize all authentication response fields
    public AuthResponse(String token, String username, String message) {
        this.token = token;
        this.username = username;
        this.message = message;
    }

    // Returns the JWT token
    public String getToken() {
        return token;
    }

    // Returns the authenticated username
    public String getUsername() {
        return username;
    }

    // Returns the response message
    public String getMessage() {
        return message;
    }
}

/*
1. AuthResponse.java
This DTO sends the authentication result from the backend to the frontend after successful
 registration or login.

 2. Understand with an example
After successful login, the backend can send this JSON response:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "vishal",
  "message": "Login successful"
}


- token — JWT token used for authenticated requests.
- username — identifies the logged-in user.
- message — displays the result to the frontend.
*/