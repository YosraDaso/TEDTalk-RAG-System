package com.Technion.TEDTalkRAGAssistant.dtos;

import java.util.List;

public class PromptResponse {
    private String response;
    private List<ContextChunk> context;
    private AugmentedPrompt Augmented_prompt;

    public PromptResponse() {
    }

    public PromptResponse(String response, List<ContextChunk> context, AugmentedPrompt augmentedPrompt) {
        this.response = response;
        this.context = context;
        this.Augmented_prompt = augmentedPrompt;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<ContextChunk> getContext() {
        return context;
    }

    public void setContext(List<ContextChunk> context) {
        this.context = context;
    }

    public AugmentedPrompt getAugmented_prompt() {
        return Augmented_prompt;
    }

    public void setAugmented_prompt(AugmentedPrompt augmented_prompt) {
        this.Augmented_prompt = augmented_prompt;
    }
}