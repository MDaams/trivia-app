package com.example.trivia_backend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServiceUnavailable;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withTooManyRequests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import com.example.trivia_backend.dtos.TriviaAPIResponseDTO;
import com.example.trivia_backend.exceptions.RateLimitExceededException;

@RestClientTest(TriviaAPIService.class)
public class TriviaAPIServiceTest {

    @Autowired
    private TriviaAPIService service;

    @Autowired
    private MockRestServiceServer server;

    private final String ENDPOINT = "https://opentdb.com/api.php?amount=50&encode=base64";

    @Test
    @DisplayName("It should parse the questions received from the external API")
    void getQuestionsParsesQuestions() {
        String mockResponse = """
                {
                  "response_code": 0,
                  "results": [
                    {
                      "question": "Kiwi?",
                      "correct_answer": "Swagbaas",
                      "incorrect_answers": ["dab"]
                    }
                  ]
                }
                """;

        server.expect(requestTo(ENDPOINT))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));
        TriviaAPIResponseDTO triviaAPIResponseDTO = service.getQuestions();
        assertThat(triviaAPIResponseDTO.results().get(0).question()).isEqualTo("Kiwi?");
    }

    @Test
    @DisplayName("It should throw an exception when the endpoint could not be found")
    void getQuestionsShouldHandleNotFound() {
        server.expect(requestTo(ENDPOINT))
                .andRespond(withResourceNotFound());

        Exception exception = assertThrows(Exception.class, () -> {
            service.getQuestions();
        });

        assertThat(exception.getMessage()).contains("404");
    }

    @Test
    @DisplayName("It should throw an exception when the endpoint returns 429: Too many requests")
    void getQuestionsShouldHandleTooManyRequests() {
        server.expect(requestTo(ENDPOINT))
                .andRespond(withTooManyRequests());

        Exception exception = assertThrows(RateLimitExceededException.class, () -> {
            service.getQuestions();
        });

        assertThat(exception.getMessage()).contains("429");
    }

    @Test
    @DisplayName("It should throw an exception on any status code that is not 200")
    void getQuestionsShouldHandleAnyNonOk() {
        server.expect(requestTo(ENDPOINT))
                .andRespond(withServiceUnavailable());

        Exception exception = assertThrows(Exception.class, () -> {
            service.getQuestions();
        });

        assertThat(exception.getMessage()).contains("503");
        assertThat(exception.getMessage()).contains("opentdb.com");
        assertThat(exception.getMessage()).contains("api.php");
    }
}
