package com.vishal.onlinecompiler.service;


import com.vishal.onlinecompiler.dto.AuthResponse;
import com.vishal.onlinecompiler.dto.LoginRequest;
import com.vishal.onlinecompiler.dto.RegisterRequest;
import com.vishal.onlinecompiler.entity.User;
import com.vishal.onlinecompiler.repository.UserRepository;
import com.vishal.onlinecompiler.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Unit tests for AuthService; all dependencies are Mockito fakes, so no database is used
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    // Fake objects that replace the real dependencies
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtil jwtUtil;

    // The real class under test, built with the fakes above
    @InjectMocks private AuthService authService;

    // Creates a valid registration request used by the tests
    private RegisterRequest registerRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("avi");
        request.setEmail("avi@example.com");
        request.setPassword("avi12345");
        return request;
    }

    @Test
    void register_savesUserWithHashedPassword() {
        when(userRepository.existsByUsername("avi")).thenReturn(false);
        when(userRepository.existsByEmail("avi@example.com")).thenReturn(false);
        when(passwordEncoder.encode("avi12345")).thenReturn("hashed-password");

        AuthResponse response = authService.register(registerRequest());

        // Capture the User that was passed to save()
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        assertEquals("hashed-password", captor.getValue().getPassword());   // never plain text
        assertEquals("Registration successful", response.getMessage());
        assertNull(response.getToken());                                    // no token until login
    }

    @Test
    void register_duplicateUsername_throws409() {
        when(userRepository.existsByUsername("avi")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.register(registerRequest()));

        assertEquals(409, ex.getStatusCode().value());
        verify(userRepository, never()).save(any());    // nothing is saved
    }

    @Test
    void login_correctCredentials_returnsToken() {
        when(jwtUtil.generateToken("avi")).thenReturn("fake-token");

        LoginRequest request = new LoginRequest();
        request.setUsername("avi");
        request.setPassword("avi12345");

        AuthResponse response = authService.login(request);

        assertEquals("fake-token", response.getToken());
        assertEquals("Login successful", response.getMessage());
    }

    @Test
    void login_wrongPassword_throws401() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad credentials"));

        LoginRequest request = new LoginRequest();
        request.setUsername("avi");
        request.setPassword("wrong-password");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.login(request));

        assertEquals(401, ex.getStatusCode().value());
        verify(jwtUtil, never()).generateToken(anyString());   // no token for a failed login
    }
}
/*
-->1. Purpose
   These unit tests check the register and login logic of AuthService in isolation.

-->2. How Mockito is used
   @Mock creates a fake object. when(...).thenReturn(...) tells the fake what to answer.
   @InjectMocks builds the real AuthService using those fakes.
   verify(...) checks that a method was (or was not) called.

-->3. What each test proves
   register_savesUserWithHashedPassword → the password is hashed before saving
   register_duplicateUsername_throws409 → duplicates are rejected and nothing is saved
   login_correctCredentials_returnsToken → a valid login returns a JWT
   login_wrongPassword_throws401 → a bad password gives 401 and no token
*/
