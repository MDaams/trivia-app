package com.example.trivia_backend.models;

import java.util.ArrayList;
import java.util.UUID;

public class QuestionAndAnswer {
    private String id;
    private String question;
    private String correctAnswer;
    private ArrayList<String> possibleAnswers;

    public QuestionAndAnswer(String question, String correctAnswer, ArrayList<String> possibleAnswers) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.possibleAnswers = possibleAnswers;

        this.id = UUID.randomUUID().toString();
    }

}
