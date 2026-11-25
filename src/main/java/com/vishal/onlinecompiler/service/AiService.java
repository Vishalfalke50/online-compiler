package com.vishal.onlinecompiler.service;

import com.vishal.onlinecompiler.dto.AiAnalyzeRequest;
import com.vishal.onlinecompiler.dto.AiAnalyzeResponse;
import com.vishal.onlinecompiler.dto.GeminiResponse;
import com.vishal.onlinecompiler.dto.GroqResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

// Service that asks AI for an explanation, trying several providers until one works
@Service
public class AiService {

    // RestClients for Gemini and Groq
    private final RestClient geminiRestClient;
    private final RestClient groqRestClient;

    // Gemini models tried in order, and the Groq fallback settings
    private final List<String> geminiModels;
    private final String groqModel;
    private final String groqKey;

    // Spring injects the clients and settings automatically
    public AiService(@Qualifier("geminiRestClient") RestClient geminiRestClient,
                     @Qualifier("groqRestClient") RestClient groqRestClient,
                     @Value("${gemini.models}") List<String> geminiModels,
                     @Value("${groq.model}") String groqModel,
                     @Value("${groq.api.key:}") String groqKey) {
        this.geminiRestClient = geminiRestClient;
        this.groqRestClient = groqRestClient;
        this.geminiModels = geminiModels;
        this.groqModel = groqModel;
        this.groqKey = groqKey;
    }

    // Builds a prompt, tries every AI option in order and returns the first answer
    public AiAnalyzeResponse analyze(AiAnalyzeRequest request) {

        // Decide the type ourselves, because the AI does not return one
        boolean hasError = request.getErrorLog() != null && !request.getErrorLog().isBlank();
        String type = hasError ? "DEBUGGING" : "EXPLANATION";
        String prompt = buildPrompt(request, hasError);

        // Try Gemini models first, then Groq
        String text = tryGemini(prompt);
        if (text == null) {
            text = tryGroq(prompt);
        }

        // Every option failed
        if (text == null || text.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "AI is busy right now. Please try again in a moment.");
        }

        return new AiAnalyzeResponse(text, type, "Analysis completed successfully");
    }

    // Tries each Gemini model in order; returns null if all fail
    private String tryGemini(String prompt) {
        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))));

        for (String model : geminiModels) {
            try {
                GeminiResponse result = geminiRestClient.post()
                        .uri("/v1beta/models/" + model.trim() + ":generateContent")
                        .body(body)
                        .retrieve()
                        .body(GeminiResponse.class);

                String text = result == null ? null : result.extractText();
                if (text != null && !text.isBlank()) {
                    return text;
                }
            } catch (RestClientException e) {
                // Busy, retired or rate-limited: log it and try the next model
                System.out.println("Gemini " + model + " failed: " + e.getMessage());
            }
        }
        return null;
    }

    // Tries Groq as the last option; returns null if it has no key or fails
    private String tryGroq(String prompt) {
        if (groqKey == null || groqKey.isBlank()) {
            return null;
        }

        Map<String, Object> body = Map.of(
                "model", groqModel,
                "messages", List.of(Map.of("role", "user", "content", prompt)));

        try {
            GroqResponse result = groqRestClient.post()
                    .uri("/chat/completions")
                    .body(body)
                    .retrieve()
                    .body(GroqResponse.class);

            return result == null ? null : result.extractText();
        } catch (RestClientException e) {
            System.out.println("Groq failed: " + e.getMessage());
            return null;
        }
    }

    // Builds the structured prompt sent to the AI
    private String buildPrompt(AiAnalyzeRequest request, boolean hasError) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a helpful programming tutor. Answer clearly and briefly.\n");
        sb.append("Language: ").append(request.getLanguage()).append("\n\n");
        sb.append("Code:\n").append(request.getSourceCode()).append("\n\n");
        if (hasError) {
            sb.append("Error output:\n").append(request.getErrorLog()).append("\n\n");
        }
        sb.append("Task: ").append(request.getPrompt()).append("\n");
        sb.append("Explain the code, identify any errors and suggest improvements.");
        return sb.toString();
    }
}
/*
-->1. Purpose
   AiService asks AI to explain code and never depends on one provider.

-->2. Fallback chain
   Gemini 3.8 -> Gemini 3.7 -> Gemini 3.6 -> Groq -> friendly 503 message

-->3. Why it fixes the 502
   A busy or retired model only skips to the next option. The user sees an error
   only if every option is down at the same time.
*/