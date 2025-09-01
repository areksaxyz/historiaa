package com.javaboy.situs.repository;

import com.javaboy.situs.model.Provinsi;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProvinsiRepository extends JpaRepository<Provinsi, Integer> {
    List<Provinsi> findAllByOrderByNamaProvinsiAsc();
}
