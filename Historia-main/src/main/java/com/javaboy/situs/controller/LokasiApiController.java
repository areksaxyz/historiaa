package com.javaboy.situs.controller;

import com.javaboy.situs.model.Kabupaten;
import com.javaboy.situs.model.Kecamatan;
import com.javaboy.situs.repository.KabupatenRepository;
import com.javaboy.situs.repository.KecamatanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/lokasi")
public class LokasiApiController {

    private static final Logger log = LoggerFactory.getLogger(LokasiApiController.class);

    private final KabupatenRepository kabRepo;
    private final KecamatanRepository kecRepo;

    public LokasiApiController(KabupatenRepository kabRepo, KecamatanRepository kecRepo) {
        this.kabRepo = kabRepo;
        this.kecRepo = kecRepo;
    }

    @GetMapping(value = "/kabupaten/{provId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> listKabupatenByPath(@PathVariable Integer provId) {
        List<Kabupaten> list = kabRepo.findByProvinsi_ProvinsiIdOrderByNamaKabupatenAsc(provId);
        log.info("[API] /api/lokasi/kabupaten/{} -> {} rows", provId, list.size());
        return mapKabupaten(list);
    }

    @GetMapping(value = "/kecamatan/{kabId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> listKecamatanByPath(@PathVariable Integer kabId) {
        List<Kecamatan> list = kecRepo.findByKabupaten_KabupatenIdOrderByNamaKecamatanAsc(kabId);
        log.info("[API] /api/lokasi/kecamatan/{} -> {} rows", kabId, list.size());
        return mapKecamatan(list);
    }

    @GetMapping(value = "/kabupaten", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> listKabupatenByQuery(@RequestParam Integer provinsiId) {
        List<Kabupaten> list = kabRepo.findByProvinsi_ProvinsiIdOrderByNamaKabupatenAsc(provinsiId);
        log.info("[API] /api/lokasi/kabupaten?provinsiId={} -> {} rows", provinsiId, list.size());
        return mapKabupaten(list);
    }

    @GetMapping(value = "/kecamatan", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> listKecamatanByQuery(@RequestParam Integer kabupatenId) {
        List<Kecamatan> list = kecRepo.findByKabupaten_KabupatenIdOrderByNamaKecamatanAsc(kabupatenId);
        log.info("[API] /api/lokasi/kecamatan?kabupatenId={} -> {} rows", kabupatenId, list.size());
        return mapKecamatan(list);
    }

    private List<Map<String, Object>> mapKabupaten(List<Kabupaten> list) {
        List<Map<String, Object>> out = new ArrayList<>(list.size());
        for (Kabupaten k : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("kabupatenId", k.getKabupatenId());
            m.put("namaKabupaten", k.getNamaKabupaten());
            out.add(m);
        }
        return out;
    }

    private List<Map<String, Object>> mapKecamatan(List<Kecamatan> list) {
        List<Map<String, Object>> out = new ArrayList<>(list.size());
        for (Kecamatan k : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("kecamatanId", k.getKecamatanId());
            m.put("namaKecamatan", k.getNamaKecamatan());
            out.add(m);
        }
        return out;
    }
}
