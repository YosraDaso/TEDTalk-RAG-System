package com.Technion.TEDTalkRAGAssistant.controller;

import com.Technion.TEDTalkRAGAssistant.dtos.PromptRequest;
import com.Technion.TEDTalkRAGAssistant.dtos.PromptResponse;
import com.Technion.TEDTalkRAGAssistant.dtos.StatsResponse;
import com.Technion.TEDTalkRAGAssistant.services.ChatWithVectorStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ChatController {

    @Autowired
    private ChatWithVectorStoreService chatWithVectorStoreService;

    /**
     * POST /api/prompt
     * Main endpoint for querying the RAG system
     */
    @PostMapping("/prompt")
    public ResponseEntity<PromptResponse> prompt(@RequestBody PromptRequest request) {
        System.out.println("Received question: " + request.getQuestion());

        if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            PromptResponse response = chatWithVectorStoreService.processRAGQuery(request.getQuestion());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error processing prompt: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/stats
     * Returns current RAG system configuration
     */
    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> stats() {
        try {
            StatsResponse stats = chatWithVectorStoreService.getStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("Error getting stats: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}