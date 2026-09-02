package com.example.trivia_backend.quiz.dtos.exception;

import com.example.trivia_backend.quiz.dtos.ApiResponse;

public record ErrorResponseDTO(String message) implements ApiResponse {
}
