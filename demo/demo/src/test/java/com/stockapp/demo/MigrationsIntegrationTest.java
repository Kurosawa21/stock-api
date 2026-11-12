package com.stockapp.demo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional      // ici sans risque : aucun service transactionnel n'est exercé, tout est annulé à la fin
class MigrationsIntegrationTest extends IntegrationTestBase {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    @DisplayName("Flyway : toutes les migrations se sont appliquées sans échec")
    void aucune_migration_en_echec() {
        Integer echecs = jdbc.queryForObject(
                "select count(*) from flyway_schema_history where success = false", Integer.class);
        Integer appliquees = jdbc.queryForObject(
                "select count(*) from flyway_schema_history", Integer.class);

        assertThat(echecs).isZero();
        assertThat(appliquees).isGreaterThanOrEqualTo(3);
    }

    @ParameterizedTest(name = "table {0}")
    @ValueSource(strings = {"categorie", "produit", "entrepot", "stock_entrepot"})
    @DisplayName("le schéma contient les tables attendues")
    void tables_presentes(String table) {
        Integer n = jdbc.queryForObject(
                "select count(*) from information_schema.tables where table_schema = 'public' and table_name = ?",
                Integer.class, table);

        assertThat(n).isEqualTo(1);
    }

    @Test
    @DisplayName("contrainte CHECK : PostgreSQL refuse une quantité négative")
    void quantite_negative_refusee() {
        Long entrepotId = jdbc.queryForObject(
                "insert into entrepot (name, city) values ('test', 'test') returning id", Long.class);
        Long produitId = jdbc.queryForObject("select min(id) from produit", Long.class);

        assertThatThrownBy(() -> jdbc.update(
                "insert into stock_entrepot (produit_id, entrepot_id, quantity) values (?, ?, ?)",
                produitId, entrepotId, -5))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("contrainte UNIQUE : un second stock pour la même paire produit/entrepôt est refusé")
    void paire_unique() {
        Map<String, Object> paire = jdbc.queryForMap(
                "select produit_id, entrepot_id from stock_entrepot limit 1");

        assertThatThrownBy(() -> jdbc.update(
                "insert into stock_entrepot (produit_id, entrepot_id, quantity) values (?, ?, ?)",
                paire.get("produit_id"), paire.get("entrepot_id"), 1))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}