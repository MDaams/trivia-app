package com.example.trivia_backend.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

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

        String question = "Kiwi?";
        String correctAnswer = "Yes";
        List<String> possibleAnswers = new ArrayList<String>();
        possibleAnswers.add("No");
        possibleAnswers.add(correctAnswer);

        QuestionAndAnswer qa = new QuestionAndAnswer(question, correctAnswer, possibleAnswers);

        service.addQuestionAndAnswer(qa);
    }

    @Test
    void serviceShouldNotHoldDuplicateQuestions() {
        String question = "Kiwi?";
        String correctAnswer = "Yes";
        List<String> possibleAnswers = new ArrayList<String>();
        possibleAnswers.add("No");
        possibleAnswers.add(correctAnswer);

        QuestionAndAnswer qa = new QuestionAndAnswer(question, correctAnswer, possibleAnswers);
        List<QuestionAndAnswer> result = service.addQuestionAndAnswer(qa);

        assertThat(result).hasSize(1);
    }

    @Test
    void serviceShouldEvaluateAnswer() {
        QuestionAndAnswer questionAndAnswer = service.getFirstQuestion();

        boolean result = service.evaluateAnswer(questionAndAnswer.getId(), "Yes");

        assertThat(result).isTrue();
    }

    @Test
    void serviceShouldSetQuestionAsAnsweredAfterEvaluatingAnswer() {
        QuestionAndAnswer questionAndAnswer = service.getFirstQuestion();

        service.evaluateAnswer(questionAndAnswer.getId(), "Yes");

        QuestionAndAnswer updatedQuestionAndAnswer = service.getFirstQuestion();

        assertThat(updatedQuestionAndAnswer.isAnswered()).isTrue();
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
