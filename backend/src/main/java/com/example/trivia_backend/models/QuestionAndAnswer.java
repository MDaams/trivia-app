package com.example.trivia_backend.models;

import java.util.List;

public record QuestionAndAnswer(
        String id,
        String question,
        String correctAnswer,
        List<String> possibleAnswers) {

    public boolean isCorrect(String givenAnswer) {
        return correctAnswer.equals(givenAnswer);
    }
}
