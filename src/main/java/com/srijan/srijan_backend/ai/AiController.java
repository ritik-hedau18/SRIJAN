package com.srijan.srijan_backend.ai;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generate(@RequestBody GenerateRequest request) {
        String result = aiService.generate(request.prompt());
        return ResponseEntity.ok(result);
    }

    public record GenerateRequest(String prompt) {}
}