
package com.vishal.onlinecompiler.dto;

// Used to validate incoming registration request data
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    // Ensures username is not null, empty or blank
    @NotBlank(message = "Username is required")
    @Size(max = 50, message = "Username cannot exceed 50 characters")
    private String username;

    // Ensures email is valid and not blank
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    // Ensures password is not blank and has at least 8 characters
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must contain 8 to 100 characters")
    private String password;

    // Default constructor for request data binding
    public RegisterRequest() {
    }

    // Returns the username
    public String getUsername() {
        return username;
    }

    // Sets the username
    public void setUsername(String username) {
        this.username = username;
    }

    // Returns the email
    public String getEmail() {
        return email;
    }

    // Sets the email
    public void setEmail(String email) {
        this.email = email;
    }

    // Returns the password
    public String getPassword() {
        return password;
    }

    // Sets the password
    public void setPassword(String password) {
        this.password = password;
    }
}


/**
 1. What is DTO?
 DTO (Data Transfer Object) is a Java object used to transfer data between different layers
 of an application.
 In our Online Compiler project, DTOs will transfer data between the frontend and backend
 through REST APIs.

 Example: User Registration
 The frontend sends:
 - Username
 - Email
 - Password
 Our backend receives this data using a RegistrationRequest DTO.

 2. Why do we need DTOs?
 1. Security: We don't expose sensitive entity fields such as database IDs or password hashes.
 2. Data control: We decide which fields the frontend can send or receive.
 3. Validation: We can validate incoming data using annotations.
 4. Separation: DTOs keep API data separate from database entities.
 3. Entity vs DTO

 Entity	DTO
 Represents database table	Transfers data
 Maps to database	Maps to API request/response
 Uses JPA annotations	Can use validation annotations
 Example: User	Example: RegistrationRequest


 */