package com.example.trivia_backend.dtos.questions;

import java.util.List;

import com.example.trivia_backend.dtos.ApiResponse;

public record TriviaQuestionsResponseDTO(
        List<TriviaQuestionResponseDTO> results) implements ApiResponse {
}
