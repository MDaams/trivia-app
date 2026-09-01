package com.example.trivia_backend.opentDB.dtos;

import com.example.trivia_backend.opentDB.records.TriviaAPIQuestion;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record TriviaAPIResponseDTO(
                @JsonProperty("results") List<TriviaAPIQuestion> results) {
}