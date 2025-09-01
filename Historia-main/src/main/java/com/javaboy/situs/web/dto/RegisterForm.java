package com.javaboy.situs.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterForm {
    @NotBlank
    private String nama;

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 6)
    private String password;

    @NotBlank
    private String confirmPassword;

    // getters & setters
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    // alias opsional untuk template yang pakai "name"
    public String getName() { return nama; }
    public void setName(String name) { this.nama = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public boolean passwordsMatch() {
        return password != null && password.equals(confirmPassword);
    }
}
