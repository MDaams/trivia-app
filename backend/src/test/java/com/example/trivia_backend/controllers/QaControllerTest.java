package com.example.trivia_backend.controllers;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;

import com.example.trivia_backend.dtos.AnswerResult;
import com.example.trivia_backend.dtos.CheckAnswerRequestDTO;
import com.example.trivia_backend.models.QuestionAndAnswer;
import com.example.trivia_backend.services.QaService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(QaController.class)
public class QaControllerTest {
        @Autowired
        private MockMvc mockClient;

        @MockitoBean
        private QaService qaService;

        @Autowired
        private ObjectMapper objectMapper;

        private QuestionAndAnswer mockQuestionAndAnswer;

        @BeforeEach
        void beforeEach() {
                mockQuestionAndAnswer = new QuestionAndAnswer("kiwi", "Kiwi?", "Swagbaas",
                                List.of("Swagbaas", "dab"));
        }

        @Test
        @DisplayName("Get /questions should return a list filled with questions and answers")
        void getQuestionsShouldReturnListOfQuestions() throws Exception {
                when(qaService.getQuestionsAndAnswers()).thenReturn(List.of(mockQuestionAndAnswer));
                mockClient.perform(get("/questions"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].question").value("Kiwi?"))
                                .andExpect(jsonPath("$[0].possible_answers", contains("Swagbaas", "dab")));
        }

        @Test
        @DisplayName("Get /questions should return a 200 ok when there are no questions")
        void getQuestionsShouldHandleEmptyList() throws Exception {
                when(qaService.getQuestionsAndAnswers()).thenReturn(List.of(mockQuestionAndAnswer));
                when(qaService.getQuestionsAndAnswers()).thenReturn(new ArrayList<>());
                mockClient.perform(get("/questions"))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Get /question should return a single question and multiple answers")
        void getQuestionShouldReturnOneQuestion() throws Exception {
                when(qaService.getFirstQuestion()).thenReturn(mockQuestionAndAnswer);
                mockClient.perform(get("/question"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.question").value("Kiwi?"))
                                .andExpect(jsonPath("$.possible_answers", contains("Swagbaas", "dab")));
        }

        @Test
        @DisplayName("Post /checkAnswer should return true when the answer is correct")
        void postCheckAnswerReturnsTrue() throws Exception {
                AnswerResult answerResult = new AnswerResult(true, "Swagbaas");
                when(qaService.evaluateAnswer("kiwi", "Swagbaas")).thenReturn(answerResult);

                CheckAnswerRequestDTO requestDTO = new CheckAnswerRequestDTO("kiwi", "Swagbaas");

                mockClient
                                .perform(post("/checkanswer").contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.isCorrect").value("true"))
                                .andExpect(jsonPath("$.correctAnswer").value("Swagbaas"));
        }

        @Test
        @DisplayName("Post /checkAnswer should return false when the answer is incorrect")
        void postCheckAnswerReturnsFalse() throws Exception {
                AnswerResult answerResult = new AnswerResult(false, "Swagbaas");
                when(qaService.evaluateAnswer("kiwi", "dab")).thenReturn(answerResult);

                CheckAnswerRequestDTO requestDTO = new CheckAnswerRequestDTO("kiwi", "dab");

                mockClient
                                .perform(post("/checkanswer").contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.isCorrect").value("false"))
                                .andExpect(jsonPath("$.correctAnswer").value("Swagbaas"));
        }

        @Test
        @DisplayName("Post /checkAnswers should handle answers for multiple questions")
        void postCheckAnswersHandlesMultiple() throws Exception {
                AnswerResult answerResultCorrect = new AnswerResult(true, "Swagbaas");
                AnswerResult answerResultIncorrect = new AnswerResult(false, "Swagbaas");
                when(qaService.evaluateAnswer("kiwi", "Swagbaas")).thenReturn(answerResultCorrect);
                when(qaService.evaluateAnswer("kiwi 2", "dab")).thenReturn(answerResultIncorrect);

                CheckAnswerRequestDTO firstAnswer = new CheckAnswerRequestDTO("kiwi", "Swagbaas");
                CheckAnswerRequestDTO secondAnswer = new CheckAnswerRequestDTO("kiwi 2", "dab");

                List<CheckAnswerRequestDTO> requestDTO = new ArrayList<>(List.of(firstAnswer, secondAnswer));

                mockClient
                                .perform(post("/checkanswers").contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.results[0].isCorrect").value("true"))
                                .andExpect(jsonPath("$.results[0].correctAnswer").value("Swagbaas"))
                                .andExpect(jsonPath("$.results[1].isCorrect").value("false"))
                                .andExpect(jsonPath("$.results[1].correctAnswer").value("Swagbaas"));
        }

        @Test
        @DisplayName("post /checkAnswer should handle when a question for which the answer is given does not exist")
        void postCheckAnswerShouldHandleNonExistingQuestion() throws Exception {
                when(qaService.evaluateAnswer("kiwi", "Swagbaas"))
                                .thenThrow(new RuntimeException("Question not found."));

                CheckAnswerRequestDTO requestDTO = new CheckAnswerRequestDTO("kiwi", "Swagbaas");

                MvcResult response = mockClient
                                .perform(post("/checkanswer").contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isUnprocessableContent())
                                .andReturn();

                assertThat(response.getResponse().getContentAsString()).contains("Question not found.");
        }

        @Test
        @DisplayName("post /checkAnswers should handle when a question for which the answer is given does not exist")
        void postCheckAnswersShouldHandleNonExistingQuestion() throws Exception {
                when(qaService.evaluateAnswer("kiwi", "Swagbaas"))
                                .thenThrow(new RuntimeException("Question not found."));

                CheckAnswerRequestDTO requestDTO = new CheckAnswerRequestDTO("kiwi", "Swagbaas");

                MvcResult response = mockClient
                                .perform(post("/checkanswers").contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(List.of(requestDTO))))
                                .andExpect(status().isUnprocessableContent())
                                .andReturn();

                assertThat(response.getResponse().getContentAsString()).contains("Question not found.");
        }

        @Test
        @DisplayName("post /checkAnswer should return 400 when no Id provided")
        void postCheckAnswerShouldThrowBadRequestWhenNoId() throws Exception {
                CheckAnswerRequestDTO requestDTO = new CheckAnswerRequestDTO(null, "Swagbaas");

                mockClient
                                .perform(post("/checkanswer").contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("post /checkAnswer should return 400 when no answer provided")
        void postCheckAnswerShouldThrowBadRequestWhenNoAnswer() throws Exception {
                CheckAnswerRequestDTO requestDTO = new CheckAnswerRequestDTO("kiwi", null);

                mockClient
                                .perform(post("/checkanswer").contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("post /checkAnswers should return 400 when no Id provided")
        void postCheckAnswersShouldThrowBadRequestWhenNoId() throws Exception {
                CheckAnswerRequestDTO requestDTO = new CheckAnswerRequestDTO(null, "Swagbaas");

                mockClient
                                .perform(post("/checkanswers").contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(List.of(requestDTO))))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("post /checkAnswers should return 400 when no answer provided")
        void postCheckAnswersShouldThrowBadRequestWhenNoAnswer() throws Exception {
                CheckAnswerRequestDTO requestDTO = new CheckAnswerRequestDTO("kiwi", null);

                mockClient
                                .perform(post("/checkanswers").contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(List.of(requestDTO))))
                                .andExpect(status().isBadRequest());
        }

}
