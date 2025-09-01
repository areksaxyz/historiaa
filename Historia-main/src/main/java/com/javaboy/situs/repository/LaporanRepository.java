package com.javaboy.situs.repository;

import com.javaboy.situs.model.Laporan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LaporanRepository extends JpaRepository<Laporan, Integer> {
    List<Laporan> findByStatusLaporan(String status);
    long countByStatusLaporan(String status);
    List<Laporan> findTop5ByStatusLaporanOrderByReportedAtDesc(String status);
}
