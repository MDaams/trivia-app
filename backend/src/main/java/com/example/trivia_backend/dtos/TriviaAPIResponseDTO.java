package com.example.trivia_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TriviaAPIResponseDTO(
                @JsonProperty("results") List<TriviaQuestionDto> results) {
        public record TriviaQuestionDto(

                        @JsonProperty("question") String question,

                        @JsonProperty("correct_answer") String correctAnswer,

                        @JsonProperty("incorrect_answers") List<String> incorrectAnswers) {
        }
}