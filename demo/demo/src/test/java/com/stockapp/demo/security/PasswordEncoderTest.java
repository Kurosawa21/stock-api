package com.stockapp.demo.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordEncoderTest {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    @DisplayName("le même mot de passe donne deux hashs différents (sel aléatoire), tous deux valides")
    void sel_aleatoire() {
        String hash1 = encoder.encode("secret");
        String hash2 = encoder.encode("secret");

        assertThat(hash1).isNotEqualTo(hash2);
        assertThat(encoder.matches("secret", hash1)).isTrue();
        assertThat(encoder.matches("secret", hash2)).isTrue();
    }

    @Test
    @DisplayName("un mauvais mot de passe est refusé")
    void mauvais_mot_de_passe() {
        String hash = encoder.encode("secret");

        assertThat(encoder.matches("autre", hash)).isFalse();
    }

    @Test
    @DisplayName("le hash n'est pas le mot de passe, et suit le format BCrypt (60 caractères, coût 10)")
    void format_bcrypt() {
        String hash = encoder.encode("secret");

        assertThat(hash).isNotEqualTo("secret");
        assertThat(hash).hasSize(60).startsWith("$2a$10$");
    }
}