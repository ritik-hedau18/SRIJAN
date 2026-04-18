package com.srijan.srijan_backend.ai;

import com.srijan.srijan_backend.generator.GenerationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/generate")
    public ResponseEntity<GenerationResponse> generate(
            @RequestBody GenerateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        GenerationResponse response = aiService.generate(
                request.prompt(),
                request.sessionId(),
                userDetails.getUsername()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{sessionId}")
    public ResponseEntity<Resource> download(@PathVariable String sessionId) {
        String zipPath = System.getProperty("java.io.tmpdir")
                + "/srijan/" + sessionId + ".zip";

        File zipFile = new File(zipPath);

        if (!zipFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(zipFile);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"srijan-project.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    public record GenerateRequest(String prompt, String sessionId) {}
}