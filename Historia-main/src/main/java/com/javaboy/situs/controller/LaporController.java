package com.javaboy.situs.controller;

import com.javaboy.situs.model.Laporan;
import com.javaboy.situs.model.Kabupaten;
import com.javaboy.situs.model.Kecamatan;
import com.javaboy.situs.model.Provinsi;
import com.javaboy.situs.repository.LaporanRepository;
import com.javaboy.situs.repository.KabupatenRepository;
import com.javaboy.situs.repository.KecamatanRepository;
import com.javaboy.situs.repository.ProvinsiRepository;
import com.javaboy.situs.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class LaporController {

    private final LaporanRepository laporanRepo;
    private final ProvinsiRepository provRepo;
    private final KabupatenRepository kabRepo;
    private final KecamatanRepository kecRepo;
    private final FileStorageService storage;

    @GetMapping("/lapor")
    public String form(Model model) {
        model.addAttribute("provinsiList", provRepo.findAllByOrderByNamaProvinsiAsc());
        return "lapor/form";
    }

    @PostMapping("/lapor")
    public String submit(
            @RequestParam String namaSitus,
            @RequestParam(required = false) Integer provinsiId,
            @RequestParam(required = false) Integer kabupatenId,
            @RequestParam(required = false) Integer kecamatanId,
            @RequestParam String deskripsiSingkat,
            @RequestParam(required = false) String tahunDitemukan,
            @RequestParam(required = false) String kondisi,
            @RequestParam String namaPelapor,
            @RequestParam String emailPelapor,
            @RequestParam(required = false) MultipartFile fotoFile,
            @RequestParam(required = false) String fotoUrl,
            RedirectAttributes ra
    ) {
        Laporan lap = new Laporan();
        lap.setNamaSitus(namaSitus);

        String desc = deskripsiSingkat;
        if (kondisi != null && !kondisi.isBlank()) {
            desc = desc + "\n\n[Kondisi Saat Ini]\n" + kondisi;
        }
        lap.setDeskripsiSingkat(desc);

        lap.setTahunDitemukan(tahunDitemukan);
        lap.setNamaPelapor(namaPelapor);
        lap.setEmailPelapor(emailPelapor);

        // Bangun teks lokasi dari master data
        String lokasiText = "-";
        if (provinsiId != null) {
            String p = provRepo.findById(provinsiId).map(Provinsi::getNamaProvinsi).orElse("-");
            String k = (kabupatenId != null) ? kabRepo.findById(kabupatenId).map(Kabupaten::getNamaKabupaten).orElse("-") : "-";
            String c = (kecamatanId != null) ? kecRepo.findById(kecamatanId).map(Kecamatan::getNamaKecamatan).orElse("-") : "-";
            lokasiText = c + ", " + k + ", " + p;
        }
        lap.setLokasiText(lokasiText);

        // Foto: pakai file jika ada; jika tidak ada file, pakai URL
        if (fotoFile != null && !fotoFile.isEmpty()) {
            String saved = storage.saveImage(fotoFile, "laporan");
            lap.setFotoUrl(saved);
        } else {
            lap.setFotoUrl((fotoUrl != null && !fotoUrl.isBlank()) ? fotoUrl : null);
        }

        lap.setStatusLaporan("PENDING");
        lap.setReportedAt(LocalDateTime.now());

        laporanRepo.save(lap);
        ra.addFlashAttribute("pesanSukses", "Laporan terkirim. Menunggu validasi admin. Terima kasih!");

        return "redirect:/lapor";
    }
}
