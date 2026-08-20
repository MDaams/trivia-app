package com.example.trivia_backend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.trivia_backend.exceptions.QuestionNotFoundException;
import com.example.trivia_backend.models.QuestionAndAnswer;

@SpringBootTest
class QaServiceTests {

    @Autowired
    private QaService service;

    @BeforeEach
    void beforeEach() {
        service.clearQuestionAndAnswers();

        String uuidString = UUID.randomUUID().toString();
        String question = "Kiwi?";
        String correctAnswer = "Yes";
        List<String> possibleAnswers = new ArrayList<String>();
        possibleAnswers.add("No");
        possibleAnswers.add(correctAnswer);

        QuestionAndAnswer qa = new QuestionAndAnswer(uuidString, question, correctAnswer, possibleAnswers);

        service.addQuestion(qa);
    }

    @Test
    void shouldAddDuplicateQuestions() {
        String uuidString = UUID.randomUUID().toString();
        String question = "Kiwi?";
        String correctAnswer = "Yes";
        List<String> possibleAnswers = new ArrayList<String>();
        possibleAnswers.add("No");
        possibleAnswers.add(correctAnswer);

        QuestionAndAnswer qa = new QuestionAndAnswer(uuidString, question, correctAnswer, possibleAnswers);
        List<QuestionAndAnswer> result = service.addQuestion(qa);

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldEvaluateAnswer() {
        QuestionAndAnswer questionAndAnswer = service.getFirstQuestion();

        boolean result = service.evaluateAnswer(questionAndAnswer.id(), "Yes");

        assertThat(result).isTrue();
    }

    @Test
    void shouldDeleteQuestionAfterEvaluatingAnswer() {
        QuestionAndAnswer questionAndAnswer = service.getFirstQuestion();

        service.evaluateAnswer(questionAndAnswer.id(), "Yes");

        assertThat(service.getQuestionsAndAnswers().size()).isEqualTo(0);
    }

    @Test
    void shouldReturnRuntimeExceptionWhenEvaluatingAnswerOnNonExistingQuestion() {
        QuestionAndAnswer qa = service.getFirstQuestion();

        service.clearQuestionAndAnswers();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.evaluateAnswer(qa.id(), "dab");
        });

        assertThat(exception).isInstanceOf(QuestionNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo("Question not found.");
    }

    @Test
    void getFirstQuestionShouldReturnNullWhenListIsEmpty() {
        service.clearQuestionAndAnswers();

        QuestionAndAnswer qa = service.getFirstQuestion();

        assertThat(qa).isNull();
    }
}
