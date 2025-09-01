package com.javaboy.situs;

import com.javaboy.situs.model.Kabupaten;
import com.javaboy.situs.model.Kecamatan;
import com.javaboy.situs.model.Provinsi;
import com.javaboy.situs.model.Situs;
import com.javaboy.situs.repository.KabupatenRepository;
import com.javaboy.situs.repository.KecamatanRepository;
import com.javaboy.situs.repository.ProvinsiRepository;
import com.javaboy.situs.repository.SitusRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootApplication
public class SitusApplication {

    private static final Logger log = LoggerFactory.getLogger(SitusApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SitusApplication.class, args);
    }

    @Bean
    @Transactional
    CommandLineRunner initDatabase(ProvinsiRepository provinsiRepo,
                                   KabupatenRepository kabupatenRepo,
                                   KecamatanRepository kecamatanRepo,
                                   SitusRepository situsRepo) {
        return args -> {
            if (provinsiRepo.count() > 0) {
                log.info("Database sudah berisi data, proses inisialisasi dilewati.");
                return;
            }

            // Menggunakan format CSV default TANPA header, sesuai file Anda
            CSVFormat format = CSVFormat.DEFAULT.builder().build();

            // 1. Memuat data Provinsi dari CSV
            log.info("Memuat data provinsi dari CSV...");
            try (Reader reader = new InputStreamReader(new ClassPathResource("data/provinsi.csv").getInputStream(), StandardCharsets.UTF_8)) {
                CSVParser csvParser = new CSVParser(reader, format);
                List<Provinsi> provinsiList = new ArrayList<>();
                for (CSVRecord record : csvParser) {
                    Provinsi p = new Provinsi();
                    p.setProvinsiId(Integer.parseInt(record.get(0))); // Kolom ke-0 untuk ID
                    p.setNamaProvinsi(record.get(1)); // Kolom ke-1 untuk Nama
                    provinsiList.add(p);
                }
                provinsiRepo.saveAll(provinsiList);
                log.info("Berhasil memuat {} data provinsi.", provinsiRepo.count());
            }

            // 2. Memuat data Kabupaten dari CSV
            log.info("Memuat data kabupaten dari CSV...");
            Map<Integer, Provinsi> provinsiMap = provinsiRepo.findAll().stream()
                    .collect(Collectors.toMap(Provinsi::getProvinsiId, Function.identity()));
            try (Reader reader = new InputStreamReader(new ClassPathResource("data/kabupaten.csv").getInputStream(), StandardCharsets.UTF_8)) {
                CSVParser csvParser = new CSVParser(reader, format);
                List<Kabupaten> kabupatenList = new ArrayList<>();
                for (CSVRecord record : csvParser) {
                    try {
                        Kabupaten k = new Kabupaten();
                        k.setKabupatenId(Integer.parseInt(record.get(0))); // Kolom ke-0 untuk ID
                        k.setNamaKabupaten(record.get(2)); // Kolom ke-2 untuk Nama
                        Provinsi provinsiInduk = provinsiMap.get(Integer.parseInt(record.get(1))); // Kolom ke-1 untuk ID Provinsi
                        if (provinsiInduk != null) {
                            k.setProvinsi(provinsiInduk);
                            kabupatenList.add(k);
                        }
                    } catch (Exception e) {
                        log.error("Gagal memproses baris kabupaten: {}", record.toString());
                    }
                }
                kabupatenRepo.saveAll(kabupatenList);
                log.info("Berhasil memuat {} data kabupaten.", kabupatenRepo.count());
            }

            // 3. Memuat data Kecamatan dari CSV
            log.info("Memuat data kecamatan dari CSV...");
            Map<Integer, Kabupaten> kabupatenMap = kabupatenRepo.findAll().stream()
                    .collect(Collectors.toMap(Kabupaten::getKabupatenId, Function.identity()));
            try (Reader reader = new InputStreamReader(new ClassPathResource("data/kecamatan.csv").getInputStream(), StandardCharsets.UTF_8)) {
                CSVParser csvParser = new CSVParser(reader, format);
                List<Kecamatan> kecamatanList = new ArrayList<>();
                for (CSVRecord record : csvParser) {
                    try {
                        Kecamatan kc = new Kecamatan();
                        kc.setKecamatanId(Integer.parseInt(record.get(0))); // Kolom ke-0 untuk ID
                        kc.setNamaKecamatan(record.get(2)); // Kolom ke-2 untuk Nama
                        Kabupaten kabupatenInduk = kabupatenMap.get(Integer.parseInt(record.get(1))); // Kolom ke-1 untuk ID Kabupaten
                        if(kabupatenInduk != null) {
                            kc.setKabupaten(kabupatenInduk);
                            kecamatanList.add(kc);
                        }
                    } catch (Exception e) {
                        log.error("Gagal memproses baris kecamatan: {}", record.toString());
                    }
                }
                kecamatanRepo.saveAll(kecamatanList);
                log.info("Berhasil memuat {} data kecamatan.", kecamatanRepo.count());
            }

            // 4. Membuat data Situs contoh yang terhubung dengan lokasi
            log.info("Membuat data situs contoh...");
            Map<Integer, Kecamatan> kecamatanMap = kecamatanRepo.findAll().stream()
                    .collect(Collectors.toMap(Kecamatan::getKecamatanId, Function.identity()));
            
            Situs borobudur = new Situs();
            borobudur.setNamaSitus("Candi Borobudur");
            borobudur.setDeskripsi("Candi Buddha terbesar di dunia...");
            borobudur.setLat(new BigDecimal("-7.607874"));
            borobudur.setLon(new BigDecimal("110.203751"));
            borobudur.setProvinsi(provinsiMap.get(33));
            borobudur.setKabupaten(kabupatenMap.get(3308));
            borobudur.setKecamatan(kecamatanMap.get(3308110));
            borobudur.setFotoUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/8/8c/Borobudur-Noth-View-of-Stupas.jpg/1280px-Borobudur-Noth-View-of-Stupas.jpg");

            Situs monas = new Situs();
            monas.setNamaSitus("Monumen Nasional (Monas)");
            monas.setDeskripsi("Monumen peringatan setinggi 132 meter...");
            monas.setLat(new BigDecimal("-6.175392"));
            monas.setLon(new BigDecimal("106.827153"));
            monas.setProvinsi(provinsiMap.get(31));
            monas.setKabupaten(kabupatenMap.get(3171));
            monas.setKecamatan(kecamatanMap.get(3171060));
            monas.setFotoUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/5/5d/Monumen_Nasional_Jakarta.jpg/800px-Monumen_Nasional_Jakarta.jpg");

            situsRepo.saveAll(Arrays.asList(borobudur, monas));
            log.info("Berhasil membuat data contoh lengkap.");
        };
    }
}
