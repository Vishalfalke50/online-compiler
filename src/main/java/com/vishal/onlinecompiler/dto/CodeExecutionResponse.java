
package com.vishal.onlinecompiler.dto;

// Response DTO used to send code execution results to the frontend
public class CodeExecutionResponse {

    // Stores the output produced by the executed program
    private String output;

    // Stores error messages produced during execution
    private String error;

    // Stores the execution status, such as Accepted or Compilation Error
    private String status;

    // Stores the execution time returned by Judge0
    private String time;

    // Stores the memory consumed during execution
    private String memory;

    // Constructor to initialize all execution response fields
    public CodeExecutionResponse(String output, String error, String status,
                                 String time, String memory) {
        this.output = output;
        this.error = error;
        this.status = status;
        this.time = time;
        this.memory = memory;
    }

    // Returns the program output
    public String getOutput() {
        return output;
    }

    // Returns the execution error
    public String getError() {
        return error;
    }

    // Returns the execution status
    public String getStatus() {
        return status;
    }

    // Returns the execution time
    public String getTime() {
        return time;
    }

    // Returns the memory usage
    public String getMemory() {
        return memory;
    }
}
/*
-->This DTO sends the code execution result from the backend to the frontend after
   Judge0 executes the submitted code.

 2. Example response
{
  "output": "Hello World",
  "error": null,
  "status": "Accepted",
  "time": "0.02",
  "memory": "3500"
}

Remember: These values will come from Judge0's execution response. We will map them in the
          Service layer later.
*/