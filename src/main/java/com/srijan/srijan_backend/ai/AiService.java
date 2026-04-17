package com.srijan.srijan_backend.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private final ChatClient chatClient;

    public AiService(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem(PromptTemplates.CODE_GENERATOR)
                .build();
    }

    public String generate(String userPrompt) {
        return chatClient
                .prompt()
                .user(userPrompt)
                .call()
                .content();
    }
}