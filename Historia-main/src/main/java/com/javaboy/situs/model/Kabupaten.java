package com.javaboy.situs.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "TblKabupaten")
public class Kabupaten {

    @Id
    @Column(name = "KabupatenID")
    private Integer kabupatenId; // gunakan kode Kemendagri sebagai PK

    @Column(name = "NamaKabupaten", length = 255, nullable = false)
    private String namaKabupaten;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ProvinsiID", referencedColumnName = "ProvinsiID")
    private Provinsi provinsi;
}
