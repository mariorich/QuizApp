package com.quiz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.quiz.model.Question;
import com.quiz.service.QuestionService;
import com.quiz.service.QuizUserDetailService;
import com.quiz.model.User;
import org.springframework.ui.Model;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;  
import org.springframework.beans.factory.annotation.Autowired;



@Controller
@RequestMapping("/")
public class QuizController {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuizUserDetailService quizUserDetailService;

    @GetMapping("/public/login")
    public String showLoginPage() {
        return "login";
    }
    @GetMapping("/public/register")
    public String showRegistrationPage() {
        return "register";
    }
    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password,
                               @RequestParam String email, @RequestParam String role) {
                               
        quizUserDetailService.registerUser(username, password, email, role);

        return "redirect:/login";
    }

    @GetMapping("/admin/addQuiz")
    public String showAddQuizPage() {
        return "addQuiz";
    }
    @PostMapping("/admin/addQuiz")
    public String addQuiz(@RequestParam String questionText, @RequestParam String optionA,
                          @RequestParam String optionB, @RequestParam String optionC, @RequestParam String optionD,
                          @RequestParam String correctAnswer) {
    
        ArrayList<String> options = new ArrayList<>(Arrays.asList(optionA, optionB, optionC, optionD));
        questionService.addQuiz(questionText, options, correctAnswer);
        return "redirect:/home";
    }

    @GetMapping("/admin/editQuiz")
    public String showEditQuizPage(Model model, @RequestParam int questionId) {
        Question question = questionService.getQuizById(questionId);
        model.addAttribute("question", question);
        return "editQuiz";
    }
    @PostMapping("/admin/editQuiz")
    public String editQuiz(@RequestParam int questionId, @RequestParam String questionText, @RequestParam String optionA,
                           @RequestParam String optionB, @RequestParam String optionC, @RequestParam String optionD,
                           @RequestParam String correctAnswer) {
        ArrayList<String> options = new ArrayList<>(Arrays.asList(optionA, optionB, optionC, optionD));
        Question question = new Question(questionId, questionText, options, correctAnswer);
        questionService.editQuiz(question);
        return "redirect:/home";
        }


    @GetMapping("/home")
    public String showHomePage(Model model,@RequestParam User user) {
        
        if(user.getRole().equals("ADMIN")) {
            return "reditrect:/admin/home";
        }
        return "redirect:/user/home";
    }
    @GetMapping("/admin/home")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAdminHomePage(Model model) {
        List<Question> questions = questionService.loadQuizzes();
        model.addAttribute("questions", questions);
        return "adminHome";
    }

    @GetMapping("/user/home")
    @PreAuthorize("hasRole('USER')")
    public String showUserHomePage(Model model) {
        List<Question> questions = questionService.loadQuizzes();
        model.addAttribute("questions", questions);
        return "userHome";
    }

    @PostMapping("/home")
    public String submitQuiz(Model model, @RequestParam HashMap<Long, String> userAnswers) {
        questionService.calculateScore(userAnswers);
        Long score = questionService.getScore();
        model.addAttribute("score", score);
        return "quizResult";
    }
}
