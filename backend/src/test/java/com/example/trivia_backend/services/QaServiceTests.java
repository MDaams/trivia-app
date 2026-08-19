package com.example.trivia_backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QaServiceTests {

    @Autowired
    private QaService qaService;

    @Test
    void serviceShouldHaveQAList() {
        HashSet<String> questionAndAnswers = qaService.getQuestionsAndAnswers();

        assertEquals(new HashSet<>(), questionAndAnswers);
    }

}
