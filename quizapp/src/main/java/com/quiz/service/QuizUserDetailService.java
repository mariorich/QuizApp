package com.quiz.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import com.quiz.model.User;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuizUserDetailService {

    private List<User> users = new ArrayList<>();

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register a new user
    public void registerUser(String username, String password, String email, String role) {
        String encodedPassword = passwordEncoder.encode(password);
        users.add(new User(username, encodedPassword, email, "ROLE_" + role.toUpperCase()));
    }

    // Load user by username
    public User loadUserByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

