package com.javaboy.situs.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root = Paths.get("uploads");

    public String saveImage(MultipartFile file, String subFolder) {
        try {
            if (file == null || file.isEmpty()) return null;

            String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String name = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                           + "_" + UUID.randomUUID().toString().replace("-", "");
            String filename = (ext != null && !ext.isBlank()) ? name + "." + ext : name;

            Path dir = root.resolve(subFolder == null ? "situs" : subFolder);
            Files.createDirectories(dir);

            Path dest = dir.resolve(filename);
            Files.copy(file.getInputStream(), dest);

            // KEMBALIKAN URL YANG BISA DIAKSES BROWSER
            String url = "/uploads/" + (subFolder == null ? "situs" : subFolder) + "/" + filename;
            // normalize leading slash
            if (!url.startsWith("/")) url = "/" + url;
            return url;
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan file", e);
        }
    }
}
