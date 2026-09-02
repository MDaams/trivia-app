package com.example.trivia_backend.quiz.dtos.checkAnswer;

public record CheckAnswerRequestDTO(
        String id,
        String answer) {
    public boolean isValid() {
        return id != null && answer != null && !id.isEmpty() && !answer.isEmpty();
    }
}