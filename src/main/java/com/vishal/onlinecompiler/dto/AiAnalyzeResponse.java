
package com.vishal.onlinecompiler.dto;

// Response DTO used to send AI-generated assistance to the frontend
public class AiAnalyzeResponse  {

    // Stores the AI-generated explanation or response
    private String response;

    // Stores the type of assistance provided by AI
    private String type;

    // Stores the success or failure message
    private String message;

    // Constructor to initialize all AI response fields
    public AiAnalyzeResponse (String response, String type, String message) {
        this.response = response;
        this.type = type;
        this.message = message;
    }

    // Returns the AI-generated response
    public String getResponse() {
        return response;
    }

    // Returns the type of AI assistance
    public String getType() {
        return type;
    }

    // Returns the response message
    public String getMessage() {
        return message;
    }
}
/*
-->This DTO sends the AI-generated explanation, debugging suggestions, or code
   improvements from the backend to the frontend.
2. Example JSON response
{
  "response": "The NullPointerException occurs because you are accessing a method on a null object.",
  "type": "DEBUGGING",
  "message": "Analysis completed successfully"
}

3. Field explanation
Field	    Purpose
response	AI-generated explanation or suggestions
Type	    Type of assistance: EXPLANATION, DEBUGGING, OPTIMIZATION
message	     Status message

*/