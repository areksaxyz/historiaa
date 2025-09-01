package com.javaboy.situs.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "TblProvinsi")
public class Provinsi {

    @Id
    @Column(name = "ProvinsiID")
    private Integer provinsiId; // gunakan kode Kemendagri sebagai PK (TANPA auto increment)

    @Column(name = "NamaProvinsi", length = 255, nullable = false)
    private String namaProvinsi;
}
