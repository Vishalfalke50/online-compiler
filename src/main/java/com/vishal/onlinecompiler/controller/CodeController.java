package com.vishal.onlinecompiler.controller;

import com.vishal.onlinecompiler.dto.CodeExecutionRequest;
import com.vishal.onlinecompiler.dto.CodeExecutionResponse;
import com.vishal.onlinecompiler.service.CodeExecutionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.vishal.onlinecompiler.dto.SubmissionResponse;
import com.vishal.onlinecompiler.service.HistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

// REST controller for running code
@RestController
@RequestMapping("/api/code")
public class CodeController {

    // Services used by this controller
    private final CodeExecutionService codeExecutionService;
    private final HistoryService historyService;

    // Spring injects both services automatically
    public CodeController(CodeExecutionService codeExecutionService,
                          HistoryService historyService) {
        this.codeExecutionService = codeExecutionService;
        this.historyService = historyService;
    }

    // POST /api/code/run: runs the submitted code and returns the result
    @PostMapping("/run")
    public ResponseEntity<CodeExecutionResponse> run(@Valid @RequestBody CodeExecutionRequest request,
                                                     Authentication authentication) {
        // authentication.getName() is the username taken from the JWT token
        return ResponseEntity.ok(codeExecutionService.run(request, authentication.getName()));
    }
    @GetMapping("/history")
    public ResponseEntity<List<SubmissionResponse>> history(Authentication authentication) {
        return ResponseEntity.ok(historyService.getHistory(authentication.getName()));
    }
}
/*
-->This endpoint is protected: SecurityConfig requires a valid JWT token for /api/code/**.
   The username comes from the token, so users can never run code as someone else.
*/