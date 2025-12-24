package com.Technion.TEDTalkRAGAssistant.services;
import com.Technion.TEDTalkRAGAssistant.advisors.PromptPrinterAdvisor;
import org.apache.catalina.User;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.defaultAdvisors(PromptPrinterAdvisor.builder().build()).build();
    }

    public String sendMessage(String prompt) {
        // Logic to send a message
        // This could involve calling a service that handles the chat logic
        System.out.println("Sending prompt: " + prompt);

        String response = this.chatClient.prompt(prompt).call().content();

        System.out.println(response);

        return response;
    }

    public String sendEngineeredMessage(String prompt) {
        // Logic to send a message
        // This could involve calling a service that handles the chat logic
        System.out.println("Sending prompt: " + prompt);

        Message roleMessage = new SystemMessage("Answer as: You are a marketing specialist with experience of 20 year in selling software as a service. Please provide a detailed response.");
        Message promptMessage = new UserMessage(prompt);
        Prompt myPrompt = Prompt.builder()
                .messages(roleMessage, promptMessage)
                .build();




        System.out.println(myPrompt.toString());
        String response = this.chatClient.prompt(myPrompt).call().content();

        System.out.println(response);

        return response;
    }

    public String sendStructuredMessage(String prompt) {
        // Logic to send a message
        // This could involve calling a service that handles the chat logic
        System.out.println("Sending prompt: " + prompt);

        Message roleMessage = new SystemMessage("Answer as: You are a marketing specialist with experience of 20 year in selling software as a service. Please provide a detailed response.");
        Message promptMessage = new UserMessage(prompt);
        Message assistantMessage = new AssistantMessage("Format your response in a JSON format with the following keys: 'result_explanation', 'response' and 'metadata'.");
        Prompt myPrompt = Prompt.builder()
                .messages(roleMessage,assistantMessage, promptMessage)
                .build();

        System.out.println(myPrompt.toString());
        String response = this.chatClient.prompt(myPrompt).call().content();

        System.out.println(response);

        return response;
    }


    public ChatResponse sendMessageWithToken(String prompt) {
        // Logic to send a message
        // This could involve calling a service that handles the chat logic
        System.out.println("Sending prompt: " + prompt);
        ChatResponse chatResponse = this.chatClient.prompt(prompt).call().chatResponse();
        System.out.println("Output cost:"+chatResponse.getMetadata().getUsage().getCompletionTokens());
        System.out.println("Input cost:"+chatResponse.getMetadata().getUsage().getPromptTokens());
        System.out.println(chatResponse);
        return chatResponse;
    }

    public String sendPromptWithMedia(String prompt, MultipartFile mediaFile)
    {
        MimeType mediaType = MimeTypeUtils.parseMimeType(mediaFile.getContentType());
        UserMessage userMessage = UserMessage.builder()
                .text(prompt)
                .media(List.of(new Media(mediaType, mediaFile.getResource())))
                .build();
        Prompt chatPrompt = Prompt.builder().messages(List.of(userMessage)).build();
        String response = this.chatClient.prompt(chatPrompt).call().content();
        return response;
    }


    @Value("classpath:prompt-templates/example-template.st")
    private Resource promptFile;

    public String sendMessageWithTemplate() {

        PromptTemplate template = new SystemPromptTemplate(promptFile);

        Message systemMessage = template.createMessage();

        String response = this.chatClient
                .prompt(Prompt.builder().messages(systemMessage).build())
                .call()
                .content();

        System.out.println(response);

        return response;
    }








}