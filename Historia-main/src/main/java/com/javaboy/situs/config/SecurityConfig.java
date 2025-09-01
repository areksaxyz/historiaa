package com.javaboy.situs.config;

import com.javaboy.situs.model.User;
import com.javaboy.situs.repository.UserRepository;
import com.javaboy.situs.security.CustomOAuth2UserService;
import com.javaboy.situs.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.util.Map;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authProvider,
            CustomOAuth2UserService customOAuth2UserService,
            UserRepository userRepository,
            PasswordEncoder encoder
    ) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authenticationProvider(authProvider)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/", "/login", "/register", "/registrasi", "/error",
                        "/api/lokasi/**",
                        "/css/**", "/js/**", "/images/**", "/uploads/**", "/webjars/**",
                        "/oauth2/**"
                ).permitAll()
                .requestMatchers(
                        "/admin/**",
                        "/situs/add", "/situs/save", "/situs/edit/**", "/situs/delete/**"
                ).hasRole("ADMIN")
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/authenticate")
                .successHandler((req, res, authentication) -> {
                    boolean isAdmin = authentication.getAuthorities().stream()
                            .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
                    res.sendRedirect(isAdmin ? "/admin/dashboard" : "/");
                })
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .oauth2Login(oauth -> oauth
                .loginPage("/login")
                .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                .successHandler((req, res, authentication) -> {
                    OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
                    String regId = token.getAuthorizedClientRegistrationId(); // "google-login" / "google-register"

                    OAuth2User principal = (OAuth2User) authentication.getPrincipal();
                    Map<String, Object> attrs = principal != null ? principal.getAttributes() : null;
                    String email = attrs != null ? (String) attrs.get("email") : null;
                    String name  = attrs != null ? (String) attrs.getOrDefault("name", email) : null;

                    boolean exists = email != null && userRepository.findByEmail(email).isPresent();

                    if ("google-login".equals(regId)) {
                        if (!exists) {
                            new SecurityContextLogoutHandler().logout(req, res, null);
                            HttpSession session = req.getSession(false);
                            if (session != null) session.invalidate();
                            res.sendRedirect("/login?notRegistered=true");
                            return;
                        }
                    } else if ("google-register".equals(regId)) {
                        if (email != null && !exists) {
                            User u = new User();
                            u.setEmail(email);
                            u.setNama(name != null ? name : email);
                            // password dummy acak (tidak dipakai untuk login form)
                            u.setPasswordHash(encoder.encode(UUID.randomUUID().toString()));
                            u.setIsActive(true);
                            userRepository.save(u);
                        }
                    }

                    boolean isAdmin = authentication.getAuthorities().stream()
                            .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
                    res.sendRedirect(isAdmin ? "/admin/dashboard" : "/");
                })
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
