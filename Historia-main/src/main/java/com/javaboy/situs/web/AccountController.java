package com.javaboy.situs.web;

import com.javaboy.situs.model.User;
import com.javaboy.situs.repository.UserRepository;
import com.javaboy.situs.web.dto.RegisterForm;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("accountController")
public class AccountController {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AccountController(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new RegisterForm());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid @ModelAttribute("form") RegisterForm form,
                             BindingResult br,
                             RedirectAttributes ra) {

        // kirim balik error binding
        if (br.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.form", br);
            ra.addFlashAttribute("form", form);
            return "redirect:/register";
        }

        // cek konfirmasi password
        if (!form.passwordsMatch()) {
            ra.addFlashAttribute("error", "Konfirmasi password tidak sama.");
            ra.addFlashAttribute("form", form);
            return "redirect:/register";
        }

        // cek email unik
        if (users.findByEmail(form.getEmail()).isPresent()) {
            ra.addFlashAttribute("error", "Email sudah terdaftar.");
            ra.addFlashAttribute("form", form);
            return "redirect:/register";
        }

        // simpan user
        User u = new User();
        u.setNama(form.getNama());   // atau form.getName() sama saja (alias)
        u.setEmail(form.getEmail());
        u.setPasswordHash(encoder.encode(form.getPassword()));
        u.setIsActive(true);
        users.save(u);

        ra.addFlashAttribute("pesanSukses", "Registrasi berhasil. Silakan login.");
        return "redirect:/login";
    }
}
