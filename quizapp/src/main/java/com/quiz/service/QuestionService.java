package com.quiz.service;

import com.quiz.model.Question;
import java.util.ArrayList;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;


@Service
public class QuestionService {

private List<Question> questions = new ArrayList<Question>();
private Long score;

    public void addQuiz(String questionText, ArrayList options, String correctAnswer) {
        if(!questions.isEmpty()){
            int newId = questions.get(questions.size() - 1).getId() + 1;
            Question question = new Question(newId, questionText, options, correctAnswer);
            questions.add(question);
        } else {
            Question question = new Question(1, questionText, options, correctAnswer);
            questions.add(question);
        }
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
    
    public Question getQuizById(int questionId) {
        for (Question question : questions) {
            if (question.getId() == questionId) {
                return question;
            }
        }
        throw new RuntimeException("Question not found");
    }

    public Long getScore() {
        return score;
    }
    public void calculateScore(HashMap<Long,String> userAnswers) {
        Long calculatedScore = 0L;
        for (Question question : questions) {
            String userAnswer = userAnswers.get(question.getId());
            if (question.getCorrectAnswer().equals(userAnswer)) {
                calculatedScore++;
            }
        }
        this.score = calculatedScore;    
    }

}
