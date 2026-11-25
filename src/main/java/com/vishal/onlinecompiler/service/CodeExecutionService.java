package com.vishal.onlinecompiler.service;

import com.vishal.onlinecompiler.dto.CodeExecutionRequest;
import com.vishal.onlinecompiler.dto.CodeExecutionResponse;
import com.vishal.onlinecompiler.dto.Judge0Request;
import com.vishal.onlinecompiler.dto.Judge0Response;
import com.vishal.onlinecompiler.entity.CodeSubmission;
import com.vishal.onlinecompiler.entity.User;
import com.vishal.onlinecompiler.repository.CodeSubmissionRepository;
import com.vishal.onlinecompiler.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;

// Service that runs code through Judge0 and stores the result in the history
@Service
public class CodeExecutionService {

    // Maps the language name sent by the frontend to the Judge0 language id
    private static final Map<String, Integer> LANGUAGE_IDS = Map.of(
            "java", 62,
            "python", 71,
            "cpp", 54,
            "c", 50,
            "javascript", 63
    );

    // RestClient configured for Judge0
    private final RestClient judge0RestClient;

    // Used to find the logged-in user
    private final UserRepository userRepository;

    // Used to save each run in the code_submissions table
    private final CodeSubmissionRepository submissionRepository;

    // Spring injects all three objects automatically
    public CodeExecutionService(@Qualifier("judge0RestClient") RestClient judge0RestClient,
                                UserRepository userRepository,
                                CodeSubmissionRepository submissionRepository){
        this.judge0RestClient = judge0RestClient;
        this.userRepository = userRepository;
        this.submissionRepository = submissionRepository;
    }

    // Sends the code to Judge0, saves the result and returns it to the controller
    public CodeExecutionResponse run(CodeExecutionRequest request, String username) {

        // Convert the language name to the Judge0 id, or reject unknown languages
        Integer languageId = LANGUAGE_IDS.get(request.getLanguage().toLowerCase());
        if (languageId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Unsupported language: " + request.getLanguage());
        }

        // Call Judge0 and wait for the result (wait=true means one request, one answer)
        Judge0Response result;
        try {
            result = judge0RestClient.post()
                    .uri("/submissions?base64_encoded=false&wait=true")
                    .body(new Judge0Request(request.getSourceCode(), languageId, request.getStdin()))
                    .retrieve()
                    .body(Judge0Response.class);
        } catch (RestClientException e) {
            // Print the real reason in the console (wrong key, not subscribed, no internet)
            System.out.println("Judge0 call failed: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Code execution service is not available right now");
        }

        // Judge0 returned nothing useful
        if (result == null || result.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Invalid response from code execution service");
        }

        // Compile errors take priority, then runtime errors
        String error = isBlank(result.getCompileOutput()) ? result.getStderr() : result.getCompileOutput();
        String status = result.getStatus().getDescription();
        String memory = result.getMemory() == null ? null : String.valueOf(result.getMemory());

        // Save this run in the history
        saveSubmission(request, languageId, username, result.getStdout(), error, status);

        return new CodeExecutionResponse(result.getStdout(), error, status, result.getTime(), memory);
    }

    // Stores one run in the code_submissions table
    private void saveSubmission(CodeExecutionRequest request, int languageId, String username,
                                String stdout, String error, String status) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        CodeSubmission submission = new CodeSubmission();
        submission.setUser(user);
        submission.setLanguage(request.getLanguage());
        submission.setLanguageId(languageId);
        submission.setSourceCode(request.getSourceCode());
        submission.setStdin(request.getStdin());
        // Store the program output, or the error message when there is no output
        submission.setOutput(isBlank(stdout) ? error : stdout);
        submission.setStatus(status);
        submissionRepository.save(submission);
    }

    // Small helper: true when the text is null or empty
    private boolean isBlank(String text) {
        return text == null || text.isBlank();
    }
}
/*
-->1. Purpose
   This service takes the code from the controller, runs it on Judge0 and saves the result.

-->2. How it works
   language name → Judge0 id (Java = 62)
   → POST /submissions?wait=true with source_code, language_id, stdin
   → Judge0 returns stdout, stderr, compile_output and status
   → we merge the errors, save the run in Oracle and return CodeExecutionResponse

-->3. Success, compile error and runtime error
   Accepted          → output is in stdout
   Compilation Error → message is in compile_output
   Runtime Error     → message is in stderr

-->4. Note
   For Java, the public class must be named Main, because Judge0 saves the file as Main.java.
*/