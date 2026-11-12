package com.stockapp.demo.repository;

import com.stockapp.demo.model.Categorie;
import com.stockapp.demo.model.Entrepot;
import com.stockapp.demo.model.Produit;
import com.stockapp.demo.model.StockEntrepot;
import com.stockapp.demo.repository.CategorieRepository;
import com.stockapp.demo.repository.EntrepotRepository;
import com.stockapp.demo.repository.ProduitRepository;
import com.stockapp.demo.repository.StockEntrepotRepository;
import com.stockapp.demo.repository.StockEntrepotRepository.StockTotal;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class StockEntrepotRepositoryTest {

    @Autowired private CategorieRepository categorieRepository;
    @Autowired private ProduitRepository produitRepository;
    @Autowired private EntrepotRepository entrepotRepository;
    @Autowired private StockEntrepotRepository stockRepository;
    @Autowired private EntityManager em;

    // ---------- données de test ----------

    private Categorie categorie() {
        return categorieRepository.save(new Categorie("Boissons", 1000L));
    }

    private Produit produit(String sku, Categorie categorie) {
        Produit p = new Produit();
        p.setSku(sku);
        p.setName("Produit " + sku);
        p.setPrice(new BigDecimal("2.50"));
        p.setCategorie(categorie);
        return produitRepository.save(p);
    }

    private Entrepot entrepot(String name) {
        return entrepotRepository.save(new Entrepot(name, "Montréal"));
    }

    private StockEntrepot stock(Produit produit, Entrepot entrepot, int quantity) {
        StockEntrepot s = new StockEntrepot();
        s.setProduit(produit);
        s.setEntrepot(entrepot);
        s.setQuantity(quantity);
        return stockRepository.save(s);
    }

    private static Map<Long, Long> enMap(List<StockTotal> totaux) {
        return totaux.stream().collect(Collectors.toMap(StockTotal::getProduitId, StockTotal::getTotal));
    }

    // ---------- totalsByProduitIds ----------

    @Test
    @DisplayName("totalsByProduitIds : somme par produit sur tous les entrepôts, produit sans stock absent")
    void totaux_par_produit() {
        Categorie c = categorie();
        Produit p1 = produit("BOI-0001", c);
        Produit p2 = produit("BOI-0002", c);
        Produit p3 = produit("BOI-0003", c);                 // aucun stock
        Entrepot e1 = entrepot("Montréal");
        Entrepot e2 = entrepot("Laval");
        stock(p1, e1, 10);
        stock(p1, e2, 20);
        stock(p2, e1, 5);
        em.flush();
        em.clear();

        Map<Long, Long> totaux = enMap(
                stockRepository.totalsByProduitIds(List.of(p1.getId(), p2.getId(), p3.getId())));

        assertThat(totaux).containsOnly(entry(p1.getId(), 30L), entry(p2.getId(), 5L));
    }

    @Test
    @DisplayName("totalsByProduitIds : ne renvoie que les produits demandés")
    void totaux_ne_renvoie_que_les_ids_demandes() {
        Categorie c = categorie();
        Produit p1 = produit("BOI-0001", c);
        Produit p2 = produit("BOI-0002", c);
        Entrepot e1 = entrepot("Montréal");
        stock(p1, e1, 10);
        stock(p2, e1, 99);
        em.flush();
        em.clear();

        assertThat(enMap(stockRepository.totalsByProduitIds(List.of(p1.getId()))))
                .containsOnly(entry(p1.getId(), 10L));
    }

    // ---------- findByProduitIdAndEntrepotId ----------

    @Test
    @DisplayName("findByProduitIdAndEntrepotId : trouve la ligne pour la bonne paire, vide sinon")
    void recherche_par_paire() {
        Categorie c = categorie();
        Produit p = produit("BOI-0001", c);
        Entrepot e1 = entrepot("Montréal");
        Entrepot e2 = entrepot("Laval");
        stock(p, e1, 10);
        em.flush();
        em.clear();

        assertThat(stockRepository.findByProduitIdAndEntrepotId(p.getId(), e1.getId()))
                .get().extracting(StockEntrepot::getQuantity).isEqualTo(10);
        assertThat(stockRepository.findByProduitIdAndEntrepotId(p.getId(), e2.getId())).isEmpty();
    }

    // ---------- contrainte d'unicité ----------

    @Test
    @DisplayName("deux lignes de stock pour le même produit dans le même entrepôt : refusé")
    void paire_unique() {
        Categorie c = categorie();
        Produit p = produit("BOI-0001", c);
        Entrepot e = entrepot("Montréal");
        stock(p, e, 10);

        assertThatThrownBy(() -> stock(p, e, 7)).isInstanceOf(DataIntegrityViolationException.class);
    }
}