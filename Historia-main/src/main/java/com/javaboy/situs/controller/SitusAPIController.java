package com.javaboy.situs.controller;

import com.javaboy.situs.model.Situs;
import com.javaboy.situs.service.SitusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/situs")
public class SitusAPIController {

    @Autowired
    private SitusService situsService;

    // Mendapatkan semua situs
    @GetMapping
    public List<Situs> getAllSitus() {
        return situsService.getAllSitus();
    }

    // Mendapatkan situs berdasarkan ID
    @GetMapping("/{id}")
    public ResponseEntity<Situs> getSitusById(@PathVariable Integer id) { // PERUBAHAN: Long -> Integer
        Optional<Situs> situs = situsService.getSitusById(id);
        return situs.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Membuat situs baru dengan gambar
    @PostMapping
    public ResponseEntity<Situs> createSitus(@RequestParam("namaSitus") String namaSitus,
                                             @RequestParam("deskripsi") String deskripsi,
                                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        Situs situs = new Situs();
        situs.setNamaSitus(namaSitus); 
        situs.setDeskripsi(deskripsi); 
        
        Situs savedSitus = situsService.saveSitusWithImage(situs, imageFile);
        return new ResponseEntity<>(savedSitus, HttpStatus.CREATED);
    }

    // Mengupdate situs
    @PutMapping("/{id}")
    public ResponseEntity<Situs> updateSitus(@PathVariable Integer id, // PERUBAHAN: Long -> Integer
                                             @RequestParam("namaSitus") String namaSitus,
                                             @RequestParam("deskripsi") String deskripsi,
                                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        
        Optional<Situs> situsData = situsService.getSitusById(id);

        if (situsData.isPresent()) {
            Situs situsDetails = situsData.get();
            situsDetails.setNamaSitus(namaSitus); 
            situsDetails.setDeskripsi(deskripsi); 

            Situs updatedSitus = situsService.saveSitusWithImage(situsDetails, imageFile);
            return new ResponseEntity<>(updatedSitus, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Menghapus situs
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteSitus(@PathVariable Integer id) { 
        try {
            situsService.deleteSitus(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}