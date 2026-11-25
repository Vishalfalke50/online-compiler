package com.vishal.onlinecompiler.repository;

import com.vishal.onlinecompiler.entity.CodeSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Handles database operations for code submissions
public interface CodeSubmissionRepository extends JpaRepository<CodeSubmission, Long> {

    // Fetches a user's submissions, newest first
    List<CodeSubmission> findByUserIdOrderByCreatedAtDesc(Long userId);
}

/*
2. What is Repository?
Repository is an interface that handles database operations such as
 saving, fetching, updating and deleting records using Spring Data JPA.

Why do we need Repositories?
1.CRUD operations:Provides built-in methods like save(),findById(),findAll() and deleteById().
2. Less code: No need to write SQL queries for common database operations.
3. Custom queries: Supports query methods like findByUsername().
4. Database interaction: Acts as a layer between the service layer and database.
Example: In our project:
- UserRepository → Handles operations on the User entity.
- CodeSubmissionRepository → Handles operations on the CodeSubmission entity.

3. Entity vs Repository vs DTO
Entity	Repository	DTO
Represents database table	Handles database operations	Transfers data
Uses JPA annotations	Extends JpaRepository	Uses validation annotations when needed
Maps Java object to database Communicates with database	Transfers API request/response data





*/