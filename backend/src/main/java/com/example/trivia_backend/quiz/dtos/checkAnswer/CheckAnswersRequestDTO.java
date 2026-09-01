package com.example.trivia_backend.quiz.dtos.checkAnswer;

import java.util.List;

public record CheckAnswersRequestDTO(
        List<CheckAnswerRequestDTO> answers) {

}