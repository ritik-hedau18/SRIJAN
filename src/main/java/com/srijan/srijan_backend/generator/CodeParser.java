package com.srijan.srijan_backend.generator;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CodeParser {

    private static final Pattern FILE_PATTERN =
            Pattern.compile("=== FILE: (.+?) ===\\s*\\n([\\s\\S]*?)(?==== FILE:|$)");

    public List<ParsedFile> parse(String aiResponse) {
        List<ParsedFile> files = new ArrayList<>();
        Matcher matcher = FILE_PATTERN.matcher(aiResponse);

        while (matcher.find()) {
            String filePath = matcher.group(1).trim();
            String content = matcher.group(2).trim();

            if (!filePath.isBlank() && !content.isBlank()) {
                files.add(new ParsedFile(filePath, content));
            }
        }

        return files;
    }
}