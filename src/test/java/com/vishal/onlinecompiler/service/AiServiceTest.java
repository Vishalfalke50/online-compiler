
package com.vishal.onlinecompiler.service;

import com.vishal.onlinecompiler.dto.AiAnalyzeRequest;
import com.vishal.onlinecompiler.dto.GeminiResponse;
import com.vishal.onlinecompiler.dto.GroqResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// Unit tests for AiService and its provider fallback mechanism
@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestClient geminiRestClient;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestClient groqRestClient;

    private AiService aiService;

    // Creates a fresh service before every test
    @BeforeEach
    void setUp() {
        aiService = new AiService(
                geminiRestClient,
                groqRestClient,
                List.of("gemini-test-model"),
                "groq-test-model",
                "test-api-key"
        );
    }

    // Test 1: Gemini succeeds and returns an explanation
    @Test
    void analyze_geminiSuccess_returnsExplanation() {

        GeminiResponse geminiResponse = mock(GeminiResponse.class);

        when(geminiResponse.extractText())
                .thenReturn("This code prints Hello World.");

        when(geminiRestClient.post()
                .uri("/v1beta/models/gemini-test-model:generateContent")
                .body(anyMap())
                .retrieve()
                .body(GeminiResponse.class))
                .thenReturn(geminiResponse);

        var response = aiService.analyze(request(null));

        assertEquals("This code prints Hello World.", response.getResponse());
        assertEquals("EXPLANATION", response.getType());
        assertEquals("Analysis completed successfully", response.getMessage());

        verify(groqRestClient, never()).post();
    }

    // Test 2: Gemini fails and Groq successfully provides the answer
    @Test
    void analyze_geminiFails_groqSuccess_returnsResponse() {

        when(geminiRestClient.post()
                .uri(anyString())
                .body(anyMap())
                .retrieve()
                .body(GeminiResponse.class))
                .thenThrow(new RestClientException("Gemini unavailable"));

        GroqResponse groqResponse = mock(GroqResponse.class);

        when(groqResponse.extractText())
                .thenReturn("This code has been explained by Groq.");

        when(groqRestClient.post()
                .uri("/chat/completions")
                .body(anyMap())
                .retrieve()
                .body(GroqResponse.class))
                .thenReturn(groqResponse);

        var response = aiService.analyze(request(null));

        assertEquals("This code has been explained by Groq.", response.getResponse());
        assertEquals("EXPLANATION", response.getType());
        assertEquals("Analysis completed successfully", response.getMessage());
    }

    // Test 3: Both providers fail and service returns HTTP 503
    @Test
    void analyze_allProvidersFail_throws503() {

        when(geminiRestClient.post()
                .uri(anyString())
                .body(anyMap())
                .retrieve()
                .body(GeminiResponse.class))
                .thenThrow(new RestClientException("Gemini unavailable"));

        when(groqRestClient.post()
                .uri("/chat/completions")
                .body(anyMap())
                .retrieve()
                .body(GroqResponse.class))
                .thenThrow(new RestClientException("Groq unavailable"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> aiService.analyze(request(null))
        );

        assertEquals(503, exception.getStatusCode().value());
    }

    // Test 4: Groq key is missing and Gemini also fails
    @Test
    void analyze_groqKeyMissing_throws503() {

        AiService serviceWithoutGroqKey = new AiService(
                geminiRestClient,
                groqRestClient,
                List.of("gemini-test-model"),
                "groq-test-model",
                ""
        );

        when(geminiRestClient.post()
                .uri(anyString())
                .body(anyMap())
                .retrieve()
                .body(GeminiResponse.class))
                .thenThrow(new RestClientException("Gemini unavailable"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> serviceWithoutGroqKey.analyze(request(null))
        );

        assertEquals(503, exception.getStatusCode().value());

        verify(groqRestClient, never()).post();
    }

    // Creates a common AI analysis request for the tests
    private AiAnalyzeRequest request(String errorLog) {

        AiAnalyzeRequest request = new AiAnalyzeRequest();

        request.setSourceCode("System.out.println(\"Hello World\");");
        request.setLanguage("Java");
        request.setPrompt("Explain this code.");
        request.setErrorLog(errorLog);

        return request;
    }
}
