package com.example.trivia_backend.services;

import java.util.HashSet;

import org.springframework.stereotype.Service;

@Service
public class QaService {
    private HashSet<String> questionsAndAnswers;

    public QaService() {
        questionsAndAnswers = new HashSet<>();
    }

    public HashSet<String> getQuestionsAndAnswers() {
        return questionsAndAnswers;
    }

}
