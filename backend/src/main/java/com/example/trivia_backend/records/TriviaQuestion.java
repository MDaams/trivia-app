package com.example.trivia_backend.records;

import java.util.List;
import java.util.UUID;

public record TriviaQuestion(
        UUID id,
        String question,
        String correctAnswer,
        boolean isPresented,
        List<String> possibleAnswers) {

    public boolean isPresented() {
        return isPresented;
    }

    public boolean isCorrect(String givenAnswer) {
        return correctAnswer.equals(givenAnswer);
    }
}
