package com.cpt202.booking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class AvatarStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("png", "jpg", "jpeg", "webp");
    private static final String PUBLIC_PREFIX = "/uploads/avatars/";

    private final Path avatarUploadDirectory;

    public AvatarStorageService(@Value("${app.avatar.upload-dir:uploads/avatars}") String avatarUploadDirectory) {
        this.avatarUploadDirectory = Paths.get(avatarUploadDirectory).toAbsolutePath().normalize();
    }

    public String storeAvatar(MultipartFile avatar, String username) {
        if (avatar == null || avatar.isEmpty()) {
            return null;
        }

        String extension = extractExtension(avatar.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Avatar must be a PNG, JPG, or WEBP image.");
        }

        try {
            Files.createDirectories(avatarUploadDirectory);
            String filename = sanitizeSegment(username) + "-" + UUID.randomUUID() + "." + extension;
            Path target = avatarUploadDirectory.resolve(filename);
            try (InputStream inputStream = avatar.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return PUBLIC_PREFIX + filename;
        } catch (IOException ex) {
            throw new IllegalStateException("Avatar upload failed. Please try again.");
        }
    }

    public void deleteAvatar(String avatarPath) {
        if (avatarPath == null || avatarPath.isBlank() || !avatarPath.startsWith(PUBLIC_PREFIX)) {
            return;
        }

        try {
            Files.deleteIfExists(avatarUploadDirectory.resolve(avatarPath.substring(PUBLIC_PREFIX.length())));
        } catch (IOException ex) {
            // Cleanup should not block profile updates or registration completion.
        }
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("Avatar file must include a valid extension.");
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.') + 1)
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private String sanitizeSegment(String source) {
        String normalized = source == null ? "avatar" : source.trim().toLowerCase(Locale.ROOT);
        String sanitized = normalized.replaceAll("[^a-z0-9]+", "-").replaceAll("^-+|-+$", "");
        return sanitized.isBlank() ? "avatar" : sanitized;
    }
}
