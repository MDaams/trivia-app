package com.example.trivia_backend.services;

import java.util.HashSet;

import org.springframework.stereotype.Service;

import com.example.trivia_backend.models.QuestionAndAnswer;

@Service
public class QaService {
    private HashSet<QuestionAndAnswer> questionsAndAnswers;

    public QaService() {
        questionsAndAnswers = new HashSet<>();
    }

    public HashSet<QuestionAndAnswer> getQuestionsAndAnswers() {
        return questionsAndAnswers;
    }

    public HashSet<QuestionAndAnswer> addQuestionAndAnswer(QuestionAndAnswer qa) {
        HashSet<QuestionAndAnswer> updatedQuestionAndAnswers = new HashSet<>(this.questionsAndAnswers);

        updatedQuestionAndAnswers.add(qa);

        this.questionsAndAnswers = updatedQuestionAndAnswers;

        return this.questionsAndAnswers;
    }

    public void clearQuestionAndAnswers() {
        this.questionsAndAnswers.clear();
    }

}
