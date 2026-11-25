package com.vishal.onlinecompiler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// Response received from Judge0 after the code is executed
@JsonIgnoreProperties(ignoreUnknown = true)
public class Judge0Response {

    // Normal program output
    private String stdout;

    // Runtime error output
    private String stderr;

    // Compiler error messages
    @JsonProperty("compile_output")
    private String compileOutput;

    // Execution time in seconds
    private String time;

    // Memory used in KB
    private Integer memory;

    // Execution status, for example Accepted or Compilation Error
    private Status status;

    public Judge0Response() {
    }

    public String getStdout() { return stdout; }
    public void setStdout(String stdout) { this.stdout = stdout; }
    public String getStderr() { return stderr; }
    public void setStderr(String stderr) { this.stderr = stderr; }
    public String getCompileOutput() { return compileOutput; }
    public void setCompileOutput(String compileOutput) { this.compileOutput = compileOutput; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public Integer getMemory() { return memory; }
    public void setMemory(Integer memory) { this.memory = memory; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    // Nested class for the status object: {"id": 3, "description": "Accepted"}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Status {
        private int id;
        private String description;

        public Status() {
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
/*
-->This DTO holds Judge0's reply. Our service converts it into CodeExecutionResponse
   (output, error, status, time, memory) before sending it to the frontend.
*/