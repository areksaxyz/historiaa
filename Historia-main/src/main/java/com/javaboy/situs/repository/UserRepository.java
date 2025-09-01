package com.javaboy.situs.repository;

import com.javaboy.situs.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Tambahan untuk login pakai email/username:
    Optional<User> findByEmailIgnoreCase(String email);

    // Cari user berdasarkan prefix sebelum '@' (username)
    Optional<User> findFirstByEmailStartingWithIgnoreCase(String prefix);
}
