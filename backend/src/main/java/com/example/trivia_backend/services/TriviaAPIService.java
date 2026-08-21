package com.example.trivia_backend.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.trivia_backend.dtos.TriviaAPIResponseDTO;
import com.example.trivia_backend.exceptions.RateLimitExceededException;

@Service
public class TriviaAPIService {
    private final String AMOUNT = "50";
    private final String BASE_URL = "https://opentdb.com";
    private final String ENDPOINT = "/api.php?amount=" + AMOUNT;

    private final RestClient restClient;

    public TriviaAPIService() {
        this.restClient = RestClient.builder().baseUrl(BASE_URL).build();
    }

    public TriviaAPIResponseDTO getQuestions() {
        var requestSpecification = restClient.get();

        var responseSpecification = requestSpecification.uri(ENDPOINT).retrieve()
                .onStatus(status -> status.value() == 429, (request, response) -> {
                    throw new RateLimitExceededException("429: Too Many Requests");
                })
                .onStatus(status -> status.isError(), (request, response) -> {
                    throw new RuntimeException(
                            "Something went wrong while calling the Trivia API (" + BASE_URL + ENDPOINT + "): "
                                    + response.getStatusCode());
                });

        return responseSpecification.body(TriviaAPIResponseDTO.class);
    }
}