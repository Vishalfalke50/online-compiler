package com.vishal.onlinecompiler.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Entity class mapped to the app_users table; stores registered user details
@Entity // marks this class as a JPA entity, so Hibernate maps it to a database table
@Table(name = "app_users") // table name in Oracle (USER is a reserved word, so we avoid it)
public class User {

    @Id // primary key of the table
    @GeneratedValue(strategy = GenerationType.IDENTITY) // database auto-generates the id value
    private Long id;

    @Column(nullable = false, unique = true, length = 50) // required, no duplicates, max 50 characters
    private String username;

    @Column(nullable = false, unique = true, length = 100) // required, no duplicates, max 100 characters
    private String email;

    @Column(nullable = false) // required; stores the BCrypt hashed password, never plain text
    private String password;

    @Column(nullable = false, length = 20) // required; defaults to ROLE_USER
    private String role = "ROLE_USER";

    @Column(name = "created_at", nullable = false, updatable = false) // set once, never updated
    private LocalDateTime createdAt;

    @PrePersist // runs automatically just before the row is first saved
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public User() {}

    public Long getId() { return id; }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
/*
    User class: "It stores the details of registered users, like username, email,
                 encrypted password and role."

*/