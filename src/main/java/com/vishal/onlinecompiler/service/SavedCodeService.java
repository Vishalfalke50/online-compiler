package com.vishal.onlinecompiler.service;

import com.vishal.onlinecompiler.dto.SaveCodeRequest;
import com.vishal.onlinecompiler.dto.SavedCodeResponse;
import com.vishal.onlinecompiler.dto.UpdateCodeRequest;
import com.vishal.onlinecompiler.entity.SavedCode;
import com.vishal.onlinecompiler.entity.User;
import com.vishal.onlinecompiler.repository.SavedCodeRepository;
import com.vishal.onlinecompiler.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// Service that saves, lists, updates and deletes programs of the logged-in user
@Service
public class SavedCodeService {

    // Used to find the logged-in user
    private final UserRepository userRepository;

    // Used to read and write the saved_codes table
    private final SavedCodeRepository savedCodeRepository;

    // Spring injects both repositories automatically
    public SavedCodeService(UserRepository userRepository,
                            SavedCodeRepository savedCodeRepository) {
        this.userRepository = userRepository;
        this.savedCodeRepository = savedCodeRepository;
    }

    // Saves a new program for the user
    public SavedCodeResponse save(SaveCodeRequest request, String username) {
        User user = findUser(username);

        SavedCode saved = new SavedCode();
        saved.setUser(user);
        saved.setTitle(request.getTitle());
        saved.setLanguage(request.getLanguage());
        saved.setSourceCode(request.getSourceCode());

        return toResponse(savedCodeRepository.save(saved));
    }

    // Returns all saved programs of the user, most recently updated first
    public List<SavedCodeResponse> getAll(String username) {
        User user = findUser(username);

        return savedCodeRepository.findByUserIdOrderByUpdatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Updates a saved program, only if it belongs to the user
    public SavedCodeResponse update(Long id, UpdateCodeRequest request, String username) {
        SavedCode saved = findOwned(id, username);

        saved.setTitle(request.getTitle());
        saved.setLanguage(request.getLanguage());
        saved.setSourceCode(request.getSourceCode());

        return toResponse(savedCodeRepository.save(saved));
    }

    // Deletes a saved program, only if it belongs to the user
    public void delete(Long id, String username) {
        savedCodeRepository.delete(findOwned(id, username));
    }

    // Finds the user from the username stored in the JWT token
    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    // Finds a saved program that belongs to the user, or returns 404
    private SavedCode findOwned(Long id, String username) {
        User user = findUser(username);
        return savedCodeRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Saved code not found"));
    }

    // Converts the entity into a safe response DTO
    private SavedCodeResponse toResponse(SavedCode s) {
        return new SavedCodeResponse(
                s.getId(),
                s.getTitle(),
                s.getLanguage(),
                s.getSourceCode(),
                s.getCreatedAt(),
                s.getUpdatedAt());
    }
}
/*
-->1. Purpose
   SavedCodeService holds the logic for the programs a user chooses to keep.

-->2. How it works
   username from the JWT → find the User → save, list, update or delete
   their programs → return SavedCodeResponse DTOs

-->3. Why return 404 for another user's program?
   findOwned searches by id and user id together. Someone else's program looks
   the same as one that does not exist, so users cannot see or change each
   other's code.
*/