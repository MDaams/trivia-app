package com.example.trivia_backend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.trivia_backend.dtos.TriviaAPIResponseDTO;
import com.example.trivia_backend.dtos.TriviaAPIResponseDTO.TriviaAPIQuestion;
import com.example.trivia_backend.exceptions.QuestionNotFoundException;
import com.example.trivia_backend.records.EvaluationResult;
import com.example.trivia_backend.records.TriviaQuestion;

@ExtendWith(MockitoExtension.class)
class QaServiceTests {

    @InjectMocks
    private QaService service;

    @Mock
    private TriviaAPIService triviaAPIService;

    @BeforeEach
    void beforeEach() {
        TriviaAPIQuestion triviaQuestionOne = new TriviaAPIQuestion("Kiwi? 1", "Swagbaas", List.of("dab"));
        TriviaAPIQuestion triviaQuestionTwo = new TriviaAPIQuestion("Kiwi? 2", "Swagbaas", List.of("dab"));
        TriviaAPIQuestion triviaQuestionThree = new TriviaAPIQuestion("Kiwi? 3", "Swagbaas", List.of("dab"));
        TriviaAPIQuestion triviaQuestionFour = new TriviaAPIQuestion("Kiwi? 4", "Swagbaas", List.of("dab"));
        TriviaAPIQuestion triviaQuestionFive = new TriviaAPIQuestion("Kiwi? 5", "Swagbaas", List.of("dab"));
        TriviaAPIQuestion triviaQuestionSix = new TriviaAPIQuestion("Kiwi? 6", "Swagbaas", List.of("dab"));
        List<TriviaAPIQuestion> triviaQuestions = new ArrayList<>(List.of(triviaQuestionOne, triviaQuestionTwo,
                triviaQuestionThree, triviaQuestionFour, triviaQuestionFive, triviaQuestionSix));
        TriviaAPIResponseDTO triviaAPIResponseDTO = new TriviaAPIResponseDTO(triviaQuestions);
        when(triviaAPIService.getQuestions()).thenReturn(triviaAPIResponseDTO);
    }

    @AfterEach
    void AfterEach() {
        service.clearQuestions();
    }

    @Test
    @DisplayName("It should combine the correct and the incorrect answers in a possible answers list")
    void shouldCombineCorrectAndIncorrectAnswers() {
        TriviaQuestion qa = getFirstQuestion();

        assertThat(qa.possibleAnswers()).contains("Swagbaas");
        assertThat(qa.possibleAnswers()).contains("dab");
    }

    @Test
    @DisplayName("If an object with a specific question value exists it should not readd the question")
    void shouldNotAddDuplicateQuestions() {
        assertThat(service.getTriviaQuestions(50)).hasSize(6);
        UUID id = UUID.randomUUID();
        String question = "Kiwi? 1";
        String correctAnswer = "Yes";
        List<String> possibleAnswers = new ArrayList<String>();
        possibleAnswers.add("false");
        possibleAnswers.add(correctAnswer);

        TriviaQuestion qa = new TriviaQuestion(id, question, correctAnswer, false, possibleAnswers);
        List<TriviaQuestion> result = service.addTriviaQuestionToPool(qa);

        assertThat(result).hasSize(6);
    }

    @Test
    @DisplayName("The service should be able to evaluate answers")
    void shouldEvaluateAnswer() {
        TriviaQuestion qa = getFirstQuestion();

        EvaluationResult result = service.evaluateAnswer(qa.id().toString(), "Swagbaas");

        assertThat(result.isCorrect()).isTrue();
        assertThat(result.correctAnswer()).isEqualTo(qa.correctAnswer());
    }

    @Test
    @DisplayName("The service should delete the question from the list after the answer for that question is evaluated")
    void shouldDeleteQuestionAfterEvaluatingAnswer() {
        TriviaQuestion qa = getFirstQuestion();

        service.evaluateAnswer(qa.id().toString(), "Yes");

        assertThat(service.getTriviaQuestions(50).size()).isEqualTo(5);
    }

    @Test
    @DisplayName("It should throw a RuntimeException if the question for which the answer is given does not exist")
    void shouldReturnRuntimeExceptionWhenEvaluatingAnswerOnNonExistingQuestion() {
        TriviaQuestion qa = getFirstQuestion();

        service.evaluateAnswer(qa.id().toString(), "dab");
        // Should be deleted now

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.evaluateAnswer(qa.id().toString(), "dab");
        });

        assertThat(exception).isInstanceOf(QuestionNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo("Question not found.");
    }

    @Test
    @DisplayName("When pool has less than five unused trivia questions it should fetch a new batch of trivia api questions")
    void getTriviaQuestionsFetchesNewBatchWhenBelowFive() {
        List<TriviaQuestion> questions = service.getTriviaQuestions(5);
        assertThat(service.amountOfUnusedQuestions()).isEqualTo(1);

        service.evaluateAnswer(questions.get(0).id().toString(), "test");

        assertThat(service.amountOfUnusedQuestions()).isEqualTo(1);

        verify(triviaAPIService, times(1)).getQuestions();
        // Answered questions gets readded as new
        questions = service.getTriviaQuestions(2);
        verify(triviaAPIService, times(2)).getQuestions();

        assertThat(service.amountOfUnusedQuestions()).isEqualTo(0);
        service.evaluateAnswer(questions.get(0).id().toString(), "test");
        service.evaluateAnswer(questions.get(1).id().toString(), "test");

        service.getTriviaQuestions(1);
        assertThat(service.amountOfUnusedQuestions()).isEqualTo(1);
    }

    @Test
    @DisplayName("Get trivia questions should return the request amount")
    void getTriviaQuestionsShouldReturnRequestedAmount() {
        assertThat(service.getTriviaQuestions(2)).hasSize(2);
    }

    @Test
    @DisplayName("Get trivia questions should return the max amount if requested amount is more than available in memory")
    void getTriviaQuestionsShouldReturnAllAvailableInMemoryIfRequestedAmountIsMoreThanStored() {
        assertThat(service.getTriviaQuestions(60)).hasSize(6);
    }

    @Test
    @DisplayName("Handles exception during fetching from Trivia API service")
    void fetchQuestionsShouldHandleException() {
        when(triviaAPIService.getQuestions()).thenThrow(new RuntimeException());
        List<TriviaQuestion> triviaQuestions = service.getTriviaQuestions(5);

        assertThat(triviaQuestions).hasSize(0);
    }

    @Test
    @DisplayName("Handles non existing question when evaluating answer")
    void evaluateAnswerShouldHandleNonExistingQuestion() {
        TriviaQuestion question = getFirstQuestion();
        service.evaluateAnswer(question.id().toString(), null);

        assertDoesNotThrow(() -> {
            service.evaluateAnswer(question.id().toString(), null);
        });
    }

    @Test
    @DisplayName("It should parse boolean strings true and false to human readable Yes and No")
    void shouldParseBooleanValuesToHumanReadable() {
        TriviaAPIQuestion mockQuestion = new TriviaAPIQuestion(
                "kiwi?",
                "true",
                List.of("false"));
        TriviaAPIResponseDTO booleanResponse = new TriviaAPIResponseDTO(List.of(mockQuestion));

        service.clearQuestions();
        when(triviaAPIService.getQuestions()).thenReturn(booleanResponse);

        List<TriviaQuestion> questions = service.getTriviaQuestions(1);

        assertThat(questions).hasSize(1);
        TriviaQuestion parsedQuestion = questions.get(0);

        assertThat(parsedQuestion.correctAnswer()).isEqualTo("Yes");
        assertThat(parsedQuestion.possibleAnswers()).contains("No");
        assertThat(parsedQuestion.possibleAnswers()).doesNotContain("true");
        assertThat(parsedQuestion.possibleAnswers()).doesNotContain("false");
    }

    private TriviaQuestion getFirstQuestion() {
        return service.getTriviaQuestions(1).get(0);
    }
}
