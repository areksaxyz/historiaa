package com.javaboy.situs.controller;

import com.javaboy.situs.model.Laporan;
import com.javaboy.situs.repository.LaporanRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final LaporanRepository laporanRepository;

    public AdminController(LaporanRepository laporanRepository) {
        this.laporanRepository = laporanRepository;
    }

    @GetMapping("/dashboard")
    public String redirectDashboard() { return "redirect:/admin/laporan"; }

    // daftar laporan pending
    @GetMapping("/laporan")
    public String showLaporanMasuk(Model model) {
        model.addAttribute("activeMenu", "laporan");
        model.addAttribute("pageTitle", "Laporan Masuk");
        model.addAttribute("laporanList", laporanRepository.findByStatusLaporan("PENDING"));
        return "admin/laporan_masuk";
    }

    // HALAMAN DETAIL LAPORAN
    @GetMapping("/laporan/{id}")
    public String detailLaporan(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Optional<Laporan> opt = laporanRepository.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("successMessage", "Laporan tidak ditemukan.");
            return "redirect:/admin/laporan";
        }
        model.addAttribute("activeMenu", "laporan");
        model.addAttribute("pageTitle", "Detail Laporan");
        model.addAttribute("laporan", opt.get());
        return "admin/laporan_detail";
    }

    // validasi laporan
    @PostMapping("/laporan/validasi/{id}")
    public String validasiLaporan(@PathVariable("id") Integer id, RedirectAttributes ra) {
        laporanRepository.findById(id).ifPresent(l -> {
            l.setStatusLaporan("VALIDATED");
            laporanRepository.save(l);
        });
        ra.addFlashAttribute("successMessage", "Laporan berhasil divalidasi.");
        return "redirect:/admin/laporan";
    }

    // tolak laporan
    @PostMapping("/laporan/tolak/{id}")
    public String tolakLaporan(@PathVariable("id") Integer id, RedirectAttributes ra) {
        laporanRepository.findById(id).ifPresent(l -> {
            l.setStatusLaporan("REJECTED");
            laporanRepository.save(l);
        });
        ra.addFlashAttribute("successMessage", "Laporan berhasil ditolak.");
        return "redirect:/admin/laporan";
    }
}
