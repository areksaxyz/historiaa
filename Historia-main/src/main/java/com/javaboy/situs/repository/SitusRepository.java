package com.javaboy.situs.repository;

import com.javaboy.situs.model.Situs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SitusRepository extends JpaRepository<Situs, Integer> {

    // Method untuk mengambil 6 situs terbaru berdasarkan tanggal dibuat
    List<Situs> findTop6ByOrderByCreatedAtDesc();

    // Method untuk mencari situs berdasarkan nama
    List<Situs> findByNamaSitusContainingIgnoreCase(String keyword);

    // METHOD BARU: Query canggih untuk memfilter situs berdasarkan lokasi
    // Query ini akan menangani jika parameter filter ada yang null/kosong
    @Query("SELECT s FROM Situs s WHERE " +
           "(:provinsiId IS NULL OR s.provinsi.provinsiId = :provinsiId) AND " +
           "(:kabupatenId IS NULL OR s.kabupaten.kabupatenId = :kabupatenId) AND " +
           "(:kecamatanId IS NULL OR s.kecamatan.kecamatanId = :kecamatanId)")
    List<Situs> findByLokasi(
            @Param("provinsiId") Integer provinsiId,
            @Param("kabupatenId") Integer kabupatenId,
            @Param("kecamatanId") Integer kecamatanId
    );
}