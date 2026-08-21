package com.example.trivia_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TriviaAPIResponseDTO(
                @JsonProperty("results") List<TriviaAPIQuestion> results) {
        public record TriviaAPIQuestion(

                        @JsonProperty("question") String question,

                        @JsonProperty("correct_answer") String correctAnswer,

                        @JsonProperty("incorrect_answers") List<String> incorrectAnswers) {
        }
}