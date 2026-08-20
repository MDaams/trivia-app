package com.example.trivia_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record QuestionAndAnswerDTO(
                @JsonProperty("id") String id,
                @JsonProperty("question") String question,
                @JsonProperty("possible_answers") List<String> possibleAnswers) {
        public List<String> getPossibleAnswers() {
                return possibleAnswers;
        }
}