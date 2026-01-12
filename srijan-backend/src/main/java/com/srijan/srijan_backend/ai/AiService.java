package com.srijan.srijan_backend.ai;

import com.srijan.srijan_backend.generator.*;
import com.srijan.srijan_backend.session.ChatSession;
import com.srijan.srijan_backend.session.ChatSessionRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AiService {

    private final ChatClient chatClient;
    private final CodeParser codeParser;
    private final ProjectGeneratorService projectGeneratorService;
    private final ChatSessionRepository chatSessionRepository;
    private final InMemoryChatMemory chatMemory;

    public AiService(ChatClient.Builder builder,
                     CodeParser codeParser,
                     ProjectGeneratorService projectGeneratorService,
                     ChatSessionRepository chatSessionRepository) {

        this.chatMemory = new InMemoryChatMemory();
        this.chatClient = builder
                .defaultSystem(PromptTemplates.CODE_GENERATOR)
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
        this.codeParser = codeParser;
        this.projectGeneratorService = projectGeneratorService;
        this.chatSessionRepository = chatSessionRepository;
    }

    public GenerationResponse generate(String userPrompt,
                                       String sessionId,
                                       String userEmail) {

        // Step 1: If no sessionId, create a new session
        String activeSessionId = resolveSession(sessionId, userEmail, userPrompt);

        // Step 2: Call AI with memory tied to this session
        String rawResponse = chatClient
                .prompt()
                .user(userPrompt)
                .advisors(advisor -> advisor
                        .param(MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY,
                                activeSessionId))
                .call()
                .content();

        // Step 3: Parse files from AI response
        List<ParsedFile> parsedFiles = codeParser.parse(rawResponse);

        // Step 4: Generate ZIP only if files were returned
        String zipSessionId = null;
        String downloadUrl = null;

        if (!parsedFiles.isEmpty()) {
            zipSessionId = projectGeneratorService.generateZip(parsedFiles);
            downloadUrl = "/api/ai/download/" + zipSessionId;
        }

        // Step 5: Build file info list for frontend
        List<GenerationResponse.FileInfo> fileInfos = parsedFiles.stream()
                .map(f -> new GenerationResponse.FileInfo(f.filePath(), f.content()))
                .toList();

        return new GenerationResponse(activeSessionId, downloadUrl, fileInfos);
    }

    private String resolveSession(String sessionId,
                                  String userEmail,
                                  String firstPrompt) {

        // If sessionId sent and exists in DB → reuse it
        if (sessionId != null && chatSessionRepository.existsBySessionId(sessionId)) {
            return sessionId;
        }

        // Otherwise create a new session
        String newSessionId = UUID.randomUUID().toString();
        String title = firstPrompt.length() > 50
                ? firstPrompt.substring(0, 50) + "..."
                : firstPrompt;

        ChatSession session = ChatSession.builder()
                .sessionId(newSessionId)
                .userEmail(userEmail)
                .title(title)
                .build();

        chatSessionRepository.save(session);
        return newSessionId;
    }
}