package com.example.trivia_backend.quiz.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.trivia_backend.opentDB.dtos.TriviaAPIResponseDTO;
import com.example.trivia_backend.opentDB.records.TriviaAPIQuestion;
import com.example.trivia_backend.opentDB.services.TriviaAPIService;
import com.example.trivia_backend.quiz.repositories.QaRepository;
import com.example.trivia_backend.quiz.records.EvaluationResult;
import com.example.trivia_backend.quiz.records.TriviaQuestion;

@ExtendWith(MockitoExtension.class)
class QaServiceTest {

    @InjectMocks
    private QaService service;

    @Mock
    private TriviaAPIService triviaAPIService;

    @Mock
    private QaRepository qaRepository;

    private String toBase64(String plainText) {
        if (plainText == null)
            return null;
        return Base64.getEncoder().encodeToString(plainText.getBytes(StandardCharsets.UTF_8));
    }

    private TriviaAPIResponseDTO createMockTriviaResponse() {
        TriviaAPIQuestion triviaQuestionOne = new TriviaAPIQuestion(toBase64("Kiwi? 1"), toBase64("Swagbaas"),
                List.of(toBase64("dab")));
        TriviaAPIQuestion triviaQuestionTwo = new TriviaAPIQuestion(toBase64("Kiwi? 2"), toBase64("Swagbaas"),
                List.of(toBase64("dab")));
        TriviaAPIQuestion triviaQuestionThree = new TriviaAPIQuestion(toBase64("Kiwi? 3"), toBase64("Swagbaas"),
                List.of(toBase64("dab")));
        TriviaAPIQuestion triviaQuestionFour = new TriviaAPIQuestion(toBase64("Kiwi? 4"), toBase64("Swagbaas"),
                List.of(toBase64("dab")));
        TriviaAPIQuestion triviaQuestionFive = new TriviaAPIQuestion(toBase64("Kiwi? 5"), toBase64("Swagbaas"),
                List.of(toBase64("dab")));
        TriviaAPIQuestion triviaQuestionSix = new TriviaAPIQuestion(toBase64("Kiwi? 6"), toBase64("Swagbaas"),
                List.of(toBase64("dab")));

        List<TriviaAPIQuestion> triviaQuestions = new ArrayList<>(List.of(triviaQuestionOne, triviaQuestionTwo,
                triviaQuestionThree, triviaQuestionFour, triviaQuestionFive, triviaQuestionSix));
        return new TriviaAPIResponseDTO(triviaQuestions);
    }

    @Test
    @DisplayName("It should combine the correct and the incorrect answers in a possible answers list")
    void shouldCombineCorrectAndIncorrectAnswers() {
        UUID questionId = UUID.randomUUID();
        TriviaQuestion qa = new TriviaQuestion(questionId, "Kiwi?", "Swagbaas", false,
                List.of("Swagbaas", "dab"));

        assertThat(qa.possibleAnswers()).contains("Swagbaas");
        assertThat(qa.possibleAnswers()).contains("dab");
    }

    @Test
    @DisplayName("The service should be able to evaluate answers")
    void shouldEvaluateAnswer() {
        UUID questionId = UUID.randomUUID();
        TriviaQuestion qa = new TriviaQuestion(questionId, "Kiwi?", "Swagbaas", false,
                List.of("Swagbaas", "dab"));

        when(qaRepository.fetchAndRemoveAnsweredQuestion(questionId.toString())).thenReturn(qa);

        EvaluationResult result = service.evaluateAnswer(questionId.toString(), "Swagbaas");

        assertThat(result.isCorrect()).isTrue();
        assertThat(result.correctAnswer()).isEqualTo(qa.correctAnswer());
    }

    @Test
    @DisplayName("The service should validate that given answer is not blank")
    void shouldThrowExceptionOnBlankAnswer() {
        UUID questionId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () -> {
            service.evaluateAnswer(questionId.toString(), "");
        });
    }

    @Test
    @DisplayName("The service should validate that given answer is not null")
    void shouldThrowExceptionOnNullAnswer() {
        UUID questionId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () -> {
            service.evaluateAnswer(questionId.toString(), null);
        });
    }

    @Test
    @DisplayName("When pool has less than five unused trivia questions it should fetch a new batch from API")
    void getTriviaQuestionsFetchesNewBatchWhenBelowThreshold() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        TriviaQuestion q1 = new TriviaQuestion(id1, "Q1", "A1", false, List.of("A1", "B1"));
        TriviaQuestion q2 = new TriviaQuestion(id2, "Q2", "A2", false, List.of("A2", "B2"));

        when(qaRepository.countUnusedquestions()).thenReturn(1L).thenReturn(1L);
        when(qaRepository.getTriviaQuestions(5)).thenReturn(List.of(q1, q2));
        when(triviaAPIService.getQuestions()).thenReturn(createMockTriviaResponse());

        service.getTriviaQuestions(5);

        verify(triviaAPIService, times(1)).getQuestions();
    }

    @Test
    @DisplayName("Should not fetch new questions when pool has sufficient unused questions")
    void getTriviaQuestionsDoesNotFetchWhenAboveThreshold() {
        UUID id1 = UUID.randomUUID();
        TriviaQuestion q1 = new TriviaQuestion(id1, "Q1", "A1", false, List.of("A1", "B1"));

        when(qaRepository.countUnusedquestions()).thenReturn(10L);
        when(qaRepository.getTriviaQuestions(1)).thenReturn(List.of(q1));

        service.getTriviaQuestions(1);

        verify(triviaAPIService, times(0)).getQuestions();
    }

    @Test
    @DisplayName("The service should mark retrieved questions as presented")
    void shouldMarkRetrievedQuestionsAsPresented() {
        UUID id1 = UUID.randomUUID();
        TriviaQuestion q1 = new TriviaQuestion(id1, "Q1", "A1", false, List.of("A1", "B1"));

        when(qaRepository.countUnusedquestions()).thenReturn(10L);
        when(qaRepository.getTriviaQuestions(1)).thenReturn(List.of(q1));

        service.getTriviaQuestions(1);

        verify(qaRepository, times(1)).setToPresented(q1);
    }

    @Test
    @DisplayName("Handles exception during fetching from Trivia API service")
    void fetchQuestionsShouldHandleException() {
        when(qaRepository.countUnusedquestions()).thenReturn(1L);
        when(triviaAPIService.getQuestions()).thenThrow(new RuntimeException("API Error"));
        when(qaRepository.getTriviaQuestions(anyInt())).thenReturn(List.of());

        List<TriviaQuestion> triviaQuestions = service.getTriviaQuestions(5);

        assertThat(triviaQuestions).hasSize(0);
    }

    @Test
    @DisplayName("It should parse boolean strings true and false to human readable Yes and No")
    void shouldParseBooleanValuesToHumanReadable() {
        TriviaAPIQuestion mockQuestion = new TriviaAPIQuestion(
                toBase64("kiwi?"),
                toBase64("true"),
                List.of(toBase64("false")));
        TriviaAPIResponseDTO booleanResponse = new TriviaAPIResponseDTO(List.of(mockQuestion));

        when(qaRepository.countUnusedquestions()).thenReturn(1L);
        when(triviaAPIService.getQuestions()).thenReturn(booleanResponse);

        UUID id = UUID.randomUUID();
        TriviaQuestion parsedQuestion = new TriviaQuestion(id, "kiwi?", "Yes", false,
                List.of("Yes", "No"));
        when(qaRepository.getTriviaQuestions(1)).thenReturn(List.of(parsedQuestion));

        List<TriviaQuestion> questions = service.getTriviaQuestions(1);

        assertThat(questions).hasSize(1);
        TriviaQuestion result = questions.get(0);

        assertThat(result.correctAnswer()).isEqualTo("Yes");
        assertThat(result.possibleAnswers()).contains("No");
        assertThat(result.possibleAnswers()).doesNotContain("true");
        assertThat(result.possibleAnswers()).doesNotContain("false");
    }
}
