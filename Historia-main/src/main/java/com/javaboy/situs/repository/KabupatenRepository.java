package com.javaboy.situs.repository;

import com.javaboy.situs.model.Kabupaten;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KabupatenRepository extends JpaRepository<Kabupaten, Integer> {
    List<Kabupaten> findByProvinsi_ProvinsiIdOrderByNamaKabupatenAsc(Integer provinsiId);
}
