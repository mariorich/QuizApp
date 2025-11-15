package com.quiz.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.quiz.model.User;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuizUserDetailService {
    private List<User> users = new ArrayList<>();

    @Autowired
    private PasswordEncoder passwordEncoder;
    public void registerUser(String username, String password, String email, String role) {
        
        encodedPassword = passwordEncoder.encode(password);
        
        User user = new User(username, encodedPassword, email, role);
        users.add(user);
    }

    public String loadUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user.toString();
            }
        }
        throw new RuntimeException("User not found");
    }
}
