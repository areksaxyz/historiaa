package com.javaboy.situs.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "TblSitus")
public class Situs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SitusID")
    private Integer situsId;

    @Column(name = "NamaSitus", length = 255, nullable = false)
    private String namaSitus;

    @Column(name = "TahunDitemukan", length = 50)
    private String tahunDitemukan;

    @Column(name = "Kondisi", length = 50)
    private String kondisi;

    @Column(name = "Deskripsi", columnDefinition = "TEXT")
    private String deskripsi;

    @Column(name = "FotoURL", length = 255)
    private String fotoUrl;

    @Column(name = "Lat", precision = 10, scale = 8)
    private BigDecimal lat;

    @Column(name = "Lon", precision = 11, scale = 8)
    private BigDecimal lon;

    /** NEW: Link Google Maps yang diinput admin */
    @Column(name = "MapsURL", length = 512)
    private String mapsUrl;

    @ManyToOne
    @JoinColumn(name = "ProvinsiID")
    private Provinsi provinsi;

    @ManyToOne
    @JoinColumn(name = "KabupatenID")
    private Kabupaten kabupaten;

    @ManyToOne
    @JoinColumn(name = "KecamatanID")
    private Kecamatan kecamatan;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
