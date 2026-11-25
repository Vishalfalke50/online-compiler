package com.vishal.onlinecompiler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

// Maps the response received from Groq (OpenAI chat format)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroqResponse {

    // Stores the possible answers returned by Groq
    private List<Choice> choices;

    public List<Choice> getChoices() { return choices; }
    public void setChoices(List<Choice> choices) { this.choices = choices; }

    // Returns the answer text, or null if any level is missing
    public String extractText() {
        if (choices == null || choices.isEmpty() || choices.get(0).getMessage() == null) {
            return null;
        }
        return choices.get(0).getMessage().getContent();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice {
        private Message message;
        public Message getMessage() { return message; }
        public void setMessage(Message message) { this.message = message; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        private String content;
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}
/*
-->Groq replies with: choices -> message -> content. extractText() hides that nesting.
*/