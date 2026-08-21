package com.example.trivia_backend.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.example.trivia_backend.dtos.AnswerResult;
import com.example.trivia_backend.dtos.ApiResponse;
import com.example.trivia_backend.dtos.CheckAnswerRequestDTO;
import com.example.trivia_backend.dtos.CheckAnswerResponseDTO;
import com.example.trivia_backend.dtos.CheckAnswersResponseDTO;
import com.example.trivia_backend.dtos.ErrorResponseDTO;
import com.example.trivia_backend.dtos.QuestionAndAnswerResponseDTO;
import com.example.trivia_backend.models.QuestionAndAnswer;
import com.example.trivia_backend.services.QaService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class QaController {

    private final QaService qaService;

    QaController(QaService qaService) {
        this.qaService = qaService;
    }

    @GetMapping("/questions")
    public List<QuestionAndAnswerResponseDTO> getQuestions() {
        List<QuestionAndAnswerResponseDTO> output = new ArrayList<>();
        List<QuestionAndAnswer> questions = qaService.getQuestionsAndAnswers();

        for (int i = 0; i < questions.size(); i++) {
            QuestionAndAnswer qa = questions.get(i);
            QuestionAndAnswerResponseDTO qaDTO = new QuestionAndAnswerResponseDTO(qa.id(), qa.question(),
                    qa.possibleAnswers());
            output.add(qaDTO);
        }
        return output;
    }

    @GetMapping("/question")
    public QuestionAndAnswerResponseDTO getQuestion() {
        QuestionAndAnswer qa = qaService.getFirstQuestion();
        return new QuestionAndAnswerResponseDTO(qa.id(), qa.question(), qa.possibleAnswers());
    }

    @PostMapping("/checkanswers")
    public ResponseEntity<ApiResponse> postCheckAnswers(
            @RequestBody List<CheckAnswerRequestDTO> requests) {
        List<CheckAnswerResponseDTO> results = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            CheckAnswerRequestDTO request = requests.get(i);

            if (request.id() == null || request.answer() == null) {
                return ResponseEntity.badRequest().build();
            }

            AnswerResult result = null;
            try {
                result = qaService.evaluateAnswer(request.id(), request.answer());
            } catch (Exception e) {
                System.err.println(e.getMessage());
                return ResponseEntity.unprocessableContent().body(new ErrorResponseDTO(e.getMessage()));

            }

            results.add(new CheckAnswerResponseDTO(result.isCorrect(),
                    result.correctAnswer()));
        }

        return ResponseEntity.ok(new CheckAnswersResponseDTO(results));
    }

    @PostMapping("/checkanswer")
    public ResponseEntity<ApiResponse> postCheckAnswer(@RequestBody CheckAnswerRequestDTO request) {
        if (request.id() == null || request.answer() == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            AnswerResult result = qaService.evaluateAnswer(request.id(), request.answer());
            new CheckAnswerResponseDTO(result.isCorrect(), result.correctAnswer());
            return ResponseEntity.ok(new CheckAnswerResponseDTO(result.isCorrect(), result.correctAnswer()));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.unprocessableContent().body(new ErrorResponseDTO(e.getMessage()));
        }
    }
}