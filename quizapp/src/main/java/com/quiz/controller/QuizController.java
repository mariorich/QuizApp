package com.quiz.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.quiz.service.QuizUserDetailsService;
import com.quiz.service.QuestionService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@Controller
public class QuizController {

    private final QuizUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    
    public QuizController(QuizUserDetailsService userDetailsService, AuthenticationManager authenticationManager) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/home")
    
    public String homepage(Model model) {
        // Get the authenticated user's details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Check if the user is authenticated
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            // Redirect to the login page if the user is not authenticated
            return "redirect:/login";
        }
        // Get the username
        String username = authentication.getName();
        model.addAttribute("username", username);
        // Get the user's role
        String role = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst()
            .orElse("ROLE_STAFF"); // Default role if no authority is found
        // Redirect to the appropriate page based on the role
        if (role.equals("ROLE_ADMIN")) {
            return "redirect:/admin/quizList"; // Return the admin.html template
        } else {
            return "user/quiz"; // Return the viewer.html template
        }
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username, // Username from the form
            @RequestParam String password, // Password from the form
            @RequestParam String role // Role from the form
    ) {
        // Register the user by storing their details in the HashMap
        try {
            userDetailsService.registerUser(username, password, role);
        } catch (Exception userExistsAlready) {
            // Redirect to the /register endpoint
            return "redirect:/register?error";
        }
        // Authenticate the user programmatically
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );
        // Set the authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Redirect to the /login endpoint
        return "redirect:/login?success";
    }

    // Inject QuestionService
    @Autowired
    private QuestionService questionService;

    @GetMapping("/admin/quizList")
    public String quizList(Model model) {
        model.addAttribute("quizzes", questionService.loadQuizzes());
        return "quizList"; // Your admin page template
    }

    @GetMapping("/user/quiz")
    public String quiz(Model model) {
        model.addAttribute("quizzes", questionService.loadQuizzes());
        return "quiz"; // Your user page template
    }

    // Admin adds quiz
    @GetMapping("/admin/addQuiz")
    public String addQuizPage(Model model) {
        return "addQuiz"; // AddQuiz template
    }

    @PostMapping("/admin/addQuiz")
    public String addQuiz(@RequestParam String questionText,
                        @RequestParam String optionA,
                        @RequestParam String optionB,
                        @RequestParam String optionC,
                        @RequestParam String optionD,
                        @RequestParam String correctAnswer) {

        ArrayList<String> options = new ArrayList<>(Arrays.asList(optionA, optionB, optionC, optionD));
        questionService.addQuiz(questionText, options, correctAnswer);
        return "redirect:/admin/quizList";
    }

    // User submits answers
    @PostMapping("/submitQuiz")
    public String submitQuiz(@RequestParam HashMap<Long, String> userAnswers, Model model) {
        questionService.calculateScore(userAnswers);
        model.addAttribute("score", questionService.getScore());
        return "quizResult"; // Create a Thymeleaf template to show the score
    }
}
