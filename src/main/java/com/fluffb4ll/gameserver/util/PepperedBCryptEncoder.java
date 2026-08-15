package com.fluffb4ll.gameserver.util;

import org.jspecify.annotations.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** Кастомная имплементация PasswordEncoder с использованием BCryptEncoder.
 * Пароль предварительно хэшируется с использованием алгоритма HMAC-SHA256, где пеппер используется в качестве ключа.
 * Это сделано для того, чтобы алгоритм BCrypt не обрезал хвост длинного пароля при хэшировании. */
public class PepperedBCryptEncoder implements PasswordEncoder {
    private final PasswordEncoder bcrypt;
    private final String pepper;

    public PepperedBCryptEncoder(String pepper, int bcryptStrength) {
        if (pepper == null || pepper.isBlank()) {
            throw new IllegalArgumentException("Pepper must not be empty");
        }
        this.pepper = pepper;
        bcrypt = new BCryptPasswordEncoder(bcryptStrength);
    }

    @Override
    public @Nullable String encode(@Nullable CharSequence rawPassword) {
        String peppered = hmacSha256(rawPassword);
        return bcrypt.encode(peppered);
    }

    @Override
    public boolean matches(@Nullable CharSequence rawPassword, @Nullable String encodedPassword) {
        String peppered = hmacSha256(rawPassword);
        return bcrypt.matches(peppered, encodedPassword);
    }

    private String hmacSha256(CharSequence rawPassword) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret = new SecretKeySpec(
                    pepper.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            mac.init(secret);
            byte[] hmacBytes = mac.doFinal(rawPassword.toString().getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to calculate HMAC-SHA256 for password", e);
        }
    }
}
