package com.example.trivia_backend.dtos.checkAnswer;

import java.util.List;

import com.example.trivia_backend.dtos.ApiResponse;

public record CheckAnswersResponseDTO(
                List<CheckAnswerResponseDTO> results) implements ApiResponse {
}
