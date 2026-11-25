
package com.vishal.onlinecompiler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Request DTO used to receive AI assistance requests from the frontend
public class AiAnalyzeRequest  {

    // Stores the source code that the user wants the AI to analyze
    @NotBlank(message = "Source code is required")
    private String sourceCode;

    // Stores the programming language of the submitted code
    @NotBlank(message = "Programming language is required")
    private String language;

    // Stores the compilation or runtime error details, if any
    private String errorLog;

    // Stores the user's question or instruction for the AI
    @NotBlank(message = "AI prompt is required")
    @Size(max = 2000, message = "Prompt cannot exceed 2000 characters")
    private String prompt;

    // Default constructor for request data binding
    public AiAnalyzeRequest () {
    }

    // Returns the source code
    public String getSourceCode() {
        return sourceCode;
    }

    // Sets the source code
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    // Returns the programming language
    public String getLanguage() {
        return language;
    }

    // Sets the programming language
    public void setLanguage(String language) {
        this.language = language;
    }

    // Returns the error details
    public String getErrorLog() {
        return errorLog;
    }

    // Sets the error details
    public void setErrorLog(String errorLog) {
        this.errorLog = errorLog;
    }

    // Returns the user's AI prompt
    public String getPrompt() {
        return prompt;
    }

    // Sets the user's AI prompt
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
/*
-->This DTO will receive a user's question or request when they ask the AI assistant
    to explain code, identify errors, or suggest improvements.
-->2. Example request
   {
     "sourceCode": "int a = 10; System.out.println(a);",
     "prompt": "Explain this code."
   }
-->Note: We will connect this DTO to the AI service later. We still need to decide whether
         to use Gemini or OpenAI for your project.
*/