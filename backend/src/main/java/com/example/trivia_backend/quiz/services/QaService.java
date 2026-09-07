package com.example.trivia_backend.quiz.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.trivia_backend.opentDB.dtos.TriviaAPIResponseDTO;
import com.example.trivia_backend.opentDB.records.TriviaAPIQuestion;
import com.example.trivia_backend.opentDB.services.TriviaAPIService;
import com.example.trivia_backend.quiz.records.EvaluationResult;
import com.example.trivia_backend.quiz.records.TriviaQuestion;
import com.example.trivia_backend.quiz.repositories.QaRepository;

@Service
public class QaService {
    private static final Logger log = LoggerFactory.getLogger(QaService.class);

    private static final int MIN_AMOUNT_TO_FETCH_THRESHOLD = 5;

    private final TriviaAPIService triviaAPIService;

    private final QaRepository qaRepository;

    public QaService(QaRepository qaRepository, TriviaAPIService triviaAPIService) {
        this.triviaAPIService = triviaAPIService;
        this.qaRepository = qaRepository;
    }

    private void validateGivenAnswer(String givenAnswer) {
        if (givenAnswer == null || givenAnswer.isBlank()) {
            throw new IllegalArgumentException("Given answer should have a value.");
        }
    }

    public EvaluationResult evaluateAnswer(String id, String givenAnswer) {
        validateGivenAnswer(givenAnswer);

        TriviaQuestion removedQuestion = qaRepository.fetchAndRemoveAnsweredQuestion(id);

        return new EvaluationResult(removedQuestion.id(), removedQuestion.correctAnswer(),
                removedQuestion.isCorrect(givenAnswer));
    }

    public List<TriviaQuestion> getTriviaQuestions(int amount) {
        // Possible race condition here
        // Since users will probably not hit 100k soon it is accepted
        // Could be solved with setting a boolean flag and waiting for fetch to finish
        if (qaRepository.countUnusedquestions() < MIN_AMOUNT_TO_FETCH_THRESHOLD) {
            fetchTriviaAPIQuestions();
        }

        List<TriviaQuestion> selectedQuestions = qaRepository.getTriviaQuestions(amount);

        sanityCheck(selectedQuestions);

        selectedQuestions.stream().forEach(q -> qaRepository.setToPresented(q));

        return selectedQuestions;
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
        qaRepository.addBatchedQuestions(
                responseDTO.results().stream()
                        .map(TriviaAPIQuestion::toTriviaQuestion).toList());
    }

    private void sanityCheck(List<TriviaQuestion> selectedQuestions) {
        if (selectedQuestions.isEmpty()) {
            log.warn("Output of getQuestions is empty. Usable questions: {}. Total questions in pool: {}.",
                    qaRepository.countUnusedquestions(), qaRepository.countTotalAmountOfQuestions());
        }
    }
}
