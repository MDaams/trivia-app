package com.example.trivia_backend.dtos.questions;

import com.example.trivia_backend.dtos.ApiResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TriviaQuestionResponseDTO(
                String id,
                String question,
                @JsonProperty("possible_answers") List<String> possibleAnswers) implements ApiResponse {
        public List<String> getPossibleAnswers() {
                return possibleAnswers;
        }
}