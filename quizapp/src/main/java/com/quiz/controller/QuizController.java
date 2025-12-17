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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;


import com.quiz.service.QuizUserDetailsService;

import jakarta.servlet.http.HttpServletRequest;

import com.quiz.model.Question;
import com.quiz.service.QuestionService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Controller
public class QuizController {

    private final QuizUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    
    public QuizController(QuizUserDetailsService userDetailsService, AuthenticationManager authenticationManager) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
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
            return "redirect:user/quiz"; // Return the viewer.html template
        }
    }

    @GetMapping("/login")
    public String login(Authentication authentication, @RequestParam(required = false) String logout) {
    // If user is logged in and not logging out, redirect to /home
    // need to add the logout parameter check because the spring security automatically sends to the login page

    if (authentication != null && authentication.isAuthenticated() && logout == null) {
        return "redirect:/home";
    }
    return "login"; // show login page
    }

    @GetMapping("/register")
    public String registerPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/home";
        }
        return "register"; // return register.html
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
        
        List<Question> questions = questionService.loadQuizzes();
        int newId = questions.isEmpty() ? 1 : questions.get(questions.size() - 1).getId() + 1;
        ArrayList<String> options = new ArrayList<>(Arrays.asList(optionA, optionB, optionC, optionD));
        Question question = new Question(newId, questionText, options, correctAnswer);
        questionService.addQuiz(question);
        return "redirect:/admin/quizList";
    }

    @GetMapping("/admin/editQuiz")
    public String addEditPage(@RequestParam int questionId, Model model) {
        Question question = questionService.getQuizById(questionId);
        model.addAttribute("question", question);
        return "editQuiz"; // AddQuiz template
    }

    @PutMapping("/admin/editQuiz")
    public String editQuiz(
            @RequestParam int questionId,
            @RequestParam String questionText,
            @RequestParam String optionA,
            @RequestParam String optionB,
            @RequestParam String optionC,
            @RequestParam String optionD,
            @RequestParam String correctAnswer) {

        List<String> options = Arrays.asList(optionA, optionB, optionC, optionD);
        Question updatedQuestion = new Question(questionId, questionText, new ArrayList<>(options), correctAnswer);
        questionService.editQuiz(updatedQuestion);
        return "redirect:/admin/quizList";
    }

    @DeleteMapping("/admin/deleteQuiz")
    public String deleteQuiz(@RequestParam int questionId) {
        questionService.deleteQuiz(questionId);
        return "redirect:/admin/quizList";
    }


    // User submits answers
    @PostMapping("/user/submitQuiz")
    public String submitQuiz(HttpServletRequest request, Model model) {
        
        Map<Integer, String> userAnswers = new HashMap<>();
        
        int i = 0;
        
        for (Question q : questionService.loadQuizzes()) {
            String answer = request.getParameter("answer" + i);
            userAnswers.put(q.getId(), answer);
            i++;
        }

        model.addAttribute("score", questionService.calculateScore(userAnswers));
        return "quizResult";
    }


}
