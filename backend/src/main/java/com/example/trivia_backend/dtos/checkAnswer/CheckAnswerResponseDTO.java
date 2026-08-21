package com.example.trivia_backend.dtos.checkAnswer;

import com.example.trivia_backend.dtos.ApiResponse;

public record CheckAnswerResponseDTO(
        String id,
        String correctAnswer,
        boolean isCorrect) implements ApiResponse {
}
