package com.example.trivia_backend.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QuestionAndAnswerRecordTest {
    @Test
    void modelShouldKeepPossibleAnswers() {
        String uuidString = UUID.randomUUID().toString();
        String question = "Kiwi?";
        String correctAnswer = "Kiwi 1";
        List<String> possibleAnswers = new ArrayList<>(List.of(correctAnswer, "Kiwi 2", "Kiwi 3", "Kiwi 4"));
        QuestionAndAnswer qa = new QuestionAndAnswer(uuidString, question, correctAnswer, possibleAnswers);
        List<String> expected = List.of("Kiwi 1", "Kiwi 2", "Kiwi 3", "Kiwi 4");

        List<String> result = qa.possibleAnswers();

        assertThat(result).containsAnyElementsOf(expected);
    }

    @Test
    void modelValidatesCorrectAnswer() {
        String uuidString = UUID.randomUUID().toString();
        String question = "Kiwi?";
        String correctAnswer = "Kiwi 1";
        List<String> possibleAnswers = new ArrayList<>(List.of(correctAnswer, "Kiwi 2", "Kiwi 3", "Kiwi 4"));
        QuestionAndAnswer qa = new QuestionAndAnswer(uuidString, question, correctAnswer, possibleAnswers);

        boolean result = qa.isCorrect(correctAnswer);

        assertThat(result).isTrue();
    }

    @Test
    void modelValidatesIncorrectAnswer() {
        String uuidString = UUID.randomUUID().toString();
        String question = "Kiwi?";
        String correctAnswer = "Kiwi 1";
        List<String> possibleAnswers = new ArrayList<>(List.of(correctAnswer, "Kiwi 2", "Kiwi 3", "Kiwi 4"));
        QuestionAndAnswer qa = new QuestionAndAnswer(uuidString, question, correctAnswer, possibleAnswers);

        boolean result = qa.isCorrect("Kiwi 2");

        assertThat(result).isFalse();
    }

    @Test
    void modelValidatesIncorrectAnswerWhenNotPartOfPossibilities() {
        String uuidString = UUID.randomUUID().toString();
        String question = "Kiwi?";
        String correctAnswer = "Kiwi 1";
        List<String> possibleAnswers = new ArrayList<>(List.of(correctAnswer, "Kiwi 2", "Kiwi 3", "Kiwi 4"));
        QuestionAndAnswer qa = new QuestionAndAnswer(uuidString, question, correctAnswer, possibleAnswers);

        boolean result = qa.isCorrect("SwagBaas");

        assertThat(result).isFalse();
    }
}
