package com.quiz.service;

import com.quiz.model.Question;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class QuestionService {

private List<Question> questions = new ArrayList<Question>();

    public void addQuiz(Question question) {
        questions.add(question);
    }

    public List<Question> loadQuizzes() {
        return questions;
    }
    
    public void editQuiz(Question updatedQuestion){
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getId() == updatedQuestion.getId()) {
                questions.set(i, updatedQuestion);
                return;
            }
            throw new RuntimeException("Question not found");
        }
    }

    public void deleteQuiz(int questionId){
        questions.removeIf(question -> question.getId() == questionId);
    }
    
}
