package com.srijan.srijan_backend.generator;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ProjectGeneratorService {

    private static final String BASE_DIR = System.getProperty("java.io.tmpdir") + "/srijan/";

    public String generateZip(List<ParsedFile> files) {
        String sessionId = UUID.randomUUID().toString();
        Path projectDir = Paths.get(BASE_DIR + sessionId);

        try {
            writeFilesToDisk(files, projectDir);
            return createZip(projectDir, sessionId);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate project ZIP", e);
        }
    }

    private void writeFilesToDisk(List<ParsedFile> files, Path projectDir) throws IOException {
        for (ParsedFile file : files) {
            Path fullPath = projectDir.resolve(file.filePath());

            Files.createDirectories(fullPath.getParent());
            Files.writeString(fullPath, file.content());
        }
    }

    private String createZip(Path projectDir, String sessionId) throws IOException {
        Path zipPath = Paths.get(BASE_DIR + sessionId + ".zip");

        try (ZipOutputStream zipOut = new ZipOutputStream(
                new FileOutputStream(zipPath.toFile()))) {

            Files.walk(projectDir)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        String zipEntryName = projectDir.relativize(path).toString();
                        try {
                            zipOut.putNextEntry(new ZipEntry(zipEntryName));
                            Files.copy(path, zipOut);
                            zipOut.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException("Error zipping file: " + path, e);
                        }
                    });
        }

        return sessionId;
    }
}