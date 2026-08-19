package com.example.trivia_backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.trivia_backend.models.QuestionAndAnswer;

@Service
public class QaService {
    private List<QuestionAndAnswer> questionsAndAnswers;

    public QaService() {
        questionsAndAnswers = new ArrayList<>();
    }

    public List<QuestionAndAnswer> getQuestionsAndAnswers() {
        return questionsAndAnswers;
    }

    private void setQuestionAndAnswers(List<QuestionAndAnswer> updatedQuestionAndAnswers) {
        this.questionsAndAnswers = updatedQuestionAndAnswers;
    }

    private QuestionAndAnswer findById(String id) {
        for (QuestionAndAnswer qa : questionsAndAnswers) {
            if (qa.getId().equals(id)) {
                return qa;
            }
        }
        return null;
    }

    private QuestionAndAnswer findByQuestion(String question) {
        for (QuestionAndAnswer qa : questionsAndAnswers) {
            if (qa.getQuestion().equals(question)) {
                return qa;
            }
        }
        return null;
    }

    private boolean isExistingQuestion(String question) {
        return findByQuestion(question) != null;
    }

    public List<QuestionAndAnswer> addQuestionAndAnswer(QuestionAndAnswer qa) {
        if (isExistingQuestion(qa.getQuestion())) {
            return this.getQuestionsAndAnswers();
        }

        List<QuestionAndAnswer> updatedQuestionAndAnswers = new ArrayList<>(this.getQuestionsAndAnswers());

        updatedQuestionAndAnswers.add(qa);

        this.setQuestionAndAnswers(updatedQuestionAndAnswers);

        return this.getQuestionsAndAnswers();
    }

    public void clearQuestionAndAnswers() {
        this.questionsAndAnswers = new ArrayList<>();
    }

    public QuestionAndAnswer getFirstQuestion() {
        List<QuestionAndAnswer> questionAndAnswers = new ArrayList<>(this.getQuestionsAndAnswers());

        if (questionAndAnswers.size() == 0) {
            return null;
        }

        return questionAndAnswers.get(0);
    }

    public boolean evaluateAnswer(String id, String givenAnswer) {
        QuestionAndAnswer questionAndAnswer = this.findById(id);

        if (questionAndAnswer == null) {
            return false;
        }

        return questionAndAnswer.isCorrectAnswer(givenAnswer);
    }
}
