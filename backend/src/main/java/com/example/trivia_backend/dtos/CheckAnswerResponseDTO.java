package com.example.trivia_backend.dtos;

public record CheckAnswerResponseDTO(
                boolean isCorrect,
                String correctAnswer) {
}
