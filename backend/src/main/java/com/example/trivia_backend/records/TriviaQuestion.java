package com.example.trivia_backend.records;

import java.util.List;

public record TriviaQuestion(
        String id,
        String question,
        String correctAnswer,
        List<String> possibleAnswers) {

    public boolean isCorrect(String givenAnswer) {
        return correctAnswer.equals(givenAnswer);
    }
}
