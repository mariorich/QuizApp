package com.quiz.model;

import java.util.ArrayList;

public class Question {
    private int id;
    private String questionText;
    private ArrayList<String> options;
    private String correctAnswer;

    // Constructor
    public Question(int id, String questionText, ArrayList<String> options, String correctAnswer) {
        this.id = id;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }
    // Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getQuestionText() {
        return questionText;
    }
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    public ArrayList<String> getOptions() {
        return options;
    }
    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", questionText='" + questionText + '\'' +
                ", options=" + options +
                ", correctAnswer='" + correctAnswer + '\'' +
                '}';
    }
}
