
package com.vishal.onlinecompiler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Request DTO used to receive code that the user wants to save
public class SaveCodeRequest {

    // Stores the title or name of the saved program
    @NotBlank(message = "Code title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    // Stores the programming language
    @NotBlank(message = "Programming language is required")
    @Size(max = 30, message = "Language cannot exceed 30 characters")
    private String language;

    // Stores the source code written by the user
    @NotBlank(message = "Source code is required")
    @Size(max = 50000, message = "Source code cannot exceed 50000 characters")
    private String sourceCode;

    // Default constructor for request data binding
    public SaveCodeRequest() {
    }

    // Returns the saved program title
    public String getTitle() {
        return title;
    }

    // Sets the saved program title
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

    // Returns the source code
    public String getSourceCode() {
        return sourceCode;
    }

    // Sets the source code
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }
}
/*
1. Example JSON request
{
  "title": "My First Java Program",
  "language": "Java",
  "sourceCode": "public class Main { public static void main(String[] args) { System.out.println(\"Hello World\"); } }"
}

Why do we need title?
-->Users may save multiple programs. A title helps them identify each saved program in
   their dashboard.

*/