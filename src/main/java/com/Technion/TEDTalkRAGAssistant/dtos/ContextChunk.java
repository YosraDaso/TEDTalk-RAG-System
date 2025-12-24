package com.Technion.TEDTalkRAGAssistant.dtos;

public class ContextChunk {
    private String talk_id;
    private String title;
    private String chunk;
    private double score;

    public ContextChunk() {
    }

    public ContextChunk(String talk_id, String title, String chunk, double score) {
        this.talk_id = talk_id;
        this.title = title;
        this.chunk = chunk;
        this.score = score;
    }

    public String getTalk_id() {
        return talk_id;
    }

    public void setTalk_id(String talk_id) {
        this.talk_id = talk_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChunk() {
        return chunk;
    }

    public void setChunk(String chunk) {
        this.chunk = chunk;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}