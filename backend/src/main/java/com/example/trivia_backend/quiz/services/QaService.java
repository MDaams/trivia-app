package com.example.trivia_backend.quiz.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.trivia_backend.opentDB.dtos.TriviaAPIResponseDTO;
import com.example.trivia_backend.opentDB.records.TriviaAPIQuestion;
import com.example.trivia_backend.opentDB.services.TriviaAPIService;
import com.example.trivia_backend.quiz.exceptions.QuestionNotFoundException;
import com.example.trivia_backend.quiz.records.EvaluationResult;
import com.example.trivia_backend.quiz.records.TriviaQuestion;

@Service
public class QaService {
    private static final Logger log = LoggerFactory.getLogger(QaService.class);

    private static final int MIN_AMOUNT_TO_FETCH_THRESHOLD = 5;

    private final TriviaAPIService triviaAPIService;

    private final ConcurrentHashMap<UUID, TriviaQuestion> questionPoolMap;

    public QaService(TriviaAPIService triviaAPIService) {
        questionPoolMap = new ConcurrentHashMap<>();
        this.triviaAPIService = triviaAPIService;
    }

    public List<TriviaQuestion> addTriviaQuestionToPool(TriviaQuestion qa) {
        // Possible race condition
        // Impact: Duplicate trivia questions
        // Risk is accepted because they still have a unique ID
        if (questionValueExists(qa.question())) {
            return new ArrayList<>(questionPoolMap.values());
        }

        questionPoolMap.putIfAbsent(qa.id(), qa);

        return new ArrayList<>(questionPoolMap.values());

    }

    private void validateGivenAnswer(String givenAnswer) {
        if (givenAnswer == null || givenAnswer.isBlank()) {
            throw new RuntimeException("Given answer should have a value.");
        }
    }

    public EvaluationResult evaluateAnswer(String id, String givenAnswer) {
        validateGivenAnswer(givenAnswer);

        TriviaQuestion triviaQuestion = questionPoolMap.get(UUID.fromString(id));
        if (triviaQuestion == null) {
            handleTriviaQuestionNotFound(id);
        }

        TriviaQuestion removedQuestion = questionPoolMap.remove(triviaQuestion.id());
        if (removedQuestion == null) {
            handleTriviaQuestionNotFound(id);
        }

        return new EvaluationResult(removedQuestion.id(), removedQuestion.correctAnswer(),
                removedQuestion.isCorrect(givenAnswer));
    }

    long amountOfUnusedQuestions() {
        return questionPoolMap.values().stream().filter(TriviaQuestion::canBeUsed).count();
    }

    public List<TriviaQuestion> getTriviaQuestions(int amount) {
        if (amountOfUnusedQuestions() < MIN_AMOUNT_TO_FETCH_THRESHOLD) {
            // Possible race condition
            // Impact: Two times a fetch is done
            // Risk is accepted because the max limit is 50 questions and they get
            // self-destroyed when answered
            fetchTriviaAPIQuestions();
        }

        List<TriviaQuestion> selectedQuestions = getAmountFromPool(amount);

        sanityCheck(selectedQuestions);

        return selectedQuestions;
    }

    private boolean questionValueExists(String questionValue) {
        return questionPoolMap.values().stream()
                .anyMatch(q -> q.isEqual(questionValue));
    }

    private void sanityCheck(List<TriviaQuestion> selectedQuestions) {
        if (selectedQuestions.isEmpty()) {
            log.warn("Output of getQuestions is empty. Usable questions: {}. Total questions in pool: {}.",
                    amountOfUnusedQuestions(), questionPoolMap.size());
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
        log.warn("Lookup for question failed. Requested ID: {}", id);
        throw new QuestionNotFoundException("Question not found.");
    }

    private void fetchTriviaAPIQuestions() {
        TriviaAPIResponseDTO responseDTO = null;

        try {
            responseDTO = triviaAPIService.getQuestions();
        } catch (Exception e) {
            log.error("Fetching of external Trivia questions failed. {}", e.getMessage(), e);
            return;
        }

        processTriviaAPIQuestions(responseDTO);
    }

    private void processTriviaAPIQuestions(TriviaAPIResponseDTO responseDTO) {
        responseDTO.results().stream()
                .map(TriviaAPIQuestion::toTriviaQuestion)
                .forEach(this::addTriviaQuestionToPool);

    }

    public void clearQuestions() {
        this.questionPoolMap.clear();
    }

}
