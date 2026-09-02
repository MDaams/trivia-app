package com.example.trivia_backend.quiz.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.trivia_backend.quiz.dtos.ApiResponse;
import com.example.trivia_backend.quiz.dtos.checkAnswer.CheckAnswerRequestDTO;
import com.example.trivia_backend.quiz.dtos.checkAnswer.CheckAnswerResponseDTO;
import com.example.trivia_backend.quiz.dtos.checkAnswer.CheckAnswersRequestDTO;
import com.example.trivia_backend.quiz.dtos.checkAnswer.CheckAnswersResponseDTO;
import com.example.trivia_backend.quiz.dtos.exception.ErrorResponseDTO;
import com.example.trivia_backend.quiz.dtos.questions.TriviaQuestionResponseDTO;
import com.example.trivia_backend.quiz.dtos.questions.TriviaQuestionsResponseDTO;
import com.example.trivia_backend.quiz.exceptions.QuestionNotFoundException;
import com.example.trivia_backend.quiz.records.TriviaQuestion;
import com.example.trivia_backend.quiz.services.QaService;

@RestController
public class QaController {

    private static final Logger log = LoggerFactory.getLogger(QaController.class);

    private final QaService qaService;

    QaController(QaService qaService) {
        this.qaService = qaService;
    }

    @GetMapping("/questions")
    public ResponseEntity<ApiResponse> getQuestions(@RequestParam(defaultValue = "1") int amount) {
        try {
            return ResponseEntity.ok(new TriviaQuestionsResponseDTO(getUsableQuestions(amount)));
        } catch (Exception e) {
            return handleGenericException("/questions", e);
        }
    }

    @PostMapping("/checkAnswers")
    public ResponseEntity<ApiResponse> postCheckAnswers(
            @RequestBody CheckAnswersRequestDTO request) {
        if (!requestIsValid(request)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            return ResponseEntity.ok(gradeAnswers(request.answers()));
        } catch (QuestionNotFoundException e) {
            return handleQuestionNotFound(e.getMessage());
        } catch (Exception e) {
            return handleGenericException("/checkanswers", e);
        }
    }

    private List<TriviaQuestionResponseDTO> getUsableQuestions(int amount) {
        return qaService.getTriviaQuestions(amount).stream()
                .map(TriviaQuestion::toDTO).toList();
    }

    private boolean requestIsValid(CheckAnswersRequestDTO request) {
        return request.answers().stream().allMatch(CheckAnswerRequestDTO::isValid);
    }

    private ResponseEntity<ApiResponse> handleQuestionNotFound(String errorMessage) {
        return ResponseEntity.unprocessableContent().body(new ErrorResponseDTO(errorMessage));
    }

    private ResponseEntity<ApiResponse> handleGenericException(String endpointName, Exception e) {
        log.error("Error in {}: {}", endpointName, e.getMessage(), e);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponseDTO("Something went wrong on the server."));
    }

    private CheckAnswersResponseDTO gradeAnswers(List<CheckAnswerRequestDTO> answers) {
        List<CheckAnswerResponseDTO> output = answers.stream()
                .map(a -> qaService.evaluateAnswer(a.id(), a.answer()).toDTO())
                .toList();
        return new CheckAnswersResponseDTO(output);
    }
}