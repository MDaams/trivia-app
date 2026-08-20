package com.example.trivia_backend.controllers;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.contains;

import com.example.trivia_backend.models.QuestionAndAnswer;
import com.example.trivia_backend.services.QaService;

@WebMvcTest(QaController.class)
public class QaControllerTest {
    @Autowired
    private MockMvc mockClient;

    @MockitoBean
    private QaService qaService;

    @BeforeEach
    void beforeEach() {
        QuestionAndAnswer mockQuestionAndAnswer = new QuestionAndAnswer("kiwi", "Kiwi?", "Swagbaas",
                List.of("Swagbaas", "dab"));
        when(qaService.getFirstQuestion()).thenReturn(mockQuestionAndAnswer);
        when(qaService.getQuestionsAndAnswers()).thenReturn(List.of(mockQuestionAndAnswer));
    }

    @Test
    void getQuestionsShouldReturnListOfQuestions() throws Exception {
        mockClient.perform(get("/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].question").value("Kiwi?"))
                .andExpect(jsonPath("$[0].possible_answers", contains("Swagbaas", "dab")));
    }

    @Test
    void getQuestionsShouldHandleEmptyList() throws Exception {
        when(qaService.getQuestionsAndAnswers()).thenReturn(new ArrayList<>());
        mockClient.perform(get("/questions"))
                .andExpect(status().isOk());
    }

    @Test
    void getQuestionsShouldReturnOneQuestion() throws Exception {
        mockClient.perform(get("/question"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question").value("Kiwi?"))
                .andExpect(jsonPath("$.possible_answers", contains("Swagbaas", "dab")));
    }
}
