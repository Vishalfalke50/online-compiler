package com.vishal.onlinecompiler.dto;

// Used to validate incoming code execution request data
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CodeExecutionRequest {

    // Ensures that source code is provided
    @NotBlank(message = "Source code is required")
    private String sourceCode;

    // Stores the programming language selected by the user
    @NotBlank(message = "Programming language is required")
    @Size(max = 30, message = "Language name cannot exceed 30 characters")
    private String language;

    // Stores the input provided to the program (optional)
    private String stdin;

    // Default constructor for request data binding
    public CodeExecutionRequest() {
    }

    // Returns the source code
    public String getSourceCode() {
        return sourceCode;
    }

    // Sets the source code
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    // Returns the selected programming language
    public String getLanguage() {
        return language;
    }

    // Sets the selected programming language
    public void setLanguage(String language) {
        this.language = language;
    }

    // Returns the program input
    public String getStdin() {
        return stdin;
    }

    // Sets the program input
    public void setStdin(String stdin) {
        this.stdin = stdin;
    }
}

/*
 -->This DTO will receive the code execution details from the frontend when a user clicks
    the Run button.
 -->2. Understand the fields
       Field	    Purpose	                    Example
       sourceCode	Code written by user	    System.out.println("Hello");
       language	    Selected language	        Java
        stdin	     Input given to program 	10
 -->One important design decision: The frontend sends the language name (Java), not the Judge0
    language ID. Our backend will map the language name to the correct Judge0 ID.
*/