package com.javaboy.situs.controller;

import com.javaboy.situs.model.Situs;
import com.javaboy.situs.repository.ProvinsiRepository;
import com.javaboy.situs.service.SitusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/situs")
public class SitusWebController {

    @Autowired
    private SitusService situsService;

    @Autowired
    private ProvinsiRepository provinsiRepository;

    // DAFTAR SITUS (list, pencarian, filter)
    @GetMapping
    public String listSitus(Model model,
                            @RequestParam(value = "keyword", required = false) String keyword,
                            @RequestParam(value = "provinsiId", required = false) Integer provinsiId,
                            @RequestParam(value = "kabupatenId", required = false) Integer kabupatenId,
                            @RequestParam(value = "kecamatanId", required = false) Integer kecamatanId) {

        List<Situs> situsList;
        if (keyword != null && !keyword.isBlank()) {
            situsList = situsService.searchSitus(keyword);
        } else if (provinsiId != null || kabupatenId != null || kecamatanId != null) {
            situsList = situsService.findSitusByFilter(provinsiId, kabupatenId, kecamatanId);
        } else {
            situsList = situsService.getAllSitus();
        }

        model.addAttribute("situsList", situsList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("provinsiList", provinsiRepository.findAllByOrderByNamaProvinsiAsc());
        return "situs/list_public";
    }

    // FORM ADD (bila dipakai di sisi publik)
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("situs", new Situs());
        return "situs/form";
    }

    @PostMapping("/save")
    public String saveSitus(@ModelAttribute("situs") Situs situs,
                            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                            RedirectAttributes redirectAttributes) {
        try {
            situsService.saveSitusWithImage(situs, imageFile);
            redirectAttributes.addFlashAttribute("successMessage", "Situs berhasil disimpan!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal menyimpan situs.");
            e.printStackTrace();
        }
        return "redirect:/situs";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Situs> situs = situsService.getSitusById(id);
        if (situs.isPresent()) {
            model.addAttribute("situs", situs.get());
            return "situs/form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Situs dengan ID " + id + " tidak ditemukan.");
            return "redirect:/situs";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteSitus(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            situsService.deleteSitus(id);
            redirectAttributes.addFlashAttribute("successMessage", "Situs berhasil dihapus!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal menghapus situs.");
        }
        return "redirect:/situs";
    }

    @GetMapping("/detail/{id}")
    public String viewSitusDetail(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Situs> situsOptional = situsService.getSitusById(id);
        if (situsOptional.isPresent()) {
            Situs s = situsOptional.get();
            model.addAttribute("situs", s);
            model.addAttribute("mapsLink", buildMapsLink(s));                 // link Google Maps
            model.addAttribute("tanggalDitemukanDisplay", buildTanggalDisplay(s)); // dd-MM-yyyy
            return "situs/detail";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Situs dengan ID " + id + " tidak ditemukan.");
            return "redirect:/";
        }
    }

    /** Bangun link Google Maps dari mapsUrl, koordinat, atau alamat administratif */
    private String buildMapsLink(Situs s) {
        if (s.getMapsUrl() != null && !s.getMapsUrl().isBlank()) {
            return s.getMapsUrl();
        }
        if (s.getLat() != null && s.getLon() != null) {
            return "https://www.google.com/maps?q=" + s.getLat() + "," + s.getLon();
        }
        String kec = s.getKecamatan() != null ? s.getKecamatan().getNamaKecamatan() : "";
        String kab = s.getKabupaten() != null ? s.getKabupaten().getNamaKabupaten() : "";
        String prov = s.getProvinsi()  != null ? s.getProvinsi().getNamaProvinsi()  : "";
        String address = (kec + ", " + kab + ", " + prov).replaceAll("^,\\s*|\\s*,\\s*$", "");
        if (address.isBlank()) return null;
        return "https://www.google.com/maps/search/?api=1&query=" +
                URLEncoder.encode(address, StandardCharsets.UTF_8);
    }

    private String buildTanggalDisplay(Situs s) {
        // 1) Coba cari LocalDate getTglDitemukan()
        try {
            var m = s.getClass().getMethod("getTglDitemukan");
            Object val = m.invoke(s);
            if (val instanceof LocalDate ld) {
                return ld.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            }
        } catch (NoSuchMethodException ignore) {
            // entity belum punya field tglDitemukan -> lanjut fallback
        } catch (Exception ignore) { }

        // 2) Fallback: parse String yyyy-MM-dd dari tahunDitemukan
        String t = s.getTahunDitemukan();
        if (t == null || t.isBlank()) return null;

        try {
            LocalDate ld = LocalDate.parse(t, DateTimeFormatter.ISO_LOCAL_DATE);
            return ld.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (Exception ignore) { }

        return t;
    }
}
