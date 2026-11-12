package com.stockapp.demo;

import com.stockapp.demo.model.Categorie;
import com.stockapp.demo.model.Entrepot;
import com.stockapp.demo.model.Produit;
import com.stockapp.demo.model.StockEntrepot;
import com.stockapp.demo.repository.CategorieRepository;
import com.stockapp.demo.repository.EntrepotRepository;
import com.stockapp.demo.repository.ProduitRepository;
import com.stockapp.demo.repository.StockEntrepotRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StockTransfertIntegrationTest extends com.stockapp.demo.IntegrationTestBase {

    @Autowired private CategorieRepository categorieRepository;
    @Autowired private ProduitRepository produitRepository;
    @Autowired private EntrepotRepository entrepotRepository;
    @Autowired private StockEntrepotRepository stockRepository;

    private Categorie categorie;
    private Produit produit;
    private Entrepot source;
    private Entrepot destination;
    private final List<StockEntrepot> stocks = new ArrayList<>();

    // ---------- données uniques par test, supprimées ensuite ----------

    @BeforeEach
    void creerDonnees() {
        String id = UUID.randomUUID().toString().substring(0, 8);
        categorie = categorieRepository.save(new Categorie("IT-" + id));

        Produit p = new Produit();
        p.setSku("IT-" + id);
        p.setName("Produit de test");
        p.setPrice(new BigDecimal("2.50"));
        p.setCategorie(categorie);
        produit = produitRepository.save(p);

        source = entrepotRepository.save(new Entrepot("IT-src-" + id, "Montréal"));
        destination = entrepotRepository.save(new Entrepot("IT-dst-" + id, "Laval"));
    }

    @AfterEach
    void nettoyer() {
        stockRepository.deleteAll(stocks);                 // l'ordre respecte les clés étrangères
        produitRepository.delete(produit);
        entrepotRepository.deleteAll(List.of(source, destination));
        categorieRepository.delete(categorie);
    }

    private void stock(Entrepot entrepot, int quantity) {
        StockEntrepot s = new StockEntrepot();
        s.setProduit(produit);
        s.setEntrepot(entrepot);
        s.setQuantity(quantity);
        stocks.add(stockRepository.save(s));
    }

    private int quantite(Entrepot entrepot) {
        return stockRepository.findByProduitIdAndEntrepotId(produit.getId(), entrepot.getId())
                .orElseThrow().getQuantity();
    }

    private String transfert(int quantity) {
        return """
                {"produitId": %d, "sourceId": %d, "destinationId": %d, "quantity": %d}
                """.formatted(produit.getId(), source.getId(), destination.getId(), quantity);
    }

    // ---------- tests ----------

    @Test
    @DisplayName("transfert valide : 204, la source est débitée et la destination créditée en base")
    void transfert_ok() throws Exception {
        stock(source, 50);
        stock(destination, 5);

        mvc.perform(post("/api/stocks/transfert").contentType(MediaType.APPLICATION_JSON).content(transfert(10)))
                .andExpect(status().isNoContent());

        assertThat(quantite(source)).isEqualTo(40);
        assertThat(quantite(destination)).isEqualTo(15);
    }

    @Test
    @DisplayName("stock insuffisant : refusé, aucune quantité modifiée")
    void stock_insuffisant() throws Exception {
        stock(source, 5);
        stock(destination, 5);

        mvc.perform(post("/api/stocks/transfert").contentType(MediaType.APPLICATION_JSON).content(transfert(10)))
                .andExpect(status().isBadRequest());

        assertThat(quantite(source)).isEqualTo(5);
        assertThat(quantite(destination)).isEqualTo(5);
    }

    @Test
    @DisplayName("panne pendant le transfert : tout est annulé, la source garde son stock")
    void panne_annule_tout() throws Exception {
        stock(source, 50);
        stock(destination, Integer.MAX_VALUE - 5);   // le crédit de 10 déborde : valeur négative, que le CHECK (quantity >= 0) refuse

        int statut = mvc.perform(post("/api/stocks/transfert")
                        .contentType(MediaType.APPLICATION_JSON).content(transfert(10)))
                .andReturn().getResponse().getStatus();

        assertThat(statut).isGreaterThanOrEqualTo(400);              // 409 si ton gestionnaire d'intégrité est en place
        assertThat(quantite(source)).isEqualTo(50);                  // le débit a été annulé avec le reste
        assertThat(quantite(destination)).isEqualTo(Integer.MAX_VALUE - 5);
    }
}