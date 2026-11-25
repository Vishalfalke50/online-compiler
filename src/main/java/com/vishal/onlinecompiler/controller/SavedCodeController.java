package com.vishal.onlinecompiler.controller;

import com.vishal.onlinecompiler.dto.SaveCodeRequest;
import com.vishal.onlinecompiler.dto.SavedCodeResponse;
import com.vishal.onlinecompiler.dto.UpdateCodeRequest;
import com.vishal.onlinecompiler.service.SavedCodeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// REST controller for saving, listing, updating and deleting programs
@RestController
@RequestMapping("/api/code")
public class SavedCodeController {

    // Service that contains the saved code logic
    private final SavedCodeService savedCodeService;

    // Spring injects the service automatically
    public SavedCodeController(SavedCodeService savedCodeService) {
        this.savedCodeService = savedCodeService;
    }

    // POST /api/code/save: saves a new program
    @PostMapping("/save")
    public ResponseEntity<SavedCodeResponse> save(@Valid @RequestBody SaveCodeRequest request,
                                                  Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedCodeService.save(request, authentication.getName()));
    }

    // GET /api/code/saved: returns all saved programs of the logged-in user
    @GetMapping("/saved")
    public ResponseEntity<List<SavedCodeResponse>> getAll(Authentication authentication) {
        return ResponseEntity.ok(savedCodeService.getAll(authentication.getName()));
    }

    // PUT /api/code/saved/{id}: updates a saved program
    @PutMapping("/saved/{id}")
    public ResponseEntity<SavedCodeResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody UpdateCodeRequest request,
                                                    Authentication authentication) {
        return ResponseEntity.ok(savedCodeService.update(id, request, authentication.getName()));
    }

    // DELETE /api/code/saved/{id}: deletes a saved program
    @DeleteMapping("/saved/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        savedCodeService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
/*
-->1. Purpose
   SavedCodeController exposes the endpoints for the programs a user keeps.

-->2. Endpoints
   POST   /api/code/save        → create (201)
   GET    /api/code/saved       → list
   PUT    /api/code/saved/{id}  → update
   DELETE /api/code/saved/{id}  → delete (204)

-->3. Security
   All endpoints need a valid JWT. The username comes from the token.
*/