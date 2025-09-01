package com.javaboy.situs.service;

import com.javaboy.situs.model.User;
import com.javaboy.situs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository users;

    @Value("${app.security.admin-emails:}")
    private String adminEmails;

    @Value("${app.security.admin-domain:}")
    private String adminDomain;

    public UserService(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        // login bisa berupa email ATAU username (bagian sebelum '@')
        Optional<User> opt;
        if (login.contains("@")) {
            opt = users.findByEmailIgnoreCase(login.trim());
        } else {
            // cari email yang diawali "username@"
            opt = users.findFirstByEmailStartingWithIgnoreCase(login.trim() + "@");
        }

        User u = opt.orElseThrow(() ->
                new UsernameNotFoundException("User tidak ditemukan"));

        // roles
        Set<GrantedAuthority> auths = new HashSet<>();
        auths.add(new SimpleGrantedAuthority("ROLE_USER"));

        if (isAdminEmail(u.getEmail())) {
            auths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return new org.springframework.security.core.userdetails.User(
                u.getEmail(), // username internal = email
                u.getPasswordHash(),
                u.getIsActive() != null && u.getIsActive(),
                true, true, true,
                auths
        );
    }

    private boolean isAdminEmail(String email) {
        if (email == null) return false;

        // fallback default admin
        if ("admin@gmail.com".equalsIgnoreCase(email)) return true;

        // allowlist email
        if (adminEmails != null && !adminEmails.isBlank()) {
            for (String e : adminEmails.split(",")) {
                if (email.equalsIgnoreCase(e.trim())) return true;
            }
        }
        // by domain (opsional)
        if (adminDomain != null && !adminDomain.isBlank()) {
            String suf = "@" + adminDomain.trim().toLowerCase(Locale.ROOT);
            return email.toLowerCase(Locale.ROOT).endsWith(suf);
        }
        return false;
    }
}
