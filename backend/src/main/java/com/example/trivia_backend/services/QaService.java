package com.example.trivia_backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Service;

import com.example.trivia_backend.dtos.AnswerResult;
import com.example.trivia_backend.dtos.TriviaAPIResponseDTO;
import com.example.trivia_backend.dtos.TriviaAPIResponseDTO.TriviaQuestionDto;
import com.example.trivia_backend.exceptions.QuestionNotFoundException;
import com.example.trivia_backend.models.QuestionAndAnswer;

@Service
public class QaService {
    private final TriviaAPIService triviaAPIService;

    private final AtomicReference<List<QuestionAndAnswer>> questionsAndAnswers;

    public QaService(TriviaAPIService triviaAPIService) {
        questionsAndAnswers = new AtomicReference<>(List.of());
        this.triviaAPIService = triviaAPIService;
        this.fetchQuestions();
    }

    private void setQuestionAndAnswers(List<QuestionAndAnswer> updatedQuestionAndAnswers) {
        this.questionsAndAnswers.set(updatedQuestionAndAnswers);
    }

    public List<QuestionAndAnswer> getQuestionsAndAnswers() {
        return questionsAndAnswers.get();
    }

    public void clearQuestionAndAnswers() {
        this.questionsAndAnswers.set(List.of());
    }

    private QuestionAndAnswer findById(String id) {
        for (QuestionAndAnswer qa : this.getQuestionsAndAnswers()) {
            if (qa.id().equals(id)) {
                return qa;
            }
        }
        return null;
    }

    private int findIndexByQuestion(String question, List<QuestionAndAnswer> questionAndAnswers) {
        for (int i = 0; i < questionAndAnswers.size(); i++) {
            QuestionAndAnswer qa = questionAndAnswers.get(i);
            if (qa.question().equals(question)) {
                return i;
            }
        }
        return -1;
    }

    public List<QuestionAndAnswer> deleteQuestionAndAnswers(QuestionAndAnswer qa) {
        return this.questionsAndAnswers.updateAndGet(currentList -> {
            List<QuestionAndAnswer> updatedQuestionAndAnswers = new ArrayList<>(currentList);
            int index = findIndexByQuestion(qa.question(), updatedQuestionAndAnswers);

            updatedQuestionAndAnswers.remove(index);

            return updatedQuestionAndAnswers;
        });
    }

    public QuestionAndAnswer getFirstQuestion() {
        List<QuestionAndAnswer> questionAndAnswers = new ArrayList<>(this.getQuestionsAndAnswers());

        if (questionAndAnswers.isEmpty()) {
            return null;
        }

        return questionAndAnswers.get(0);
    }

    public AnswerResult evaluateAnswer(String id, String givenAnswer) {
        QuestionAndAnswer questionAndAnswer = this.findById(id);

        if (questionAndAnswer == null) {
            throw new QuestionNotFoundException("Question not found.");
        }

        deleteQuestionAndAnswers(questionAndAnswer);

        return new AnswerResult(questionAndAnswer.isCorrect(givenAnswer), questionAndAnswer.correctAnswer());
    }

    public List<QuestionAndAnswer> addQuestion(QuestionAndAnswer qa) {
        return this.questionsAndAnswers.updateAndGet(currentList -> {
            List<QuestionAndAnswer> updatedQuestionAndAnswers = new ArrayList<>(currentList);

            if (findIndexByQuestion(qa.question(), updatedQuestionAndAnswers) > -1) {
                return updatedQuestionAndAnswers;
            }

            updatedQuestionAndAnswers.add(qa);

            return updatedQuestionAndAnswers;
        });
    }

    public void fetchQuestions() {
        this.clearQuestionAndAnswers();
        TriviaAPIResponseDTO responseDTO = triviaAPIService.getQuestions();
        List<TriviaQuestionDto> questionsAndAnswerDtos = responseDTO.results();

        List<QuestionAndAnswer> questionAndAnswers = new ArrayList<>();
        for (int i = 0; i < questionsAndAnswerDtos.size(); i++) {
            TriviaQuestionDto triviaQuestionDto = questionsAndAnswerDtos.get(i);
            List<String> possibleAnswers = new ArrayList<>(triviaQuestionDto.incorrectAnswers());
            possibleAnswers.add(triviaQuestionDto.correctAnswer());
            QuestionAndAnswer questionAndAnswer = new QuestionAndAnswer(UUID.randomUUID().toString(),
                    triviaQuestionDto.question(),
                    triviaQuestionDto.correctAnswer(), possibleAnswers);

            questionAndAnswers.add(questionAndAnswer);
        }

        this.setQuestionAndAnswers(questionAndAnswers);
    }
}
