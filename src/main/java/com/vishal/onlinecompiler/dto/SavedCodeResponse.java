
package com.vishal.onlinecompiler.dto;

import java.time.LocalDateTime;

// Response DTO used to send saved program details to the frontend
public class SavedCodeResponse {

    // Stores the unique saved code ID
    private Long id;

    // Stores the title of the saved program
    private String title;

    // Stores the programming language
    private String language;

    // Stores the saved source code
    private String sourceCode;

    // Stores the date and time when the code was saved
    private LocalDateTime createdAt;

    // Stores the date and time when the code was last updated
    private LocalDateTime updatedAt;

    // Constructor to initialize all saved code response fields
    public SavedCodeResponse(Long id, String title, String language,
                             String sourceCode, LocalDateTime createdAt,
                             LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.language = language;
        this.sourceCode = sourceCode;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Returns the saved code ID
    public Long getId() {
        return id;
    }

    // Returns the program title
    public String getTitle() {
        return title;
    }

    // Returns the programming language
    public String getLanguage() {
        return language;
    }

    // Returns the saved source code
    public String getSourceCode() {
        return sourceCode;
    }

    // Returns the creation date and time
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Returns the last updated date and time
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
/*
 -->This DTO returns the saved program details from the backend to the frontend when a
   user views their saved code.
 2. Example JSON response
{
  "id": 1,
  "title": "My First Java Program",
  "language": "Java",
  "sourceCode": "public class Main { ... }",
  "createdAt": "2026-11-24T10:30:00",
  "updatedAt": "2026-11-24T10:30:00"
}
- SaveCodeRequest → Frontend sends saved program details to backend.
- SavedCodeResponse → Backend sends saved program details to frontend.
Important: This feature will eventually need its own SavedCode entity and SavedCodeRepository.
 We haven't created those yet.

*/