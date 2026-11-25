package com.vishal.onlinecompiler.dto;

import java.time.LocalDateTime;

// Response DTO used to send one code submission from the history to the frontend
public class SubmissionResponse {

    // Stores the unique submission ID
    private Long id;

    // Stores the programming language
    private String language;

    // Stores the submitted source code
    private String sourceCode;

    // Stores the input given to the program
    private String stdin;

    // Stores the program output or error message
    private String output;

    // Stores the execution status, such as Accepted or Compilation Error
    private String status;

    // Stores the AI explanation, if the user asked for one
    private String aiResponse;

    // Stores the date and time of the submission
    private LocalDateTime createdAt;

    // Constructor to initialize all submission response fields
    public SubmissionResponse(Long id, String language, String sourceCode, String stdin,
                              String output, String status, String aiResponse,
                              LocalDateTime createdAt) {
        this.id = id;
        this.language = language;
        this.sourceCode = sourceCode;
        this.stdin = stdin;
        this.output = output;
        this.status = status;
        this.aiResponse = aiResponse;
        this.createdAt = createdAt;
    }

    // Returns the submission ID
    public Long getId() {
        return id;
    }

    // Returns the programming language
    public String getLanguage() {
        return language;
    }

    // Returns the source code
    public String getSourceCode() {
        return sourceCode;
    }

    // Returns the program input
    public String getStdin() {
        return stdin;
    }

    // Returns the program output
    public String getOutput() {
        return output;
    }

    // Returns the execution status
    public String getStatus() {
        return status;
    }

    // Returns the AI explanation
    public String getAiResponse() {
        return aiResponse;
    }

    // Returns the submission time
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
/*
-->This DTO sends one past code run from the backend to the frontend, for the history page.
-->2. Example JSON response
{
  "id": 7,
  "language": "Java",
  "sourceCode": "public class Main { ... }",
  "stdin": "10",
  "output": "Hello World",
  "status": "Accepted",
  "aiResponse": null,
  "createdAt": "2026-11-24T10:30:00"
}
-->SavedCodeResponse is for programs the user chose to save; SubmissionResponse is for
   the history of every run.
*/