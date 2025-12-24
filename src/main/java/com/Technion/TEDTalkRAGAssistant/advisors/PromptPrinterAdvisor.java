package com.Technion.TEDTalkRAGAssistant.advisors;

import lombok.Builder;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;

@Builder
public class PromptPrinterAdvisor implements CallAdvisor {
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {

        System.out.println("PromptPrinterAdvisor> prompt: " + chatClientRequest.prompt());
        ChatClientResponse response = callAdvisorChain.nextCall(chatClientRequest);
        System.out.println(response);
        return response;

    }

    @Override
    public String getName() {
        return "PromptPrinterAdvisor";
    }

    @Override
    public int getOrder() {
        return 1000;
    }
}