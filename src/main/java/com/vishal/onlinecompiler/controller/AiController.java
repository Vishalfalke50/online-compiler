package com.vishal.onlinecompiler.controller;

import com.vishal.onlinecompiler.dto.AiAnalyzeRequest;
import com.vishal.onlinecompiler.dto.AiAnalyzeResponse;
import com.vishal.onlinecompiler.service.AiService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// REST controller for AI explanation and debugging
@RestController
@RequestMapping("/api/ai")
public class AiController {

    // Service that talks to Gemini
    private final AiService aiService;

    // Spring injects the service automatically
    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    // POST /api/ai/analyze: sends the code to Gemini and returns the explanation
    @PostMapping("/analyze")
    public ResponseEntity<AiAnalyzeResponse> analyze(@Valid @RequestBody AiAnalyzeRequest request) {
        return ResponseEntity.ok(aiService.analyze(request));
    }
}
/*
-->1. Purpose
   AiController receives the AI request from the frontend and passes it to AiService.

-->2. Security
   This endpoint is protected. It needs a valid JWT token, like /api/code/run.
*/