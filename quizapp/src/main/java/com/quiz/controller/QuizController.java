package com.quiz.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.quiz.service.QuizUserDetailsService;
import com.quiz.model.User;
import com.quiz.service.QuestionService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@Controller
@RequestMapping("/")
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
            return "admin"; // Return the admin.html template
        } else {
            return "viewer"; // Return the viewer.html template
        }
    }





    @Autowired
    private QuizUserDetailService quizUserDetailService;

    @GetMapping("/public/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/public/login")
    public String login(Model model,
                        @RequestParam String username,
                        @RequestParam String password) {
        return "login";
    }

    @GetMapping("/register")
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
            return "redirect:admin/quizList";
        }
        return "redirect:/user/quiz";
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
    @GetMapping("/user/addQuiz")
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
