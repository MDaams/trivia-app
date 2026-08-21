package com.example.trivia_backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

    private final ConcurrentHashMap<UUID, TriviaQuestion> questionPoolMap;

    public QaService(TriviaAPIService triviaAPIService) {
        questionPoolMap = new ConcurrentHashMap<>();
        this.triviaAPIService = triviaAPIService;
    }

    public List<TriviaQuestion> addTriviaQuestionToPool(TriviaQuestion qa) {
        questionPoolMap.putIfAbsent(qa.id(), qa);

        return new ArrayList<>(questionPoolMap.values());
    }

    public EvaluationResult evaluateAnswer(String id, String givenAnswer) {
        TriviaQuestion triviaQuestion = questionPoolMap.remove(UUID.fromString(id));
        if (triviaQuestion == null) {
            handleTriviaQuestionNotFound(id);
        }

        return new EvaluationResult(triviaQuestion.id(), triviaQuestion.correctAnswer(),
                triviaQuestion.isCorrect(givenAnswer));
    }

    public List<TriviaQuestion> getTriviaQuestions(int amount) {
        if (questionPoolMap.size() < MIN_AMOUNT_TO_FETCH_THRESHOLD) {
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

        for (Map.Entry<UUID, TriviaQuestion> entry : questionPoolMap.entrySet()) {
            TriviaQuestion triviaQuestion = entry.getValue();
            if (amountAdded >= questionPoolMap.size() || amountAdded >= amount) {
                break;
            }

            if (triviaQuestion.isPresented()) {
                continue;
            }

            if (setToPresented(triviaQuestion)) {
                output.add(triviaQuestion);
                amountAdded++;
            }
        }
        return output;
    }

    private boolean setToPresented(TriviaQuestion toUpdate) {
        TriviaQuestion replacement = new TriviaQuestion(toUpdate.id(), toUpdate.question(),
                toUpdate.correctAnswer(), true, toUpdate.possibleAnswers());
        return questionPoolMap.replace(toUpdate.id(), toUpdate, replacement);
    }

    private void handleTriviaQuestionNotFound(String id) {
        log.error("Lookup for question failed. Requested ID: {}", id);
        throw new QuestionNotFoundException("Question not found.");
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
        return new TriviaQuestion(UUID.randomUUID(),
                triviaAPIQuestion.question(),
                triviaAPIQuestion.correctAnswer(), false, possibleAnswers);
    }

}
