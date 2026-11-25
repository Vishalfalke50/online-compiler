package com.vishal.onlinecompiler.repository;

import com.vishal.onlinecompiler.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Handles database operations for users
public interface UserRepository extends JpaRepository<User, Long> {

    // Finds a user by username, used at login
    Optional<User> findByUsername(String username);

    // Checks for duplicates during registration
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}