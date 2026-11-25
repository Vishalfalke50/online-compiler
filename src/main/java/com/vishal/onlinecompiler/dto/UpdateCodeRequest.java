
package com.vishal.onlinecompiler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Request DTO used to receive updated saved program details
public class UpdateCodeRequest {

    // Stores the updated program title
    @NotBlank(message = "Code title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    // Stores the updated programming language
    @NotBlank(message = "Programming language is required")
    @Size(max = 30, message = "Language cannot exceed 30 characters")
    private String language;

    // Stores the updated source code
    @NotBlank(message = "Source code is required")
    @Size(max = 50000, message = "Source code cannot exceed 50000 characters")
    private String sourceCode;

    // Default constructor for request data binding
    public UpdateCodeRequest() {
    }

    // Returns the updated program title
    public String getTitle() {
        return title;
    }

    // Sets the updated program title
    public void setTitle(String title) {
        this.title = title;
    }

    // Returns the updated programming language
    public String getLanguage() {
        return language;
    }

    // Sets the updated programming language
    public void setLanguage(String language) {
        this.language = language;
    }

    // Returns the updated source code
    public String getSourceCode() {
        return sourceCode;
    }

    // Sets the updated source code
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }
}
/*
-->This DTO receives updated program details when a user edits an already saved program.
-->2. How it works
For example, a user has saved a program with ID 5.

They edit its title or source code and send the updated details to the backend.
- SaveCodeRequest → Creates a new saved program.
- UpdateCodeRequest → Updates an existing saved program.
- SavedCodeResponse → Returns saved program details.
*/