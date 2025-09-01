package com.javaboy.situs.config;

import com.javaboy.situs.model.User;
import com.javaboy.situs.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedDefaultAdmin(UserRepository repo, PasswordEncoder enc) {
        return args -> repo.findByEmail("admin@gmail.com").orElseGet(() -> {
            User u = new User();
            u.setNama("Administrator");
            u.setEmail("admin@gmail.com");
            u.setPasswordHash(enc.encode("admin1122"));
            u.setIsActive(true);
            System.out.println("[INIT] Admin default dibuat: admin@gmail.com / admin1122");
            return repo.save(u);
        });
    }
}
