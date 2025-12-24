package com.Technion.TEDTalkRAGAssistant.dtos;

public class StatsResponse {
    private int chunk_size;
    private double overlap_ratio;
    private int top_k;

    public StatsResponse() {
    }

    public StatsResponse(int chunk_size, double overlap_ratio, int top_k) {
        this.chunk_size = chunk_size;
        this.overlap_ratio = overlap_ratio;
        this.top_k = top_k;
    }

    public int getChunk_size() {
        return chunk_size;
    }

    public void setChunk_size(int chunk_size) {
        this.chunk_size = chunk_size;
    }

    public double getOverlap_ratio() {
        return overlap_ratio;
    }

    public void setOverlap_ratio(double overlap_ratio) {
        this.overlap_ratio = overlap_ratio;
    }

    public int getTop_k() {
        return top_k;
    }

    public void setTop_k(int top_k) {
        this.top_k = top_k;
    }
}