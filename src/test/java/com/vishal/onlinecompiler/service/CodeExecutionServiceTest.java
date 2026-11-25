package com.vishal.onlinecompiler.service;

import com.vishal.onlinecompiler.dto.CodeExecutionRequest;
import com.vishal.onlinecompiler.dto.CodeExecutionResponse;
import com.vishal.onlinecompiler.dto.Judge0Request;
import com.vishal.onlinecompiler.dto.Judge0Response;
import com.vishal.onlinecompiler.entity.CodeSubmission;
import com.vishal.onlinecompiler.entity.User;
import com.vishal.onlinecompiler.repository.CodeSubmissionRepository;
import com.vishal.onlinecompiler.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Answers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Unit tests for CodeExecutionService; Judge0 and the database are replaced by fakes
@ExtendWith(MockitoExtension.class)
class CodeExecutionServiceTest {

    // Deep stubs let us fake the whole chain: post().uri().body().retrieve().body()
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) private RestClient judge0RestClient;
    @Mock private UserRepository userRepository;
    @Mock private CodeSubmissionRepository submissionRepository;

    // The real class under test, built with the fakes above
    @InjectMocks private CodeExecutionService service;

    // Creates a run request used by the tests
    private CodeExecutionRequest request(String language) {
        CodeExecutionRequest request = new CodeExecutionRequest();
        request.setLanguage(language);
        request.setSourceCode("print('hi')");
        request.setStdin("");
        return request;
    }

    // Creates a fake Judge0 reply with the given status
    private Judge0Response judge0Reply(String status) {
        Judge0Response.Status s = new Judge0Response.Status();
        s.setId(3);
        s.setDescription(status);

        Judge0Response reply = new Judge0Response();
        reply.setStatus(s);
        reply.setTime("0.05");
        reply.setMemory(1000);
        return reply;
    }

    // Tells the fake RestClient to return the given reply for the Judge0 call
    private void stubJudge0(Judge0Response reply) {
        when(judge0RestClient.post()
                .uri(anyString())
                .body(any(Judge0Request.class))
                .retrieve()
                .body(Judge0Response.class)).thenReturn(reply);
    }

    @Test
    void run_accepted_returnsOutputAndSavesSubmission() {
        Judge0Response reply = judge0Reply("Accepted");
        reply.setStdout("hi\n");
        stubJudge0(reply);
        when(userRepository.findByUsername("avi")).thenReturn(Optional.of(new User()));

        CodeExecutionResponse response = service.run(request("Python"), "avi");

        assertEquals("hi\n", response.getOutput());
        assertEquals("Accepted", response.getStatus());
        assertEquals("1000", response.getMemory());
        assertNull(response.getError());

        // Check what was saved in the history
        ArgumentCaptor<CodeSubmission> captor = ArgumentCaptor.forClass(CodeSubmission.class);
        verify(submissionRepository).save(captor.capture());
        assertEquals(71, captor.getValue().getLanguageId());   // Python = 71
        assertEquals("Accepted", captor.getValue().getStatus());
    }

    @Test
    void run_compileError_returnsCompilerMessageAsError() {
        Judge0Response reply = judge0Reply("Compilation Error");
        reply.setCompileOutput("error: ';' expected");
        stubJudge0(reply);
        when(userRepository.findByUsername("avi")).thenReturn(Optional.of(new User()));

        CodeExecutionResponse response = service.run(request("Java"), "avi");

        assertEquals("Compilation Error", response.getStatus());
        assertEquals("error: ';' expected", response.getError());
    }

    @Test
    void run_unsupportedLanguage_throws400() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.run(request("ruby"), "avi"));

        assertEquals(400, ex.getStatusCode().value());
        verify(submissionRepository, never()).save(any());   // nothing is saved
    }

    @Test
    void run_judge0Down_throws502() {
        when(judge0RestClient.post()
                .uri(anyString())
                .body(any(Judge0Request.class))
                .retrieve()
                .body(Judge0Response.class)).thenThrow(new RestClientException("connection refused"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.run(request("Java"), "avi"));

        assertEquals(502, ex.getStatusCode().value());
        verify(submissionRepository, never()).save(any());
    }
}
/*
-->1. Purpose
   These unit tests check CodeExecutionService without calling the real Judge0 or Oracle.

-->2. How the Judge0 call is faked
   RETURNS_DEEP_STUBS makes the RestClient mock return more mocks for every step
   of the chain, so one when(...) line controls the final answer.

-->3. What each test proves
   run_accepted_...           → output, status and memory are mapped, and the run is saved
   run_compileError_...       → the compiler message appears in the error field
   run_unsupportedLanguage_...→ an unknown language gives 400 and nothing is saved
   run_judge0Down_...         → a Judge0 failure gives a clean 502 and nothing is saved
*/