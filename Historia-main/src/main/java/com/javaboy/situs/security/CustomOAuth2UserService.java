package com.javaboy.situs.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Value("${app.security.admin-emails:}")
    private String adminEmails;

    @Value("${app.security.admin-domain:}")
    private String adminDomain;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User delegate = super.loadUser(userRequest);
        Map<String, Object> attrs = delegate.getAttributes();

        String email = (String) attrs.get("email");

        String nameAttrKey = Optional.ofNullable(
                userRequest.getClientRegistration()
                        .getProviderDetails()
                        .getUserInfoEndpoint()
                        .getUserNameAttributeName()
        ).orElse("sub");

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if (isAdminEmail(email)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return new DefaultOAuth2User(authorities, attrs, nameAttrKey);
    }

    private boolean isAdminEmail(String email) {
        if (email == null || email.isBlank()) return false;

        // fallback default admin
        if ("admin@gmail.com".equalsIgnoreCase(email)) return true;

        if (adminEmails != null && !adminEmails.isBlank()) {
            for (String p : adminEmails.split(",")) {
                if (email.equalsIgnoreCase(p.trim())) return true;
            }
        }
        if (adminDomain != null && !adminDomain.isBlank()) {
            String suffix = "@" + adminDomain.trim().toLowerCase(Locale.ROOT);
            return email.toLowerCase(Locale.ROOT).endsWith(suffix);
        }
        return false;
    }
}
