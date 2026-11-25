package com.vishal.onlinecompiler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

// Configures separate RestClients for Judge0, Gemini and Groq
@Configuration
public class RestClientConfig {

    // Creates a RestClient for Judge0 code execution
    @Bean("judge0RestClient")
    public RestClient judge0RestClient(@Value("${judge0.url}") String url) {
        return RestClient.builder()
                .baseUrl(url)
                .build();
    }

    // Creates a RestClient for Gemini AI analysis
    @Bean("geminiRestClient")
    public RestClient geminiRestClient(
            @Value("${gemini.url}") String url,
            @Value("${gemini.api.key}") String apiKey) {

        return RestClient.builder()
                .baseUrl(url)
                .defaultHeader("x-goog-api-key", apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    // Creates a RestClient for Groq, the fallback AI provider
    @Bean("groqRestClient")
    public RestClient groqRestClient(
            @Value("${groq.url}") String url,
            @Value("${groq.api.key:}") String apiKey) {

        return RestClient.builder()
                .baseUrl(url)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
/*
-->One pre-configured HTTP client per external API. Services pick the right one by name.
*/