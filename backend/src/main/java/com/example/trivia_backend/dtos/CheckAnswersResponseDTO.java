package com.example.trivia_backend.dtos;

import java.util.List;

public record CheckAnswersResponseDTO(
                List<CheckAnswerResponseDTO> results) implements ApiResponse {
}
