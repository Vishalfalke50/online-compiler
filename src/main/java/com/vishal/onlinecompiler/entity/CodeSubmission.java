
package com.vishal.onlinecompiler.entity;

import jakarta.persistence.*;
        import java.time.LocalDateTime;

// Entity class mapped to the code_submissions table; stores each code run and its result
@Entity
@Table(name = "code_submissions")
public class CodeSubmission {

    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // database auto-generates the id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // many submissions belong to one user
    @JoinColumn(name = "user_id", nullable = false) // foreign key column pointing to app_users
    private User user;

    @Column(nullable = false, length = 30) // language name, e.g. Java
    private String language;

    @Column(name = "language_id", nullable = false) // Judge0 language id, e.g. 62 for Java
    private Integer languageId;

    @Lob // large text stored as CLOB
    @Column(name = "source_code", nullable = false)
    private String sourceCode;

    @Lob // custom input given by the user
    private String stdin;

    @Lob // stdout, stderr or compile output from Judge0
    private String output;

    @Column(length = 50) // e.g. Accepted, Compilation Error
    private String status;

    @Lob // AI explanation from Gemini
    @Column(name = "ai_response")
    private String aiResponse;

    @Column(name = "created_at", nullable = false, updatable = false) // set once, never updated
    private LocalDateTime createdAt;
    @Column(length = 100) // title of the saved program, optional for plain runs
    private String title;
    @PrePersist // runs just before the row is first saved
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public CodeSubmission() {}

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public Integer getLanguageId() { return languageId; }
    public void setLanguageId(Integer languageId) { this.languageId = languageId; }
    public String getSourceCode() { return sourceCode; }
    public void setSourceCode(String sourceCode) { this.sourceCode = sourceCode; }
    public String getStdin() { return stdin; }
    public void setStdin(String stdin) { this.stdin = stdin; }
    public String getOutput() { return output; }
    public void setOutput(String output) { this.output = output; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAiResponse() { return aiResponse; }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}

/*

CodeSubmission class: "It stores each code run by a user, with the language,
                       source code, input, output, status and AI response,
                       and links it to that user."
*/
 /*
 1. What is Entity?
Entity is a Java class mapped to a database table using JPA annotations. It represents
the structure of a database table and its records.

Why do we need Entities?
1. Database mapping: Maps Java classes to database tables.
2. Data storage: Represents the data stored in database rows.
3. ORM: Allows Hibernate to perform database operations using Java objects.
4. Relationships: Defines relationships between tables, such as One-to-One, One-to-Many and Many-to-One.

Example: In our project:
- User entity → APP_USERS table.
- CodeSubmission entity → CODE_SUBMISSIONS table.

 */