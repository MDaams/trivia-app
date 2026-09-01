package com.example.trivia_backend.opentDB.services;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.trivia_backend.opentDB.dtos.TriviaAPIResponseDTO;
import com.example.trivia_backend.opentDB.exceptions.RateLimitExceededException;

@Service
public class TriviaAPIService {
    // Ideally env variables but out of scope for now
    private final String AMOUNT = "50";
    private final String BASE_URL = "https://opentdb.com";
    private final String ENDPOINT = "/api.php?amount=" + AMOUNT;
    private final String ENCODING = "&encode=base64";

    private final RestClient restClient;

    public TriviaAPIService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl(BASE_URL).build();
    }

    public TriviaAPIResponseDTO getQuestions() {
        var requestSpecification = restClient.get();

        var responseSpecification = requestSpecification.uri(ENDPOINT + ENCODING).retrieve()
                .onStatus(status -> status.value() == 429, (request, response) -> {
                    handleTooManyRequestResponse();
                })
                .onStatus(status -> status.isError(), (request, response) -> {
                    handleOtherExceptions(response.getStatusCode());
                });

        return responseSpecification.body(TriviaAPIResponseDTO.class);
    }

    private void handleTooManyRequestResponse() {
        throw new RateLimitExceededException("429: Too Many Requests");
    }

    private void handleOtherExceptions(HttpStatusCode statusCode) {
        throw new RuntimeException(
                "Something went wrong while calling the Trivia API (" + BASE_URL + ENDPOINT + "): "
                        + statusCode.toString());
    }
}