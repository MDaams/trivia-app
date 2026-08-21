package com.example.trivia_backend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
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

        TriviaQuestionDto triviaQuestionDto = new TriviaQuestionDto("Kiwi?", "Swagbaas", List.of("dab"));
        List<TriviaQuestionDto> triviaQuestionDtos = new ArrayList<>(List.of(triviaQuestionDto));
        TriviaAPIResponseDTO triviaAPIResponseDTO = new TriviaAPIResponseDTO(triviaQuestionDtos);
        when(triviaAPIService.getQuestions()).thenReturn(triviaAPIResponseDTO);

        service.fetchQuestions();
    }

    @Test
    void shouldCombineCorrectAndIncorrectAnswers() {
        QuestionAndAnswer questionAndAnswer = service.getFirstQuestion();

        assertThat(questionAndAnswer.possibleAnswers()).contains("Swagbaas");
        assertThat(questionAndAnswer.possibleAnswers()).contains("dab");
    }

    @Test
    void fetchQuestionsClearsList() {
        service.addQuestion(new QuestionAndAnswer("string", "swagbaas?", "dab", List.of("dab", "Kiwi")));
        service.addQuestion(new QuestionAndAnswer("string", "dab?", "swagbaas", List.of("swagbaas", "Kiwi")));

        service.fetchQuestions();

        assertThat(service.getQuestionsAndAnswers()).hasSize(1);
    }

    @Test
    void fetchQuestionsClearsContainsNewQuestion() {
        service.clearQuestionAndAnswers();

        service.addQuestion(new QuestionAndAnswer("string", "swagbaas?", "dab", List.of("dab", "Kiwi")));

        assertThat(service.getFirstQuestion().question()).isEqualTo("swagbaas?");

        service.fetchQuestions();

        assertThat(service.getFirstQuestion().question()).isEqualTo("Kiwi?");
    }

    @Test
    void shouldAddDuplicateQuestions() {
        String uuidString = UUID.randomUUID().toString();
        String question = "Kiwi?";
        String correctAnswer = "Yes";
        List<String> possibleAnswers = new ArrayList<String>();
        possibleAnswers.add("No");
        possibleAnswers.add(correctAnswer);

        QuestionAndAnswer qa = new QuestionAndAnswer(uuidString, question, correctAnswer, possibleAnswers);
        List<QuestionAndAnswer> result = service.addQuestion(qa);

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldEvaluateAnswer() {
        QuestionAndAnswer questionAndAnswer = service.getFirstQuestion();

        AnswerResult result = service.evaluateAnswer(questionAndAnswer.id(), "Swagbaas");

        assertThat(result.isCorrect()).isTrue();
        assertThat(result.correctAnswer()).isEqualTo(questionAndAnswer.correctAnswer());
    }

    @Test
    void shouldDeleteQuestionAfterEvaluatingAnswer() {
        QuestionAndAnswer questionAndAnswer = service.getFirstQuestion();

        service.evaluateAnswer(questionAndAnswer.id(), "Yes");

        assertThat(service.getQuestionsAndAnswers().size()).isEqualTo(0);
    }

    @Test
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
    void getFirstQuestionShouldReturnNullWhenListIsEmpty() {
        service.clearQuestionAndAnswers();

        QuestionAndAnswer qa = service.getFirstQuestion();

        assertThat(qa).isNull();
    }
}
