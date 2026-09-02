package com.example.trivia_backend.quiz.records;

import java.util.List;
import java.util.UUID;

public record TriviaQuestion(
        UUID id,
        String question,
        String correctAnswer,
        boolean isPresented,
        List<String> possibleAnswers) {

    public boolean canBeUsed() {
        return !isPresented;
    }

    public boolean isCorrect(String givenAnswer) {
        return correctAnswer.equals(givenAnswer);
    }

    public boolean isEqual(String compareTo) {
        return question.equals(compareTo);
    }
}
