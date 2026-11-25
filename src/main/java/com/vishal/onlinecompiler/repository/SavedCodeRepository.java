package com.vishal.onlinecompiler.repository;

import com.vishal.onlinecompiler.entity.SavedCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Handles database operations for saved programs
public interface SavedCodeRepository extends JpaRepository<SavedCode, Long> {

    // Fetches a user's saved programs, most recently updated first
    List<SavedCode> findByUserIdOrderByUpdatedAtDesc(Long userId);

    // Finds a saved program only if it belongs to the given user
    Optional<SavedCode> findByIdAndUserId(Long id, Long userId);
}
/*
-->findByIdAndUserId makes sure a user can only open, edit or delete their own programs.
*/