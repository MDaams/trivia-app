package com.example.trivia_backend.opentDB.records;

import com.example.trivia_backend.quiz.records.TriviaQuestion;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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

    public TriviaQuestion toTriviaQuestion() {
        List<String> possibleAnswers = new ArrayList<>();

        for (String answer : this.incorrectAnswers()) {
            if (isTextBoolean(answer)) {
                possibleAnswers.add(parseBooleanToHumanReadable(answer));
            } else {
                possibleAnswers.add(answer);
            }
        }

        String correctAnswer = this.correctAnswer();
        if (isTextBoolean(correctAnswer)) {
            correctAnswer = parseBooleanToHumanReadable(correctAnswer);
        }
        possibleAnswers.add(correctAnswer);

        Collections.shuffle(possibleAnswers);

        return new TriviaQuestion(UUID.randomUUID(),
                this.question(),
                correctAnswer, false, possibleAnswers);
    }

    private String parseBooleanToHumanReadable(String text) {
        return text.toLowerCase().equals("true") ? "Yes" : text.toLowerCase().equals("false") ? "No" : text;
    }

    private boolean isTextBoolean(String text) {
        return text.toLowerCase().equals("true") || text.toLowerCase().equals("false");
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