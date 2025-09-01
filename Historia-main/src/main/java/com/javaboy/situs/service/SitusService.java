package com.javaboy.situs.service;

import com.javaboy.situs.model.Situs;
import com.javaboy.situs.repository.SitusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class SitusService {

    @Autowired
    private SitusRepository situsRepository;

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/images/";

    public List<Situs> getAllSitus() {
        return situsRepository.findAll();
    }

    public Optional<Situs> getSitusById(Integer id) {
        return situsRepository.findById(id);
    }

    public void deleteSitus(Integer id) {
        situsRepository.deleteById(id);
    }
    
    public Situs saveSitusWithImage(Situs situs, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            String filePath = storeFile(imageFile);
            situs.setFotoUrl(filePath);
        }
        return situsRepository.save(situs);
    }

    public List<Situs> getLatestSitus() {
        return situsRepository.findTop6ByOrderByCreatedAtDesc();
    }
    
    public List<Situs> searchSitus(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return situsRepository.findByNamaSitusContainingIgnoreCase(keyword);
        }
        return situsRepository.findAll();
    }

    // METHOD BARU: Untuk logika filter
    public List<Situs> findSitusByFilter(Integer provinsiId, Integer kabupatenId, Integer kecamatanId) {
        return situsRepository.findByLokasi(provinsiId, kabupatenId, kecamatanId);
    }

    // ... (method storeFile tetap sama) ...
    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) { return null; }
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) { Files.createDirectories(uploadPath); }
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            return "/uploads/images/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}