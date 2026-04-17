package com.srijan.srijan_backend.ai;

import com.srijan.srijan_backend.generator.*;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiService {

    private final ChatClient chatClient;
    private final CodeParser codeParser;
    private final ProjectGeneratorService projectGeneratorService;

    public AiService(ChatClient.Builder builder,
                     CodeParser codeParser,
                     ProjectGeneratorService projectGeneratorService) {
        this.chatClient = builder
                .defaultSystem(PromptTemplates.CODE_GENERATOR)
                .build();
        this.codeParser = codeParser;
        this.projectGeneratorService = projectGeneratorService;
    }

    public GenerationResponse generate(String userPrompt) {

        // Step 1: Get raw AI response
        String rawResponse = chatClient
                .prompt()
                .user(userPrompt)
                .call()
                .content();

        // Step 2: Parse into individual files
        List<ParsedFile> parsedFiles = codeParser.parse(rawResponse);

        // Step 3: Write to disk + create ZIP
        String sessionId = projectGeneratorService.generateZip(parsedFiles);

        // Step 4: Build response for frontend
        List<GenerationResponse.FileInfo> fileInfos = parsedFiles.stream()
                .map(f -> new GenerationResponse.FileInfo(f.filePath(), f.content()))
                .toList();

        String downloadUrl = "/api/ai/download/" + sessionId;

        return new GenerationResponse(sessionId, downloadUrl, fileInfos);
    }
}