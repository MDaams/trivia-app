package com.example.trivia_backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
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
    }

    @Test
    void serviceShouldHaveQASet() {
        HashSet<QuestionAndAnswer> result = service.getQuestionsAndAnswers();

        assertEquals(new HashSet<>(), result);
    }

    @Test
    void serviceShouldAddQuestionAndAnswer() {
        String question = "Kiwi?";
        String correctAnswer = "Yes";
        List<String> possibleAnswers = new ArrayList<String>();
        possibleAnswers.add("No");
        possibleAnswers.add(correctAnswer);

        QuestionAndAnswer qa = new QuestionAndAnswer(question, correctAnswer, possibleAnswers);

        HashSet<QuestionAndAnswer> result = service.addQuestionAndAnswer(qa);

        assertEquals(1, result.size());
    }

    @Test
    void serviceShouldNotHoldDuplicateQuestions() {
        String question = "Kiwi?";
        String correctAnswer = "Yes";
        List<String> possibleAnswers = new ArrayList<String>();
        possibleAnswers.add("No");
        possibleAnswers.add(correctAnswer);

        QuestionAndAnswer qa = new QuestionAndAnswer(question, correctAnswer, possibleAnswers);

        service.addQuestionAndAnswer(qa);
        HashSet<QuestionAndAnswer> result = service.addQuestionAndAnswer(qa);

        assertEquals(1, result.size());
    }
}
