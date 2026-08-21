package com.example.trivia_backend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.trivia_backend.dtos.AnswerResult;
import com.example.trivia_backend.dtos.TriviaAPIResponseDTO;
import com.example.trivia_backend.dtos.TriviaAPIResponseDTO.TriviaQuestionDto;
import com.example.trivia_backend.exceptions.QuestionNotFoundException;
import com.example.trivia_backend.models.QuestionAndAnswer;

@ExtendWith(MockitoExtension.class)
class QaServiceTests {

    @InjectMocks
    private QaService service;

    @Mock
    private TriviaAPIService triviaAPIService;

    @BeforeEach
    void beforeEach() {
        service.clearQuestionAndAnswers();

        TriviaQuestionDto triviaQuestionDtoOne = new TriviaQuestionDto("Kiwi? 1", "Swagbaas", List.of("dab"));
        TriviaQuestionDto triviaQuestionDtoTwo = new TriviaQuestionDto("Kiwi? 2", "Swagbaas", List.of("dab"));
        TriviaQuestionDto triviaQuestionDtoThree = new TriviaQuestionDto("Kiwi? 3", "Swagbaas", List.of("dab"));
        TriviaQuestionDto triviaQuestionDtoFour = new TriviaQuestionDto("Kiwi? 4", "Swagbaas", List.of("dab"));
        TriviaQuestionDto triviaQuestionDtoFive = new TriviaQuestionDto("Kiwi? 5", "Swagbaas", List.of("dab"));
        TriviaQuestionDto triviaQuestionDtoSix = new TriviaQuestionDto("Kiwi? 6", "Swagbaas", List.of("dab"));
        List<TriviaQuestionDto> triviaQuestionDtos = new ArrayList<>(List.of(triviaQuestionDtoOne, triviaQuestionDtoTwo,
                triviaQuestionDtoThree, triviaQuestionDtoFour, triviaQuestionDtoFive, triviaQuestionDtoSix));
        TriviaAPIResponseDTO triviaAPIResponseDTO = new TriviaAPIResponseDTO(triviaQuestionDtos);
        when(triviaAPIService.getQuestions()).thenReturn(triviaAPIResponseDTO);

        service.fetchQuestions();
    }

    @Test
    @DisplayName("It should combine the correct and the incorrect answers in a possible answers list")
    void shouldCombineCorrectAndIncorrectAnswers() {
        QuestionAndAnswer questionAndAnswer = service.getFirstQuestion();

        assertThat(questionAndAnswer.possibleAnswers()).contains("Swagbaas");
        assertThat(questionAndAnswer.possibleAnswers()).contains("dab");
    }

    @Test
    @DisplayName("When fetch questions is called it should add it to the current list of questions")
    void fetchQuestionsContainsNewQuestion() {
        assertThat(service.getFirstQuestion().question()).isEqualTo("Kiwi? 1");

        TriviaQuestionDto triviaQuestionDto = new TriviaQuestionDto("Kiwi?", "Swagbaas", List.of("dab"));
        List<TriviaQuestionDto> triviaQuestionDtos = new ArrayList<>(List.of(triviaQuestionDto));
        TriviaAPIResponseDTO triviaAPIResponseDTO = new TriviaAPIResponseDTO(triviaQuestionDtos);
        when(triviaAPIService.getQuestions()).thenReturn(triviaAPIResponseDTO);

        service.clearQuestionAndAnswers();

        service.fetchQuestions();

        assertThat(service.getFirstQuestion().question()).isEqualTo("Kiwi?");
    }

    @Test
    @DisplayName("If an object with a specific question value exists it should not readd the question")
    void shouldNotAddDuplicateQuestions() {
        assertThat(service.getQuestionsAndAnswers()).hasSize(6);
        String uuidString = UUID.randomUUID().toString();
        String question = "Kiwi? 1";
        String correctAnswer = "Yes";
        List<String> possibleAnswers = new ArrayList<String>();
        possibleAnswers.add("No");
        possibleAnswers.add(correctAnswer);

        QuestionAndAnswer qa = new QuestionAndAnswer(uuidString, question, correctAnswer, possibleAnswers);
        List<QuestionAndAnswer> result = service.addQuestion(qa);

        assertThat(result).hasSize(6);
    }

    @Test
    @DisplayName("The service should be able to evaluate answers")
    void shouldEvaluateAnswer() {
        QuestionAndAnswer questionAndAnswer = service.getFirstQuestion();

        AnswerResult result = service.evaluateAnswer(questionAndAnswer.id(), "Swagbaas");

        assertThat(result.isCorrect()).isTrue();
        assertThat(result.correctAnswer()).isEqualTo(questionAndAnswer.correctAnswer());
    }

    @Test
    @DisplayName("The service should delete the question from the list after the answer for that question is evaluated")
    void shouldDeleteQuestionAfterEvaluatingAnswer() {
        QuestionAndAnswer questionAndAnswer = service.getFirstQuestion();

        service.evaluateAnswer(questionAndAnswer.id(), "Yes");

        assertThat(service.getQuestionsAndAnswers().size()).isEqualTo(5);
    }

    @Test
    @DisplayName("It should throw a RuntimeException if the question for which the answer is given does not exist")
    void shouldReturnRuntimeExceptionWhenEvaluatingAnswerOnNonExistingQuestion() {
        QuestionAndAnswer qa = service.getFirstQuestion();

        service.clearQuestionAndAnswers();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.evaluateAnswer(qa.id(), "dab");
        });

        assertThat(exception).isInstanceOf(QuestionNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo("Question not found.");
    }

    @Test
    @DisplayName("Get first question should return null if no questions have been loaded")
    void getFirstQuestionShouldReturnNullWhenListIsEmpty() {
        when(triviaAPIService.getQuestions()).thenReturn(new TriviaAPIResponseDTO(List.of()));
        service.clearQuestionAndAnswers();

        QuestionAndAnswer qa = service.getFirstQuestion();

        assertThat(qa).isNull();
    }

    @Test
    @DisplayName("When the list of questions has less than five questions it should fetch a new batch of questions")
    void getQuestionAndAnswersFetchesNewBatchWhenBelowFive() {
        assertThat(service.getQuestionsAndAnswers()).hasSize(6);

        service.evaluateAnswer(service.getFirstQuestion().id(), "test");

        assertThat(service.getQuestionsAndAnswers()).hasSize(5);

        service.evaluateAnswer(service.getFirstQuestion().id(), "test");

        // The Trivia API is mocked. The second time we add the same questions as the
        // first time.
        // Under the hood before adding it checks if the question is unique.
        // Since the 4 old questions still exist, this means it will add the two
        // answered questions.
        assertThat(service.getQuestionsAndAnswers()).hasSize(6);
    }
}
