package com.example.trivia_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record QuestionAndAnswerResponseDTO(
                String id,
                String question,
                @JsonProperty("possible_answers") List<String> possibleAnswers) implements ApiResponse {
        public List<String> getPossibleAnswers() {
                return possibleAnswers;
        }
}