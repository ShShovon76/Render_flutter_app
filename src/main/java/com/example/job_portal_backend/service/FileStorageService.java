package com.example.job_portal_backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public String storeFile(MultipartFile file, String subDirectory) throws IOException {
        Path uploadPath = Paths.get(uploadDir, subDirectory).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        String originalFilename = file.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = UUID.randomUUID() + extension;

        Path target = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        String relativePath = subDirectory + "/" + filename;

        log.info("Stored file at {}", relativePath);
        return relativePath;
    }

    // ===== Specialized helpers =====

    public String storeResume(MultipartFile file) throws IOException {
        return storeFile(file, "resumes");
    }

    public String storeProfilePicture(MultipartFile file) throws IOException {
        return storeFile(file, "profile-pictures");
    }

    public String storeCompanyLogo(MultipartFile file) throws IOException {
        return storeFile(file, "company-logos");
    }

    public String storeCompanyCover(MultipartFile file) throws IOException {
        return storeFile(file, "company-covers");
    }

    // ===== Load file =====
    public byte[] loadFile(String filePath) throws IOException {

        if (filePath == null || filePath.isBlank()) {
            throw new FileNotFoundException("Empty file path");
        }

        Path resolvedPath;

        Path raw = Paths.get(filePath);

        if (raw.isAbsolute()) {
            resolvedPath = raw;
        } else {
            resolvedPath = Paths.get(uploadDir).resolve(filePath).normalize();
        }

        if (!Files.exists(resolvedPath)) {
            throw new FileNotFoundException("File not found: " + resolvedPath);
        }

        return Files.readAllBytes(resolvedPath);
    }



    // ===== DELETE BY STORED PATH =====
    public void deleteFile(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) return;

        try {
            Path path = Paths.get(uploadDir).resolve(storedPath).normalize();
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("Failed to delete file: {}", storedPath, e);
        }
    }

    // ===== Convert stored path to public URL =====
    public String getFileUrl(String storedPath) {
        return storedPath;
    }

}
