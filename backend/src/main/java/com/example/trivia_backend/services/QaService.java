package com.example.trivia_backend.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.trivia_backend.models.QuestionAndAnswer;

// Responsible for tracking Quiz
@Service
public class QaService {
    private HashSet<QuestionAndAnswer> questionsAndAnswers;

    public QaService() {
        questionsAndAnswers = new HashSet<>();
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

    public HashSet<QuestionAndAnswer> getQuestionsAndAnswers() {
        return questionsAndAnswers;
    }

    public HashSet<QuestionAndAnswer> addQuestionAndAnswer(QuestionAndAnswer qa) {
        if (isExistingQuestion(qa.getQuestion())) {
            return this.questionsAndAnswers;
        }

        HashSet<QuestionAndAnswer> updatedQuestionAndAnswers = new HashSet<>(this.questionsAndAnswers);

        updatedQuestionAndAnswers.add(qa);

        this.questionsAndAnswers = updatedQuestionAndAnswers;

        return this.questionsAndAnswers;
    }

    public void clearQuestionAndAnswers() {
        this.questionsAndAnswers.clear();
    }

    public QuestionAndAnswer getFirstQuestion() {
        List<QuestionAndAnswer> list = new ArrayList<>(this.getQuestionsAndAnswers());

        return list.get(0);
    }

    public boolean validateAnswer(String id, String givenAnswer) {
        QuestionAndAnswer questionAndAnswer = this.findById(id);

        if (questionAndAnswer == null) {
            return false;
        }

        return questionAndAnswer.isCorrectAnswer(givenAnswer);
    }
}
