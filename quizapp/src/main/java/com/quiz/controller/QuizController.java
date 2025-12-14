package com.quiz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import com.quiz.service.QuizUserDetailService;
import com.quiz.model.User;
import java.util.List;
import com.quiz.service.QuestionService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@Controller
@RequestMapping("/")
public class QuizController {

    @Autowired
    private QuizUserDetailService quizUserDetailService;

    @GetMapping("/public/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/public/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String email,
                               @RequestParam String role) {
        quizUserDetailService.registerUser(username, password, email, role);
        return "redirect:/public/login";
    }

    @GetMapping("/home")
    public String homeRedirect() {
        // Get logged-in username
        String username = org.springframework.security.core.context.SecurityContextHolder
                            .getContext()
                            .getAuthentication()
                            .getName();
        User user = quizUserDetailService.loadUserByUsername(username);

        if(user.getRole().equals("ROLE_ADMIN")) {
            return "redirect:/admin/home";
        }
        return "redirect:/user/home";
    }

    @GetMapping("/admin/home")
    public String adminHome() {
        return "adminHome"; // Your admin page template
    }

    @GetMapping("/user/home")
    public String userHome() {
        return "userHome"; // Your user page template
    }
    // Inject QuestionService
    @Autowired
    private QuestionService questionService;

    // Admin adds quiz
    @PostMapping("/admin/addQuiz")
    public String addQuiz(@RequestParam String questionText,
                        @RequestParam String optionA,
                        @RequestParam String optionB,
                        @RequestParam String optionC,
                        @RequestParam String optionD,
                        @RequestParam String correctAnswer) {

        ArrayList<String> options = new ArrayList<>(Arrays.asList(optionA, optionB, optionC, optionD));
        questionService.addQuiz(questionText, options, correctAnswer);
        return "redirect:/admin/home";
    }

    // User submits answers
    @PostMapping("/submitQuiz")
    public String submitQuiz(@RequestParam HashMap<Long, String> userAnswers, Model model) {
        questionService.calculateScore(userAnswers);
        model.addAttribute("score", questionService.getScore());
        return "quizResult"; // Create a Thymeleaf template to show the score
    }
}
