package com.javaboy.situs.controller;

import com.javaboy.situs.dto.SitusFormDto;
import com.javaboy.situs.model.Situs;
import com.javaboy.situs.repository.*;
import com.javaboy.situs.service.FileStorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/situs")
public class AdminSitusController {

    private final SitusRepository situsRepo;
    private final ProvinsiRepository provRepo;
    private final KabupatenRepository kabRepo;
    private final KecamatanRepository kecRepo;
    private final FileStorageService storage;

    public AdminSitusController(SitusRepository situsRepo,
                                ProvinsiRepository provRepo,
                                KabupatenRepository kabRepo,
                                KecamatanRepository kecRepo,
                                FileStorageService storage) {
        this.situsRepo = situsRepo;
        this.provRepo = provRepo;
        this.kabRepo = kabRepo;
        this.kecRepo = kecRepo;
        this.storage = storage;
    }

    @GetMapping
    public String list(Model model) {
        List<Situs> list = situsRepo.findAll();
        model.addAttribute("activeMenu", "situs");
        model.addAttribute("pageTitle", "Kelola Situs");
        model.addAttribute("list", list);
        return "admin/situs_list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("activeMenu", "situs");
        model.addAttribute("pageTitle", "Tambah Situs");
        model.addAttribute("dto", new SitusFormDto());
        model.addAttribute("provinsiList", provRepo.findAllByOrderByNamaProvinsiAsc()); // kab/kec via API
        return "admin/situs_form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        Situs s = situsRepo.findById(id).orElseThrow();
        SitusFormDto dto = SitusFormDto.fromEntity(s);

        model.addAttribute("activeMenu", "situs");
        model.addAttribute("pageTitle", "Edit Situs");
        model.addAttribute("dto", dto);
        model.addAttribute("provinsiList", provRepo.findAllByOrderByNamaProvinsiAsc());
        return "admin/situs_form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("dto") SitusFormDto dto,
                       @RequestParam(value = "fileFoto", required = false) MultipartFile fileFoto,
                       RedirectAttributes ra) {

        Situs s = (dto.getSitusId() != null)
                ? situsRepo.findById(dto.getSitusId()).orElse(new Situs())
                : new Situs();

        s.setNamaSitus(dto.getNamaSitus());

        String tgl = dto.getTanggalDitemukan();
        if (tgl != null && tgl.length() > 10) tgl = tgl.substring(0, 10);
        s.setTahunDitemukan((tgl != null && !tgl.isBlank()) ? tgl : null);

        s.setKondisi(dto.getKondisi());
        s.setDeskripsi(dto.getDeskripsi());
        s.setLat(dto.getLat());
        s.setLon(dto.getLon());
        s.setMapsUrl(dto.getMapsUrl());

        if (fileFoto != null && !fileFoto.isEmpty()) {
            String url = storage.saveImage(fileFoto, "situs");
            s.setFotoUrl(url);
        } else {
            s.setFotoUrl(dto.getFotoUrl());
        }

        // set relasi dari id
        s.setProvinsi(null); s.setKabupaten(null); s.setKecamatan(null);
        if (dto.getProvinsiId() != null) provRepo.findById(dto.getProvinsiId()).ifPresent(s::setProvinsi);
        if (dto.getKabupatenId() != null) kabRepo.findById(dto.getKabupatenId()).ifPresent(s::setKabupaten);
        if (dto.getKecamatanId() != null) kecRepo.findById(dto.getKecamatanId()).ifPresent(s::setKecamatan);

        situsRepo.save(s);
        ra.addFlashAttribute("successMessage",
                dto.getSitusId() == null ? "Situs ditambahkan." : "Perubahan disimpan.");
        return "redirect:/admin/situs";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        if (situsRepo.existsById(id)) {
            situsRepo.deleteById(id);
            ra.addFlashAttribute("successMessage", "Situs dihapus.");
        }
        return "redirect:/admin/situs";
    }
}
