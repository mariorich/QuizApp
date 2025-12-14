package com.quiz.service;

import com.quiz.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuizUserDetailService implements UserDetailsService {

    // In-memory storage for users
    private final List<User> userList = new ArrayList<>();

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Register a new user (in-memory)
     * @param username username
     * @param password raw password (will be encoded)
     * @param email user email
     * @param role role (e.g., "USER" or "ADMIN")
     */
    public void registerUser(String username, String password, String email, String role) {
        // Encode password
        String encodedPassword = passwordEncoder.encode(password);

        // Store in custom User list
        User newUser = new User(username, email, encodedPassword, role);
        userList.add(newUser);
    }

    /**
     * Load user by username for authentication
     * @param username username
     * @return Spring Security UserDetails
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find user in the in-memory list
        User u = userList.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Convert to Spring Security User for authentication
        return org.springframework.security.core.userdetails.User.builder()
                .username(u.getUsername())
                .password(u.getPassword())
                .roles(u.getRole()) // Spring Security roles
                .build();
    }

    /**
     * Optional: get all users (for debugging or listing)
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(userList);
    }
}
