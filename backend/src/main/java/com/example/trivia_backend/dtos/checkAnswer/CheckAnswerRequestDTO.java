package com.example.trivia_backend.dtos.checkAnswer;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CheckAnswerRequestDTO(
        @JsonProperty("id") String id,
        @JsonProperty("answer") String answer) {
}