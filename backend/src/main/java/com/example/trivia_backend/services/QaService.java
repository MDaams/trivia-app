package com.example.trivia_backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.trivia_backend.dtos.TriviaAPIResponseDTO;
import com.example.trivia_backend.dtos.TriviaAPIResponseDTO.TriviaAPIQuestion;
import com.example.trivia_backend.exceptions.QuestionNotFoundException;
import com.example.trivia_backend.records.EvaluationResult;
import com.example.trivia_backend.records.TriviaQuestion;

@Service
public class QaService {
    private static final Logger log = LoggerFactory.getLogger(QaService.class);

    private final int MIN_AMOUNT_TO_FETCH_THRESHOLD = 5;

    private final TriviaAPIService triviaAPIService;

    private final AtomicReference<List<TriviaQuestion>> questionPool;

    public QaService(TriviaAPIService triviaAPIService) {
        questionPool = new AtomicReference<>(List.of());
        this.triviaAPIService = triviaAPIService;
    }

    public List<TriviaQuestion> addTriviaQuestionToPool(TriviaQuestion qa) {
        return this.questionPool.updateAndGet(currentList -> {
            List<TriviaQuestion> updatedQuestionPool = new ArrayList<>(currentList);

            if (findIndexByQuestion(qa.question(), updatedQuestionPool) > -1) {
                return updatedQuestionPool;
            }

            updatedQuestionPool.add(qa);

            return updatedQuestionPool;
        });
    }

    public EvaluationResult evaluateAnswer(String id, String givenAnswer) {
        TriviaQuestion triviaQuestion = this.findById(id);

        if (triviaQuestion == null) {
            handleTriviaQuestionNotFound(id);
        }

        removeFromPool(triviaQuestion);

        return new EvaluationResult(triviaQuestion.id(), triviaQuestion.correctAnswer(),
                triviaQuestion.isCorrect(givenAnswer));
    }

    public List<TriviaQuestion> getTriviaQuestions(int amount) {
        if (questionPool.get().size() < MIN_AMOUNT_TO_FETCH_THRESHOLD) {
            fetchTriviaAPIQuestions();
        }

        List<TriviaQuestion> selectedQuestions = getAmountFromPool(amount);

        sanityCheck(selectedQuestions);

        return selectedQuestions;
    }

    private void sanityCheck(List<TriviaQuestion> selectedQuestions) {
        if (selectedQuestions.isEmpty()) {
            log.warn("Output is empty. It might be because the QuestionPool is empty. Handling gracefully.");
        }
    }

    private List<TriviaQuestion> getAmountFromPool(int amount) {
        List<TriviaQuestion> output = new ArrayList<>();
        int amountAdded = 0;

        for (TriviaQuestion qa : this.questionPool.get()) {
            if (amountAdded >= this.questionPool.get().size() || amountAdded >= amount) {
                break;
            }
            output.add(qa);
            amountAdded++;
        }

        return output;
    }

    private void handleTriviaQuestionNotFound(String id) {
        log.error("Lookup for question failed. Requested ID: {}", id);
        throw new QuestionNotFoundException("Question not found.");
    }

    private List<TriviaQuestion> removeFromPool(TriviaQuestion qa) {
        return this.questionPool.updateAndGet(currentList -> {
            List<TriviaQuestion> updatedQuestionPool = new ArrayList<>(currentList);

            int index = findIndexByQuestion(qa.question(), updatedQuestionPool);

            updatedQuestionPool.remove(index);

            return updatedQuestionPool;
        });
    }

    private void fetchTriviaAPIQuestions() {
        TriviaAPIResponseDTO responseDTO = null;

        try {
            responseDTO = triviaAPIService.getQuestions();
        } catch (Exception e) {
            log.error("Fetching of external Trivia questions failed. Error: {}", e.getMessage());
            return;
        }

        for (TriviaAPIQuestion triviaQuestion : responseDTO.results()) {
            this.addTriviaQuestionToPool(parseDTOToTriviaQuestion(triviaQuestion));
        }
    }

    private TriviaQuestion parseDTOToTriviaQuestion(TriviaAPIQuestion triviaAPIQuestion) {
        List<String> possibleAnswers = new ArrayList<>(triviaAPIQuestion.incorrectAnswers());
        possibleAnswers.add(triviaAPIQuestion.correctAnswer());
        return new TriviaQuestion(UUID.randomUUID().toString(),
                triviaAPIQuestion.question(),
                triviaAPIQuestion.correctAnswer(), possibleAnswers);
    }

    private TriviaQuestion findById(String id) {
        for (TriviaQuestion triviaQuestion : this.questionPool.get()) {
            if (triviaQuestion.id().equals(id)) {
                return triviaQuestion;
            }
        }
        return null;
    }

    private int findIndexByQuestion(String question, List<TriviaQuestion> triviaQuestions) {
        for (int i = 0; i < triviaQuestions.size(); i++) {
            if (triviaQuestions.get(i).question().equals(question)) {
                return i;
            }
        }
        return -1;
    }
}
