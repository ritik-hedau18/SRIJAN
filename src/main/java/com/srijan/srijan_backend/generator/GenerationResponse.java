package com.srijan.srijan_backend.generator;

import java.util.List;

public record GenerationResponse(
        String sessionId,
        String downloadUrl,
        List<FileInfo> files
) {
    public record FileInfo(String filePath, String content) {}
}