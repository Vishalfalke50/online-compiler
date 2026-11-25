
package com.vishal.onlinecompiler.dto;

// Used for validating incoming login request data
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    // Ensures that the username is not null, empty or blank
    @NotBlank(message = "Username is required")
    private String username;

    // Ensures that the password is not null, empty or blank
    @NotBlank(message = "Password is required")
    private String password;

    // Default constructor required for object creation and request data binding
    public LoginRequest() {
    }

    // Returns the username
    public String getUsername() {
        return username;
    }

    // Sets the username received from the frontend
    public void setUsername(String username) {
        this.username = username;
    }

    // Returns the password
    public String getPassword() {
        return password;
    }

    // Sets the password received from the frontend
    public void setPassword(String password) {
        this.password = password;
    }
}
