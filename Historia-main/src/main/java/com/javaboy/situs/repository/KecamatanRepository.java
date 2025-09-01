package com.javaboy.situs.repository;

import com.javaboy.situs.model.Kecamatan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KecamatanRepository extends JpaRepository<Kecamatan, Integer> {
    List<Kecamatan> findByKabupaten_KabupatenIdOrderByNamaKecamatanAsc(Integer kabupatenId);
}
