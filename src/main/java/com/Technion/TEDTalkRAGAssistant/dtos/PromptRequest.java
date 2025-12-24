package com.Technion.TEDTalkRAGAssistant.dtos;

public class PromptRequest {
    private String question;

    public PromptRequest() {
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}