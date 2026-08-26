package com.example.trivia_backend.records;

import java.util.UUID;

public record EvaluationResult(UUID id, String correctAnswer, boolean isCorrect) {
}
