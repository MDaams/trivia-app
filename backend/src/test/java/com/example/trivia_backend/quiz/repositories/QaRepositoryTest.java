package com.example.trivia_backend.quiz.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.trivia_backend.quiz.exceptions.QuestionNotFoundException;
import com.example.trivia_backend.quiz.records.TriviaQuestion;

class QaRepositoryTest {

    private QaRepository repository;

    @BeforeEach
    void beforeEach() {
        repository = new QaRepository();
    }

    @Test
    @DisplayName("Should add a batch of questions to the pool")
    void shouldAddBatchOfQuestions() {
        List<TriviaQuestion> batch = createQuestionBatch(5);

        repository.addBatchedQuestions(batch);

        assertThat(repository.countTotalAmountOfQuestions()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should not add duplicate questions based on question text")
    void shouldNotAddDuplicateQuestions() {
        List<TriviaQuestion> batch = createQuestionBatch(3);
        repository.addBatchedQuestions(batch);

        String duplicateQuestionText = "Question 1";
        TriviaQuestion duplicate = new TriviaQuestion(
                UUID.randomUUID(),
                duplicateQuestionText,
                "Answer",
                false,
                List.of("Answer", "Wrong"));

        repository.addBatchedQuestions(List.of(duplicate));

        assertThat(repository.countTotalAmountOfQuestions()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should retrieve the requested amount of unused questions")
    void getTriviaQuestionsShouldReturnRequestedAmount() {
        List<TriviaQuestion> batch = createQuestionBatch(5);
        repository.addBatchedQuestions(batch);

        assertThat(repository.getTriviaQuestions(2)).hasSize(2);
    }

    @Test
    @DisplayName("Should return all available questions if requested amount exceeds pool size")
    void getTriviaQuestionsShouldReturnAllAvailableIfRequestedAmountIsMoreThanStored() {
        List<TriviaQuestion> batch = createQuestionBatch(3);
        repository.addBatchedQuestions(batch);

        assertThat(repository.getTriviaQuestions(10)).hasSize(3);
    }

    @Test
    @DisplayName("Should remove question after it is answered")
    void shouldDeleteQuestionAfterEvaluatingAnswer() {
        List<TriviaQuestion> batch = createQuestionBatch(3);
        repository.addBatchedQuestions(batch);

        TriviaQuestion questionToRemove = repository.getTriviaQuestions(1).get(0);
        repository.fetchAndRemoveAnsweredQuestion(questionToRemove.id().toString());

        assertThat(repository.countTotalAmountOfQuestions()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should throw QuestionNotFoundException when fetching non-existent question")
    void shouldThrowExceptionWhenFetchingNonExistentQuestion() {
        List<TriviaQuestion> batch = createQuestionBatch(2);
        repository.addBatchedQuestions(batch);

        TriviaQuestion question = repository.getTriviaQuestions(1).get(0);
        repository.fetchAndRemoveAnsweredQuestion(question.id().toString());

        assertThrows(QuestionNotFoundException.class, () -> {
            repository.fetchAndRemoveAnsweredQuestion(question.id().toString());
        });
    }

    @Test
    @DisplayName("Should mark question as presented")
    void shouldMarkQuestionAsPresented() {
        List<TriviaQuestion> batch = createQuestionBatch(2);
        repository.addBatchedQuestions(batch);

        TriviaQuestion question = repository.getTriviaQuestions(1).get(0);
        assertThat(question.isPresented()).isFalse();

        repository.setToPresented(question);

        assertThat(repository.getTriviaQuestions(10)).hasSize(1);
    }

    @Test
    @DisplayName("Should count only unused questions")
    void shouldCountOnlyUnusedQuestions() {
        List<TriviaQuestion> batch = createQuestionBatch(5);
        repository.addBatchedQuestions(batch);

        assertThat(repository.countUnusedquestions()).isEqualTo(5);

        TriviaQuestion question = repository.getTriviaQuestions(1).get(0);
        repository.setToPresented(question);

        assertThat(repository.countUnusedquestions()).isEqualTo(4);
    }

    @Test
    @DisplayName("Should clear all questions from the pool")
    void shouldClearAllQuestions() {
        List<TriviaQuestion> batch = createQuestionBatch(5);
        repository.addBatchedQuestions(batch);

        repository.clearQuestions();

        assertThat(repository.countTotalAmountOfQuestions()).isEqualTo(0);
    }

    private List<TriviaQuestion> createQuestionBatch(int size) {
        List<TriviaQuestion> batch = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            batch.add(new TriviaQuestion(
                    UUID.randomUUID(),
                    "Question " + i,
                    "Correct Answer",
                    false,
                    List.of("Correct Answer", "Wrong Answer")));
        }
        return batch;
    }
}
