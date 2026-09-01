package com.example.trivia_backend.quiz.dtos.checkAnswer;

import java.util.List;

import com.example.trivia_backend.quiz.dtos.ApiResponse;

public record CheckAnswersResponseDTO(
        List<CheckAnswerResponseDTO> results) implements ApiResponse {
}
