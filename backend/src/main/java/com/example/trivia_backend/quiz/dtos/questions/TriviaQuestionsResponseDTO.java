package com.example.trivia_backend.quiz.dtos.questions;

import java.util.List;

import com.example.trivia_backend.quiz.dtos.ApiResponse;

public record TriviaQuestionsResponseDTO(
        List<TriviaQuestionResponseDTO> results) implements ApiResponse {
}
