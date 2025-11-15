package com.quiz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.quiz.model.Question;
import com.quiz.service.QuestionService;
import com.quiz.service.QuizUserDetailService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;  

@Controller
@RequestMapping("/")
public class QuizController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
    @GetMapping("/register")
    public String showRegistrationPage() {
        return "register";
    }
    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password,
                               @RequestParam String email, @RequestParam String role) {
                               
        quizUserDetailService.registerUser(username, password, email, role);

        return "redirect:/login";
    }

    @GetMapping("/addQuiz")
    public String showAddQuizPage() {
        return "addQuiz";
    }
    @PostMapping("/addQuiz")
    public String addQuiz(@RequestParam String questionText, @RequestParam String optionA,
                          @RequestParam String optionB, @RequestParam String optionC, @RequestParam String optionD,
                          @RequestParam String correctAnswer) {
    
        ArrayList<String> options = new ArrayList<>(Arrays.asList(optionA, optionB, optionC, optionD));
        Question question = new Question(questionText, options, correctAnswer);
        questionService.addQuiz(question);
        return "redirect:/home";
    }

    @GetMapping("/editQuiz")
    public String showEditQuizPage(@RequestParam Long questionId) {
        Question question = questionService.getQuizById(questionId);
        model.addAttribute("question", question);
        return "editQuiz";
    }
    @PostMapping("/editQuiz")
    public String editQuiz(@RequestParam Long questionId, @RequestParam String questionText, @RequestParam String optionA,
                           @RequestParam String optionB, @RequestParam String optionC, @RequestParam String optionD,
                           @RequestParam String correctAnswer) {
        ArrayList<String> options = new ArrayList<>(Arrays.asList(optionA, optionB, optionC, optionD));
        Question question = new Question(questionId, questionText, options, correctAnswer);
        questionService.updateQuiz(question);
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
