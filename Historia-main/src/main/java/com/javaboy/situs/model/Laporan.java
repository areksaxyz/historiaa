package com.javaboy.situs.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "TblLaporan")
public class Laporan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LaporanID")
    private Integer laporanId;
    
    @Column(name = "NamaSitus", length = 255)
    private String namaSitus;
    
    @Column(name = "DeskripsiSingkat", columnDefinition = "TEXT")
    private String deskripsiSingkat;
    
    @Column(name = "TahunDitemukan", length = 50)
    private String tahunDitemukan;
    
    @Column(name = "NamaPelapor", length = 100)
    private String namaPelapor;
    
    @Column(name = "EmailPelapor", length = 100)
    private String emailPelapor;
    
    @Column(name = "LokasiText", length = 255)
    private String lokasiText;
    
    @Column(name = "FotoURL", length = 255)
    private String fotoUrl;
    
    @Column(name = "StatusLaporan", length = 50)
    private String statusLaporan;

    @Column(name = "ReportedAt")
    private LocalDateTime reportedAt;

    @ManyToOne
    @JoinColumn(name = "ValidateBy")
    private User validateBy;
    
    // Default constructor
    public Laporan() {
        this.statusLaporan = "PENDING";
        this.reportedAt = LocalDateTime.now();
    }
}