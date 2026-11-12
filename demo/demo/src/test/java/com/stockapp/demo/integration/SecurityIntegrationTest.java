package com.stockapp.demo.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityIntegrationTest extends IntegrationTestBase {

    @Test
    @DisplayName("sans identifiants : 401 avec l'en-tête WWW-Authenticate")
    void sans_identifiants() throws Exception {
        mvc.perform(get("/api/produits"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().exists("WWW-Authenticate"));
    }

    @Test
    @DisplayName("mauvais mot de passe : 401")
    void mauvais_mot_de_passe() throws Exception {
        mvc.perform(get("/api/produits").with(httpBasic("admin", "mauvais")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("bons identifiants : 200")
    void bons_identifiants() throws Exception {
        mvc.perform(get("/api/produits").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("la documentation de l'API est publique")
    void documentation_publique() throws Exception {
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE par un gestionnaire : 403, authentifié mais pas autorisé")
    void delete_interdit_au_gestionnaire() throws Exception {
        mvc.perform(delete("/api/produits/999999").with(httpBasic("gestionnaire", "gest123")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE par un administrateur : autorisé, la requête atteint le service (404, id inexistant)")
    void delete_admin_atteint_le_service() throws Exception {
        mvc.perform(delete("/api/produits/999999").with(httpBasic("admin", "admin123")))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE sans identifiants : 401, pas 403")
    void delete_sans_identifiants() throws Exception {
        mvc.perform(delete("/api/produits/999999"))
                .andExpect(status().isUnauthorized());
    }
}