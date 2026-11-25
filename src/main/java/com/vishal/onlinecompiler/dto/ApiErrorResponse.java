package com.vishal.onlinecompiler.dto;

import java.time.LocalDateTime;
import java.util.Map;

// Response DTO used to send error details to the frontend in a consistent format
public class ApiErrorResponse {

    // Time when the error occurred, set automatically
    private LocalDateTime timestamp;

    // HTTP status code, such as 400 or 401
    private int status;

    // Short HTTP error name, such as Bad Request
    private String error;

    // Readable error message shown to the user
    private String message;

    // Request path where the error occurred
    private String path;

    // Field-level validation errors, such as username -> Username is required
    private Map<String, String> validationErrors;

    // Constructor for general errors
    public ApiErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // Constructor for validation errors with field-level details
    public ApiErrorResponse(int status, String error, String message, String path,
                            Map<String, String> validationErrors) {
        this(status, error, message, path);
        this.validationErrors = validationErrors;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public Map<String, String> getValidationErrors() { return validationErrors; }
}
/*
1. Example JSON response
{
  "timestamp": "2026-11-24T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/auth/register",
  "validationErrors": {
    "username": "Username is required",
    "email": "Please provide a valid email"
  }
}

2. Why do we need it?
-->Instead of returning different error formats from different controllers, we can use one
   consistent response structure.
*/