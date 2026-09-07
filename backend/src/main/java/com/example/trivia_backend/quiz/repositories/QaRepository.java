package com.example.trivia_backend.quiz.repositories;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.example.trivia_backend.quiz.exceptions.QuestionNotFoundException;
import com.example.trivia_backend.quiz.records.TriviaQuestion;

@Repository
public class QaRepository {
    private static final Logger log = LoggerFactory.getLogger(QaRepository.class);

    private final ConcurrentHashMap<UUID, TriviaQuestion> questionPoolMap;

    public QaRepository() {
        questionPoolMap = new ConcurrentHashMap<>();
    }

    public List<TriviaQuestion> getTriviaQuestions(int amount) {
        return questionPoolMap.values().stream()
                .filter(TriviaQuestion::canBeUsed)
                .limit(amount)
                .toList();
    }

    public void addBatchedQuestions(List<TriviaQuestion> batch) {
        for (TriviaQuestion triviaQuestion : batch) {
            if (questionValueExists(triviaQuestion.question())) {
                break;
            }

            questionPoolMap.putIfAbsent(triviaQuestion.id(), triviaQuestion);
        }
    }

    public void clearQuestions() {
        this.questionPoolMap.clear();
    }

    public TriviaQuestion fetchAndRemoveAnsweredQuestion(String id) {
        TriviaQuestion removedQuestion = questionPoolMap.remove(UUID.fromString(id));
        if (removedQuestion == null) {
            handleTriviaQuestionNotFound(id);
        }

        return removedQuestion;
    }

    public long countUnusedquestions() {
        return this.questionPoolMap.values().stream().filter(TriviaQuestion::canBeUsed).count();
    }

    private boolean questionValueExists(String questionValue) {
        return questionPoolMap.values().stream()
                .anyMatch(q -> q.isEqual(questionValue));
    }

    private void handleTriviaQuestionNotFound(String id) {
        log.warn("Lookup for question failed. Requested ID: {}", id);
        throw new QuestionNotFoundException("Question not found.");
    }

    public boolean setToPresented(TriviaQuestion toUpdate) {
        TriviaQuestion replacement = new TriviaQuestion(toUpdate.id(), toUpdate.question(),
                toUpdate.correctAnswer(), true, toUpdate.possibleAnswers());
        return questionPoolMap.replace(toUpdate.id(), toUpdate, replacement);
    }

    public long countTotalAmountOfQuestions() {
        return this.questionPoolMap.size();

    }

}
