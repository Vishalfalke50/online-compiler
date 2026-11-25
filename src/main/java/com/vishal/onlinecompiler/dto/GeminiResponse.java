
package com.vishal.onlinecompiler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.stream.Collectors;

// Maps the response received from Gemini
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiResponse {

    // Stores possible answers returned by Gemini
    private List<Candidate> candidates;

    // Returns the candidate answers
    public List<Candidate> getCandidates() {
        return candidates;
    }

    // Sets the candidate answers
    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    // Extracts and combines all text parts from the first candidate
    public String extractText() {

        if (candidates == null || candidates.isEmpty()
                || candidates.get(0).getContent() == null
                || candidates.get(0).getContent().getParts() == null
                || candidates.get(0).getContent().getParts().isEmpty()) {
            return null;
        }

        return candidates.get(0).getContent().getParts().stream()
                .map(Part::getText)
                .filter(text -> text != null)
                .collect(Collectors.joining());
    }

    // Represents one possible answer from Gemini
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Candidate {

        private Content content;

        public Content getContent() {
            return content;
        }

        public void setContent(Content content) {
            this.content = content;
        }
    }

    // Represents the content of a Gemini answer
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {

        private List<Part> parts;

        public List<Part> getParts() {
            return parts;
        }

        public void setParts(List<Part> parts) {
            this.parts = parts;
        }
    }

    // Represents one text part of the answer
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Part {

        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}

/*
--> 1. Purpose
   Stores Gemini's JSON response.

--> 2. Response structure
   candidates -> content -> parts -> text

--> 3. extractText()
   Extracts and combines all text parts from the first candidate.

--> 4. Null safety
   Checks whether response data exists before accessing it.

--> 5. Unknown JSON fields
   @JsonIgnoreProperties ignores fields we do not need.
*/
