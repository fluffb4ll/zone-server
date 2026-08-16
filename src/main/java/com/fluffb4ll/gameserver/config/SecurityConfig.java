package com.fluffb4ll.gameserver.config;

import com.fluffb4ll.gameserver.util.PepperedBCryptEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {
    @Value("${app.security.pepper}")
    private String pepper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PepperedBCryptEncoder(pepper, 12);
    }
}
