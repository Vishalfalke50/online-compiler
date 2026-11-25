package com.vishal.onlinecompiler.service;

import com.vishal.onlinecompiler.dto.SubmissionResponse;
import com.vishal.onlinecompiler.entity.User;
import com.vishal.onlinecompiler.repository.CodeSubmissionRepository;
import com.vishal.onlinecompiler.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// Service that returns the run history of the logged-in user
@Service
public class HistoryService {

    // Used to find the logged-in user
    private final UserRepository userRepository;

    // Used to read the user's submissions from code_submissions
    private final CodeSubmissionRepository submissionRepository;

    // Spring injects both repositories automatically
    public HistoryService(UserRepository userRepository,
                          CodeSubmissionRepository submissionRepository) {
        this.userRepository = userRepository;
        this.submissionRepository = submissionRepository;
    }

    // Returns the user's submissions, newest first
    public List<SubmissionResponse> getHistory(String username) {

        // Find the user from the username stored in the JWT token
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        // Convert each entity into a safe response DTO
        return submissionRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(s -> new SubmissionResponse(
                        s.getId(),
                        s.getLanguage(),
                        s.getSourceCode(),
                        s.getStdin(),
                        s.getOutput(),
                        s.getStatus(),
                        s.getAiResponse(),
                        s.getCreatedAt()))
                .toList();
    }
}
/*
-->1. Purpose
   HistoryService returns the past code runs of one user.

-->2. How it works
   username from the JWT → find the User → load submissions newest first
   → convert each entity into SubmissionResponse → return the list

-->3. Why only this user's data?
   The username comes from the token, not from the request, so a user can never
   read another user's history.
*/