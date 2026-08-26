package com.example.trivia_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public record TriviaAPIResponseDTO(
                @JsonProperty("results") List<TriviaAPIQuestion> results) {

        public record TriviaAPIQuestion(
                        @JsonProperty("question") String question,
                        @JsonProperty("correct_answer") String correctAnswer,
                        @JsonProperty("incorrect_answers") List<String> incorrectAnswers) {

                public TriviaAPIQuestion {
                        question = decode(question);
                        correctAnswer = decode(correctAnswer);
                        if (incorrectAnswers != null) {
                                incorrectAnswers = incorrectAnswers.stream().map(TriviaAPIQuestion::decode).toList();
                        }
                }

                private static String decode(String base64String) {
                        if (base64String == null)
                                return null;
                        try {
                                byte[] decodedBytes = Base64.getDecoder().decode(base64String);
                                return new String(decodedBytes, StandardCharsets.UTF_8);
                        } catch (IllegalArgumentException e) {
                                return base64String;
                        }
                }
        }
}