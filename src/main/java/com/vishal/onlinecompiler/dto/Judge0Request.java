package com.vishal.onlinecompiler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// Request body sent to Judge0 when we submit code for execution
public class Judge0Request {

    // Source code to run
    @JsonProperty("source_code")
    private String sourceCode;

    // Judge0 language id, for example 62 for Java
    @JsonProperty("language_id")
    private int languageId;

    // Optional input given to the program
    private String stdin;

    public Judge0Request(String sourceCode, int languageId, String stdin) {
        this.sourceCode = sourceCode;
        this.languageId = languageId;
        this.stdin = stdin;
    }

    public String getSourceCode() { return sourceCode; }
    public int getLanguageId() { return languageId; }
    public String getStdin() { return stdin; }
}
/*
-->This DTO is the JSON body we send to Judge0: source_code, language_id and stdin.
   @JsonProperty maps our Java names to the snake_case names Judge0 expects.
*/