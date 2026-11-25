
package com.vishal.onlinecompiler.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Entity class mapped to the saved_codes table; stores programs a user chooses to save
@Entity
@Table(name = "saved_codes")
public class SavedCode {

    // Marks this field as the primary key
    @Id

    // Automatically generates the primary key value
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Defines a many-to-one relationship; many saved programs can belong to one user
    @ManyToOne(fetch = FetchType.LAZY, optional = false)

    // Maps the user_id foreign key column to the User entity
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Stores the program title; cannot be null and has a maximum length of 100 characters
    @Column(nullable = false, length = 100)
    private String title;

    // Stores the programming language; cannot be null and has a maximum length of 30 characters
    @Column(nullable = false, length = 30)
    private String language;

    // Stores large source code text in the database as a CLOB
    @Lob
    @Column(name = "source_code", nullable = false)
    private String sourceCode;

    // Stores the creation timestamp and prevents it from being changed during updates
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Stores the last modification timestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Sets creation and update timestamps before the first database insert
    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    // Updates the modification timestamp before every database update
    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Default constructor required by JPA
    public SavedCode() {
    }

    // Returns the saved code ID
    public Long getId() {
        return id;
    }

    // Returns the associated user
    public User getUser() {
        return user;
    }

    // Sets the associated user
    public void setUser(User user) {
        this.user = user;
    }

    // Returns the program title
    public String getTitle() {
        return title;
    }

    // Sets the program title
    public void setTitle(String title) {
        this.title = title;
    }

    // Returns the programming language
    public String getLanguage() {
        return language;
    }

    // Sets the programming language
    public void setLanguage(String language) {
        this.language = language;
    }

    // Returns the saved source code
    public String getSourceCode() {
        return sourceCode;
    }

    // Sets the saved source code
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    // Returns the creation timestamp
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Returns the last modification timestamp
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
