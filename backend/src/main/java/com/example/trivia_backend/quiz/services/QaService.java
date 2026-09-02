package com.example.trivia_backend.quiz.services;

import java.util.ArrayList;
import java.util.Collections;
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

    private final int MIN_AMOUNT_TO_FETCH_THRESHOLD = 5;

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

    public EvaluationResult evaluateAnswer(String id, String givenAnswer) {
        TriviaQuestion triviaQuestion = questionPoolMap.get(UUID.fromString(id));
        if (triviaQuestion == null) {
            handleTriviaQuestionNotFound(id);
        }

        if (givenAnswer == null || givenAnswer.isBlank()) {
            return new EvaluationResult(triviaQuestion.id(), "", false);
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

    private String parseBooleanToHumanReadable(String text) {
        if (text.toLowerCase().equals("true")) {
            return "Yes";
        } else if (text.toLowerCase().equals("false")) {
            return "No";
        }
        return text;
    }

    private boolean isTextBoolean(String text) {
        return text.toLowerCase().equals("true") || text.toLowerCase().equals("false");
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
        List<String> possibleAnswers = new ArrayList<>();

        for (String answer : triviaAPIQuestion.incorrectAnswers()) {
            if (isTextBoolean(answer)) {
                possibleAnswers.add(parseBooleanToHumanReadable(answer));
            } else {
                possibleAnswers.add(answer);
            }
        }

        String correctAnswer = triviaAPIQuestion.correctAnswer();
        if (isTextBoolean(correctAnswer)) {
            correctAnswer = parseBooleanToHumanReadable(correctAnswer);
        }
        possibleAnswers.add(correctAnswer);

        Collections.shuffle(possibleAnswers);

        return new TriviaQuestion(UUID.randomUUID(),
                triviaAPIQuestion.question(),
                correctAnswer, false, possibleAnswers);
    }

    public void clearQuestions() {
        this.questionPoolMap.clear();
    }

}
