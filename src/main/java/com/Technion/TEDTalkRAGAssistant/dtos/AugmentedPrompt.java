package com.Technion.TEDTalkRAGAssistant.dtos;

public class AugmentedPrompt {
    private String System;
    private String User;

    public AugmentedPrompt() {
    }

    public AugmentedPrompt(String system, String user) {
        this.System = system;
        this.User = user;
    }

    public String getSystem() {
        return System;
    }

    public void setSystem(String system) {
        this.System = system;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        this.User = user;
    }
}