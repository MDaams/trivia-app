package com.example.trivia_backend.quiz.dtos.checkAnswer;

import com.example.trivia_backend.quiz.dtos.ApiResponse;

public record CheckAnswerResponseDTO(
        String id,
        String correctAnswer,
        boolean isCorrect) implements ApiResponse {
}
