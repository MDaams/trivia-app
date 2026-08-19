package com.example.trivia_backend.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.trivia_backend.models.QuestionAndAnswer;

@SpringBootTest
class QuestionAndAnswerModelTest {
    @Test
    void modelShouldKeepPossibleAnswers() {
        String question = "Kiwi?";
        String correctAnswer = "Kiwi 1";
        List<String> possibleAnswers = new ArrayList<>(List.of(correctAnswer, "Kiwi 2", "Kiwi 3", "Kiwi 4"));
        QuestionAndAnswer qa = new QuestionAndAnswer(question, correctAnswer, possibleAnswers);
        List<String> expected = List.of("Kiwi 1", "Kiwi 2", "Kiwi 3", "Kiwi 4");

        List<String> result = qa.getPossibleAnswers();

        assertThat(result).containsAnyElementsOf(expected);
    }

    @Test
    void modelValidatesCorrectAnswer() {
        String question = "Kiwi?";
        String correctAnswer = "Kiwi 1";
        List<String> possibleAnswers = new ArrayList<>(List.of(correctAnswer, "Kiwi 2", "Kiwi 3", "Kiwi 4"));
        QuestionAndAnswer qa = new QuestionAndAnswer(question, correctAnswer, possibleAnswers);

        boolean result = qa.isCorrectAnswer(correctAnswer);

        assertThat(result).isTrue();
    }

    @Test
    void modelValidatesIncorrectAnswer() {
        String question = "Kiwi?";
        String correctAnswer = "Kiwi 1";
        List<String> possibleAnswers = new ArrayList<>(List.of(correctAnswer, "Kiwi 2", "Kiwi 3", "Kiwi 4"));
        QuestionAndAnswer qa = new QuestionAndAnswer(question, correctAnswer, possibleAnswers);

        boolean result = qa.isCorrectAnswer("Kiwi 2");

        assertThat(result).isFalse();
    }

    @Test
    void modelValidatesIncorrectAnswerWhenNotPartOfPossibilities() {
        String question = "Kiwi?";
        String correctAnswer = "Kiwi 1";
        List<String> possibleAnswers = new ArrayList<>(List.of(correctAnswer, "Kiwi 2", "Kiwi 3", "Kiwi 4"));
        QuestionAndAnswer qa = new QuestionAndAnswer(question, correctAnswer, possibleAnswers);

        boolean result = qa.isCorrectAnswer("SwagBaas");

        assertThat(result).isFalse();
    }
}
