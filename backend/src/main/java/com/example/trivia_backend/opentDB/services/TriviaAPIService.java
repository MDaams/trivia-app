package com.example.trivia_backend.opentDB.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec;

import com.example.trivia_backend.opentDB.dtos.TriviaAPIResponseDTO;
import com.example.trivia_backend.opentDB.exceptions.RateLimitExceededException;

@Service
public class TriviaAPIService {
    private static final Logger log = LoggerFactory.getLogger(TriviaAPIService.class);

    // Ideally env variables but out of scope for now
    private final String AMOUNT = "50";
    private final String BASE_URL = "https://opentdb.com";
    private final String ENDPOINT = "/api.php?amount=" + AMOUNT + "&encode=base64";

    private final RestClient restClient;

    public TriviaAPIService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl(BASE_URL).build();
    }

    public TriviaAPIResponseDTO getQuestions() {
        var responseSpecification = restClient.get().uri(ENDPOINT).retrieve()
                .onStatus(status -> status.value() == 429, (request, response) -> {
                    handleTooManyRequestResponse();
                })
                .onStatus(status -> status.isError(), (request, response) -> {
                    handleGenericExceptions(response.getStatusCode());
                });

        return parseBodyToDTO(responseSpecification);
    }

    private TriviaAPIResponseDTO parseBodyToDTO(ResponseSpec responseSpecification) {
        return responseSpecification.body(TriviaAPIResponseDTO.class);
    }

    private void handleTooManyRequestResponse() {
        String message = String.format(
                "429: Too Many Requests (%s%s)",
                BASE_URL, ENDPOINT);
        log.warn(message);
        throw new RateLimitExceededException(message);
    }

    private void handleGenericExceptions(HttpStatusCode statusCode) {
        String message = String.format(
                "Something went wrong while calling the Trivia API (%s%s): %s",
                BASE_URL, ENDPOINT, statusCode);
        log.warn(message);
        throw new RuntimeException(message);
    }
}