package com.quiz.service;

import org.springframework.stereotype.Service;
import com.quiz.model.Question;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class QuestionService {

    private List<Question> questions = new ArrayList<>();
    private Long lastScore = 0L;

    // Add a new quiz
    public void addQuiz(String questionText, ArrayList<String> options, String correctAnswer) {
        int newId = questions.isEmpty() ? 1 : questions.get(questions.size() - 1).getId() + 1;
        Question question = new Question(newId, questionText, options, correctAnswer);
        questions.add(question);
    }

    // Get quiz by id
    public Question getQuizById(int id) {
        return questions.stream()
                .filter(q -> q.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // Edit quiz
    public void editQuiz(Question updatedQuestion) {
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getId() == updatedQuestion.getId()) {
                questions.set(i, updatedQuestion);
                return;
            }
        }
    }

    // Load all quizzes
    public List<Question> loadQuizzes() {
        return questions;
    }

    // Calculate score from submitted answers
    public void calculateScore(HashMap<Long, String> userAnswers) {
        long score = 0;
        for (Question q : questions) {
            String answer = userAnswers.get((long) q.getId());
            if (answer != null && answer.equals(q.getCorrectAnswer())) {
                score++;
            }
        }
        lastScore = score;
    }

    public Long getScore() {
        return lastScore;
    }
}
