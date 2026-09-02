package com.example.trivia_backend.quiz.records;

import java.util.UUID;

import com.example.trivia_backend.quiz.dtos.checkAnswer.CheckAnswerResponseDTO;

public record EvaluationResult(UUID id, String correctAnswer, boolean isCorrect) {

    public CheckAnswerResponseDTO toDTO() {
        return new CheckAnswerResponseDTO(id.toString(), correctAnswer, isCorrect);
    }
}
