package com.example.trivia_backend.quiz.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.trivia_backend.quiz.dtos.checkAnswer.CheckAnswerRequestDTO;
import com.example.trivia_backend.quiz.dtos.checkAnswer.CheckAnswersRequestDTO;
import com.example.trivia_backend.quiz.exceptions.QuestionNotFoundException;
import com.example.trivia_backend.quiz.records.EvaluationResult;
import com.example.trivia_backend.quiz.records.TriviaQuestion;
import com.example.trivia_backend.quiz.services.QaService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(QaController.class)
public class QaControllerTest {
        @Autowired
        private MockMvc mockClient;

        @MockitoBean
        private QaService qaService;

        @Autowired
        private ObjectMapper objectMapper;

        private TriviaQuestion mockQuestionAndAnswer;

        private final String checksAnswersEndpoint = "/checkAnswers";
        private final String getQuestionsEndpoint = "/questions";

        @BeforeEach
        void beforeEach() {
                mockQuestionAndAnswer = new TriviaQuestion(UUID.randomUUID(), "Kiwi?", "Swagbaas", false,
                                List.of("Swagbaas", "dab"));
        }

        @Test
        @DisplayName("Get /questions should return a list filled with questions and answers")
        void getQuestionsShouldReturnListOfQuestions() throws Exception {
                when(qaService.getTriviaQuestions(1)).thenReturn(List.of(mockQuestionAndAnswer));
                mockClient.perform(get(getQuestionsEndpoint))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.results[0].question").value("Kiwi?"))
                                .andExpect(jsonPath("$.results[0].possible_answers", contains("Swagbaas", "dab")));
        }

        @Test
        @DisplayName("Get /questions should return a 200 ok when there are no questions")
        void getQuestionsShouldHandleEmptyList() throws Exception {
                when(qaService.getTriviaQuestions(1)).thenReturn(List.of(mockQuestionAndAnswer));
                when(qaService.getTriviaQuestions(1)).thenReturn(new ArrayList<>());
                mockClient.perform(get(getQuestionsEndpoint))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Post /checkAnswers should handle answers for multiple questions")
        void postCheckAnswersHandlesMultiple() throws Exception {
                EvaluationResult answerResultCorrect = new EvaluationResult(UUID.randomUUID(), "Swagbaas", true);
                EvaluationResult answerResultIncorrect = new EvaluationResult(UUID.randomUUID(), "Swagbaas", false);
                when(qaService.evaluateAnswer(answerResultCorrect.id().toString(), "Swagbaas"))
                                .thenReturn(answerResultCorrect);
                when(qaService.evaluateAnswer(answerResultIncorrect.id().toString(), "dab"))
                                .thenReturn(answerResultIncorrect);

                CheckAnswerRequestDTO firstAnswer = new CheckAnswerRequestDTO(answerResultCorrect.id().toString(),
                                "Swagbaas");
                CheckAnswerRequestDTO secondAnswer = new CheckAnswerRequestDTO(answerResultIncorrect.id().toString(),
                                "dab");

                CheckAnswersRequestDTO requestDTO = new CheckAnswersRequestDTO(List.of(firstAnswer, secondAnswer));

                mockClient
                                .perform(post(checksAnswersEndpoint).contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.results[0].isCorrect").value("true"))
                                .andExpect(jsonPath("$.results[0].correctAnswer").value("Swagbaas"))
                                .andExpect(jsonPath("$.results[1].isCorrect").value("false"))
                                .andExpect(jsonPath("$.results[1].correctAnswer").value("Swagbaas"));
        }

        @Test
        @DisplayName("post /checkAnswers should handle when a question for which the answer is given does not exist")
        void postCheckAnswersShouldHandleNonExistingQuestion() throws Exception {
                when(qaService.evaluateAnswer("kiwi", "Swagbaas"))
                                .thenThrow(new QuestionNotFoundException("Question not found."));

                CheckAnswerRequestDTO answerDTO = new CheckAnswerRequestDTO("kiwi", "Swagbaas");
                CheckAnswersRequestDTO requestDTO = new CheckAnswersRequestDTO(List.of(answerDTO));

                MvcResult response = mockClient
                                .perform(post(checksAnswersEndpoint).contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isUnprocessableContent())
                                .andReturn();

                assertThat(response.getResponse().getContentAsString()).contains("Question not found.");
        }

        @Test
        @DisplayName("post /checkAnswers should handle any exception")
        void postCheckAnswersShouldHandleAnyException() throws Exception {
                when(qaService.evaluateAnswer("kiwi", "Swagbaas"))
                                .thenThrow(new RuntimeException("Error occured."));

                CheckAnswerRequestDTO answerDTO = new CheckAnswerRequestDTO("kiwi", "Swagbaas");
                CheckAnswersRequestDTO requestDTO = new CheckAnswersRequestDTO(List.of(answerDTO));

                MvcResult response = mockClient
                                .perform(post(checksAnswersEndpoint).contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isInternalServerError())
                                .andReturn();

                assertThat(response.getResponse().getContentAsString())
                                .contains("Something went wrong during evaluation of answer.");
        }

        @Test
        @DisplayName("post /checkAnswers should return 400 when no Id provided")
        void postCheckAnswersShouldThrowBadRequestWhenNoId() throws Exception {
                CheckAnswerRequestDTO answerDTO = new CheckAnswerRequestDTO(null, "Swagbaas");
                CheckAnswersRequestDTO requestDTO = new CheckAnswersRequestDTO(List.of(answerDTO));

                mockClient
                                .perform(post(checksAnswersEndpoint).contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("post /checkAnswers should return 400 when no answer provided")
        void postCheckAnswersShouldThrowBadRequestWhenNoAnswer() throws Exception {
                CheckAnswerRequestDTO answerDTO = new CheckAnswerRequestDTO("kiwi", null);
                CheckAnswersRequestDTO requestDTO = new CheckAnswersRequestDTO(List.of(answerDTO));

                mockClient
                                .perform(post(checksAnswersEndpoint).contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isBadRequest());
        }

}
