package com.example.trivia_backend.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    void serviceShouldNotHoldDuplicateQuestions() {
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
    void serviceShouldEvaluateAnswer() {
        QuestionAndAnswer questionAndAnswer = service.getFirstQuestion();

        boolean result = service.evaluateAnswer(questionAndAnswer.id(), "Yes");

        assertThat(result).isTrue();
    }

    @Test
    void serviceShouldDeleteQuestionAfterEvaluatingAnswer() {
        QuestionAndAnswer questionAndAnswer = service.getFirstQuestion();

        service.evaluateAnswer(questionAndAnswer.id(), "Yes");

        assertThat(service.getQuestionsAndAnswers().size()).isEqualTo(0);
    }

    @Test
    void serviceShouldEvaluateAnswerThrowsNull() {
        boolean result = service.evaluateAnswer("Swagbaas", "Kiwi 1");

        assertThat(result).isFalse();
    }

    @Test
    void getFirstQuestionReturnsNullWhenListIsEmpty() {
        service.clearQuestionAndAnswers();

        QuestionAndAnswer qa = service.getFirstQuestion();

        assertThat(qa).isNull();
    }
}
