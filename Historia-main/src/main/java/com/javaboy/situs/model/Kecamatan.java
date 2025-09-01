package com.javaboy.situs.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "TblKecamatan")
public class Kecamatan {

    @Id
    @Column(name = "KecamatanID")
    private Integer kecamatanId; // gunakan kode Kemendagri sebagai PK

    @Column(name = "NamaKecamatan", length = 255, nullable = false)
    private String namaKecamatan;

    @ManyToOne(optional = false)
    @JoinColumn(name = "KabupatenID", referencedColumnName = "KabupatenID")
    private Kabupaten kabupaten;
}
