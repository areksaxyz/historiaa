package com.javaboy.situs.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "TblUsers")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Integer userId;

    @Column(name = "Nama", length = 100, nullable = false)
    private String nama;

    @Column(name = "Email", length = 100, unique = true, nullable = false)
    private String email;

    @Column(name = "PasswordHash", length = 255, nullable = false)
    private String passwordHash;

    @Column(name = "IsActive")
    private Boolean isActive;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    // Default constructor untuk inisialisasi default
    public User() {
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }
}