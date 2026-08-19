package com.example.trivia_backend.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class QuestionAndAnswer {
    private String id;
    private String question;
    private String correctAnswer;
    private List<String> possibleAnswers;
    private boolean isAnswered;

    public QuestionAndAnswer(String question, String correctAnswer, List<String> possibleAnswers) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.possibleAnswers = this.shufflePossibleAnswers(possibleAnswers);

        this.id = UUID.randomUUID().toString();
        this.isAnswered = false;
    }

    private List<String> shufflePossibleAnswers(List<String> possibleAnswers) {
        List<String> clonedPossibleAnswers = new ArrayList<>(possibleAnswers);
        Collections.shuffle(clonedPossibleAnswers);
        return clonedPossibleAnswers;
    }

    public List<String> getPossibleAnswers() {
        return new ArrayList<>(possibleAnswers);
    }

    public boolean isCorrectAnswer(String givenAnswer) {
        this.isAnswered = true;
        return correctAnswer.equals(givenAnswer);
    }

    public String getId() {
        return this.id;
    }

    public boolean isAnswered() {
        return this.isAnswered;
    }

    public String getQuestion() {
        return this.question;
    }
}
