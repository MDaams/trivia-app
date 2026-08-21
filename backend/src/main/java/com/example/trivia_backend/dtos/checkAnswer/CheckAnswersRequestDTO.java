package com.example.trivia_backend.dtos.checkAnswer;

import java.util.List;

public record CheckAnswersRequestDTO(
        List<CheckAnswerRequestDTO> answers) {

}