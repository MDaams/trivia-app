package com.example.trivia_backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Service;

import com.example.trivia_backend.models.QuestionAndAnswer;

@Service
public class QaService {
    private final AtomicReference<List<QuestionAndAnswer>> questionsAndAnswers;

    private void setQuestionAndAnswers(List<QuestionAndAnswer> updatedQuestionAndAnswers) {
        this.questionsAndAnswers.set(updatedQuestionAndAnswers);
    }

    public QaService() {
        questionsAndAnswers = new AtomicReference<>(List.of());
    }

    public List<QuestionAndAnswer> getQuestionsAndAnswers() {
        return questionsAndAnswers.get();
    }

    public void clearQuestionAndAnswers() {
        this.questionsAndAnswers.set(List.of());
    }

    private QuestionAndAnswer findById(String id) {
        for (QuestionAndAnswer qa : this.getQuestionsAndAnswers()) {
            if (qa.id().equals(id)) {
                return qa;
            }
        }
        return null;
    }

    private int findIndexByQuestion(String question, List<QuestionAndAnswer> questionAndAnswers) {
        for (int i = 0; i < questionAndAnswers.size(); i++) {
            QuestionAndAnswer qa = questionAndAnswers.get(i);
            if (qa.question().equals(question)) {
                return i;
            }
        }
        return -1;
    }

    public List<QuestionAndAnswer> deleteQuestionAndAnswers(QuestionAndAnswer qa) {
        return this.questionsAndAnswers.updateAndGet(currentList -> {
            List<QuestionAndAnswer> updatedQuestionAndAnswers = new ArrayList<>(currentList);
            int index = findIndexByQuestion(qa.question(), updatedQuestionAndAnswers);

            updatedQuestionAndAnswers.remove(index);

            return updatedQuestionAndAnswers;
        });
    }

    public QuestionAndAnswer getFirstQuestion() {
        List<QuestionAndAnswer> questionAndAnswers = new ArrayList<>(this.getQuestionsAndAnswers());

        if (questionAndAnswers.isEmpty()) {
            return null;
        }

        return questionAndAnswers.get(0);
    }

    public boolean evaluateAnswer(String id, String givenAnswer) {
        QuestionAndAnswer questionAndAnswer = this.findById(id);

        if (questionAndAnswer == null) {
            return false;
        }

        deleteQuestionAndAnswers(questionAndAnswer);

        return questionAndAnswer.isCorrect(givenAnswer);
    }

    public List<QuestionAndAnswer> addQuestion(QuestionAndAnswer qa) {
        return this.questionsAndAnswers.updateAndGet(currentList -> {
            List<QuestionAndAnswer> updatedQuestionAndAnswers = new ArrayList<>(currentList);

            if (findIndexByQuestion(qa.question(), updatedQuestionAndAnswers) > -1) {
                return updatedQuestionAndAnswers;
            }

            updatedQuestionAndAnswers.add(qa);

            return updatedQuestionAndAnswers;
        });
    }
}
