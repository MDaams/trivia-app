package com.example.trivia_backend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.client.MockRestServiceServer;

import com.example.trivia_backend.dtos.TriviaAPIResponseDTO;
import com.example.trivia_backend.exceptions.RateLimitExceededException;

import org.springframework.http.MediaType;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServiceUnavailable;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withTooManyRequests;

@RestClientTest(TriviaAPIService.class)
public class TriviaAPIServiceTest {

    @Autowired
    private TriviaAPIService service;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void getQuestionsParsesQuestions() {
        String mockResponse = """
                {
                  "response_code": 0,
                  "results": [
                    {
                      "question": "Test question?",
                      "correct_answer": "Yes",
                      "incorrect_answers": ["No"]
                    }
                  ]
                }
                """;

        server.expect(requestTo("https://opentdb.com/api.php?amount=50"))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));
        TriviaAPIResponseDTO triviaAPIResponseDTO = service.getQuestions();
        assertThat(triviaAPIResponseDTO.results().get(0).question()).isEqualTo("Test question?");
    }

    @Test
    void getQuestionsShouldHandleNotFound() {
        server.expect(requestTo("https://opentdb.com/api.php?amount=50"))
                .andRespond(withResourceNotFound());

        Exception exception = assertThrows(Exception.class, () -> {
            service.getQuestions();
        });

        assertThat(exception.getMessage()).contains("404");
    }

    @Test
    void getQuestionsShouldHandleTooManyRequests() {
        server.expect(requestTo("https://opentdb.com/api.php?amount=50"))
                .andRespond(withTooManyRequests());

        Exception exception = assertThrows(RateLimitExceededException.class, () -> {
            service.getQuestions();
        });

        assertThat(exception.getMessage()).contains("429");
    }

    @Test
    void getQuestionsShouldHandleAnyNonOk() {
        server.expect(requestTo("https://opentdb.com/api.php?amount=50"))
                .andRespond(withServiceUnavailable());

        Exception exception = assertThrows(Exception.class, () -> {
            service.getQuestions();
        });

        assertThat(exception.getMessage()).contains("503");
        assertThat(exception.getMessage()).contains("opentdb.com");
        assertThat(exception.getMessage()).contains("api.php");
    }
}
