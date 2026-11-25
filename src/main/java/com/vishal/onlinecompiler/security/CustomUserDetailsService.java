package com.vishal.onlinecompiler.security;

import com.vishal.onlinecompiler.entity.User;
import com.vishal.onlinecompiler.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Loads user details from the Oracle database for Spring Security
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Repository used to find the user in the app_users table
    private final UserRepository userRepository;

    // Spring injects the repository automatically
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Called by Spring Security whenever it needs to find a user by username
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find the user, or throw an error if the username does not exist
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Spring has its own User class with the same name as our entity, so it is written in full
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())   // username
                .password(user.getPassword())       // BCrypt hashed password from the database
                .authorities(user.getRole())        // role, for example ROLE_USER
                .build();
    }
}
/*
-->1. Purpose
   Spring Security does not know our User entity. This class converts our User
   from the database into the UserDetails object that Spring Security understands.

-->2. How it works
   Login or token check → Spring calls loadUserByUsername("vishal")
   → UserRepository finds the row in app_users
   → we return username, hashed password and role to Spring Security
   → Spring compares the typed password with the hash using BCrypt

-->3. If the user is not found
   UsernameNotFoundException is thrown, and the login fails.

--> Some methods working
   user.getUsername() → Provides the username.
   user.getPassword() → Provides the stored BCrypt hash.
   user.getRole() → Provides the authority, such as ROLE_USER.
   .build() → Creates the final UserDetails object.
*/