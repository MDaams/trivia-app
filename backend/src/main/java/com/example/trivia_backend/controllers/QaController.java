package com.example.trivia_backend.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.example.trivia_backend.dtos.QuestionAndAnswerDTO;
import com.example.trivia_backend.models.QuestionAndAnswer;
import com.example.trivia_backend.services.QaService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    // POST /checkanswers
}
