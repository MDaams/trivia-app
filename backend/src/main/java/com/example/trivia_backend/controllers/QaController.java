package com.example.trivia_backend.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.example.trivia_backend.dtos.AnswerResult;
import com.example.trivia_backend.dtos.CheckAnswerRequestDTO;
import com.example.trivia_backend.dtos.CheckAnswerResponseDTO;
import com.example.trivia_backend.dtos.QuestionAndAnswerDTO;
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
    public List<QuestionAndAnswerDTO> getQuestions() {
        List<QuestionAndAnswerDTO> output = new ArrayList<>();
        List<QuestionAndAnswer> questions = qaService.getQuestionsAndAnswers();

        for (int i = 0; i < questions.size(); i++) {
            QuestionAndAnswer qa = questions.get(i);
            QuestionAndAnswerDTO qaDTO = new QuestionAndAnswerDTO(qa.id(), qa.question(), qa.possibleAnswers());
            output.add(qaDTO);
        }
        return output;
    }

    @GetMapping("/question")
    public QuestionAndAnswerDTO getQuestion() {
        QuestionAndAnswer qa = qaService.getFirstQuestion();
        return new QuestionAndAnswerDTO(qa.id(), qa.question(), qa.possibleAnswers());
    }

    @PostMapping("/checkanswers")
    public ResponseEntity<List<CheckAnswerResponseDTO>> postCheckAnswers(
            @RequestBody List<CheckAnswerRequestDTO> requests) {
        List<CheckAnswerResponseDTO> response = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            CheckAnswerRequestDTO request = requests.get(i);

            if (request.id() == null || request.answer() == null) {
                return ResponseEntity.badRequest().build();
            }

            AnswerResult result = qaService.evaluateAnswer(request.id(), request.answer());

            CheckAnswerResponseDTO responseDto = new CheckAnswerResponseDTO(result.isCorrect(), result.correctAnswer());

            response.add(responseDto);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/checkanswer")
    public ResponseEntity<CheckAnswerResponseDTO> postCheckAnswer(@RequestBody CheckAnswerRequestDTO request) {
        if (request.id() == null || request.answer() == null) {
            return ResponseEntity.badRequest().build();
        }

        AnswerResult result = qaService.evaluateAnswer(request.id(), request.answer());

        CheckAnswerResponseDTO response = new CheckAnswerResponseDTO(result.isCorrect(), result.correctAnswer());

        return ResponseEntity.ok(response);
    }
}