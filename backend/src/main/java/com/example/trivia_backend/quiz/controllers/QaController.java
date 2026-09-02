package com.example.trivia_backend.quiz.controllers;

import java.util.ArrayList;
import java.util.List;

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
import com.example.trivia_backend.quiz.records.EvaluationResult;
import com.example.trivia_backend.quiz.records.TriviaQuestion;
import com.example.trivia_backend.quiz.services.QaService;

@RestController
public class QaController {

    private final QaService qaService;

    QaController(QaService qaService) {
        this.qaService = qaService;
    }

    @GetMapping("/questions")
    public TriviaQuestionsResponseDTO getQuestions(@RequestParam(defaultValue = "1") int amount) {
        List<TriviaQuestionResponseDTO> output = new ArrayList<>();

        for (TriviaQuestion question : qaService.getTriviaQuestions(amount))
            output.add(parseQuestionToDto(question));

        return new TriviaQuestionsResponseDTO(output);
    }

    @PostMapping("/checkAnswers")
    public ResponseEntity<ApiResponse> postCheckAnswers(
            @RequestBody CheckAnswersRequestDTO request) {
        if (!requestIsValid(request)) {
            return ResponseEntity.badRequest().build();
        }

        return gradeAnswers(request.answers());
    }

    private TriviaQuestionResponseDTO parseQuestionToDto(TriviaQuestion qa) {
        return new TriviaQuestionResponseDTO(qa.id().toString(), qa.question(), qa.possibleAnswers());
    }

    private CheckAnswerResponseDTO parseAnswerToDTO(EvaluationResult answerResult) {
        return new CheckAnswerResponseDTO(answerResult.id().toString(), answerResult.correctAnswer(),
                answerResult.isCorrect());
    }

    private boolean requestIsValid(CheckAnswersRequestDTO request) {
        return request.answers().stream().allMatch(req -> req.id() != null && req.answer() != null);
    }

    private ResponseEntity<ApiResponse> gradeAnswers(List<CheckAnswerRequestDTO> answers) {
        List<CheckAnswerResponseDTO> results = new ArrayList<>();

        for (CheckAnswerRequestDTO answer : answers) {
            try {
                EvaluationResult result = qaService.evaluateAnswer(answer.id(), answer.answer());
                results.add(parseAnswerToDTO(result));
            } catch (QuestionNotFoundException e) {
                return ResponseEntity.unprocessableContent().body(new ErrorResponseDTO(e.getMessage()));
            } catch (Exception e) {
                return ResponseEntity.internalServerError()
                        .body(new ErrorResponseDTO("Something went wrong during evaluation of answer."));
            }
        }

        return ResponseEntity.ok(new CheckAnswersResponseDTO(results));
    }
}