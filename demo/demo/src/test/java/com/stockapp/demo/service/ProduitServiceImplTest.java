package com.stockapp.demo.service;

import com.stockapp.demo.dto.request.ProduitRequest;
import com.stockapp.demo.dto.response.ProduitResponse;
import com.stockapp.demo.exception.DuplicateSkuException;
import com.stockapp.demo.exception.ResourceNotFoundException;
import com.stockapp.demo.mapper.ProduitMapper;
import com.stockapp.demo.mapper.ProduitMapperImpl;
import com.stockapp.demo.model.Categorie;
import com.stockapp.demo.model.Produit;
import com.stockapp.demo.repository.CategorieRepository;
import com.stockapp.demo.repository.ProduitRepository;
import com.stockapp.demo.repository.StockEntrepotRepository;
import com.stockapp.demo.repository.StockEntrepotRepository.StockTotal;
import com.stockapp.demo.service.ProduitServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProduitServiceImplTest {

    @Mock
    private ProduitRepository produitRepository;
    @Mock
    private CategorieRepository categorieRepository;
    @Mock
    private StockEntrepotRepository stockRepository;
    @Spy
    private ProduitMapper mapper = new ProduitMapperImpl();

    @InjectMocks
    private ProduitServiceImpl service;

    // ---------- données de test ----------

    private static Categorie categorie(Long id, String name) {
        Categorie c = new Categorie(name, null);
        c.setId(id);
        return c;
    }

    private static Produit produit(Long id, String sku, Categorie categorie) {
        Produit p = new Produit();
        p.setId(id);
        p.setSku(sku);
        p.setName("Produit " + sku);
        p.setPrice(new BigDecimal("2.50"));
        p.setCategorie(categorie);
        return p;
    }

    private static ProduitRequest request(String sku, Long categorieId) {
        return new ProduitRequest(sku, "Eau minérale", null, new BigDecimal("1.50"), categorieId);
    }

    // StockTotal est une interface : une petite classe anonyme suffit
    private static StockTotal total(long produitId, long total) {
        return new StockTotal() {
            public Long getProduitId() { return produitId; }
            public Long getTotal() { return total; }
        };
    }

    // ---------- lister ----------

    @Test
    @DisplayName("lister : le stock total vient d'une seule requête groupée, 0 si aucun stock")
    void lister_avec_totaux() {
        Categorie boissons = categorie(1L, "Boissons");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Produit> page = new PageImpl<>(
                List.of(produit(1L, "BOI-0001", boissons), produit(2L, "BOI-0002", boissons)), pageable, 2);
        when(produitRepository.findAll(pageable)).thenReturn(page);
        when(stockRepository.totalsByProduitIds(List.of(1L, 2L))).thenReturn(List.of(total(1L, 162L)));

        Page<ProduitResponse> resultat = service.lister(pageable);

        assertThat(resultat.getContent())
                .extracting(ProduitResponse::id, ProduitResponse::totalStock)
                .containsExactly(tuple(1L, 162L), tuple(2L, 0L));
    }

    @Test
    @DisplayName("lister : page vide, aucune requête de totaux")
    void lister_page_vide() {
        Pageable pageable = PageRequest.of(0, 10);
        when(produitRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));

        assertThat(service.lister(pageable).getContent()).isEmpty();

        verifyNoInteractions(stockRepository);
    }

    // ---------- obtenir ----------

    @Test
    @DisplayName("obtenir : produit inexistant, 404")
    void obtenir_introuvable() {
        when(produitRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenir(99L)).isInstanceOf(ResourceNotFoundException.class);
    }

    // ---------- creer ----------

    @Test
    @DisplayName("creer : rattache la catégorie, enregistre, stock total à 0")
    void creer_ok() {
        Categorie boissons = categorie(1L, "Boissons");
        when(produitRepository.existsBySku("BOI-0001")).thenReturn(false);
        when(categorieRepository.findById(1L)).thenReturn(Optional.of(boissons));
        when(produitRepository.save(any(Produit.class))).thenAnswer(returnsFirstArg());

        ProduitResponse reponse = service.creer(request("BOI-0001", 1L));

        ArgumentCaptor<Produit> captor = ArgumentCaptor.forClass(Produit.class);
        verify(produitRepository).save(captor.capture());
        assertThat(captor.getValue().getSku()).isEqualTo("BOI-0001");
        assertThat(captor.getValue().getCategorie()).isSameAs(boissons);
        assertThat(reponse.categorieId()).isEqualTo(1L);
        assertThat(reponse.totalStock()).isZero();
    }

    @Test
    @DisplayName("creer : SKU déjà utilisé, 409 et rien d'enregistré")
    void creer_sku_double() {
        when(produitRepository.existsBySku("BOI-0001")).thenReturn(true);

        assertThatThrownBy(() -> service.creer(request("BOI-0001", 1L)))
                .isInstanceOf(DuplicateSkuException.class);

        verify(produitRepository, never()).save(any());
    }

    @Test
    @DisplayName("creer : catégorie inexistante, 404 et rien d'enregistré")
    void creer_categorie_introuvable() {
        when(produitRepository.existsBySku("BOI-0001")).thenReturn(false);
        when(categorieRepository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.creer(request("BOI-0001", 9L)))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(produitRepository, never()).save(any());
    }

    // ---------- modifier ----------

    @Test
    @DisplayName("modifier : SKU inchangé, pas de vérification d'unicité, l'entité est mise à jour")
    void modifier_sku_inchange() {
        Categorie boissons = categorie(1L, "Boissons");
        Produit existant = produit(10L, "BOI-0001", boissons);
        when(produitRepository.findById(10L)).thenReturn(Optional.of(existant));
        when(categorieRepository.findById(1L)).thenReturn(Optional.of(boissons));
        when(stockRepository.totalsByProduitIds(List.of(10L))).thenReturn(List.of(total(10L, 40L)));

        ProduitResponse reponse = service.modifier(10L, request("BOI-0001", 1L));

        assertThat(existant.getName()).isEqualTo("Eau minérale");
        assertThat(reponse.totalStock()).isEqualTo(40L);
        verify(produitRepository, never()).existsBySku(any());
        // pas de save() : c'est le dirty checking qui écrira au commit (à prouver vendredi avec une vraie base)
        verify(produitRepository, never()).save(any());
    }

    @Test
    @DisplayName("modifier : SKU déjà pris par un autre produit, 409")
    void modifier_sku_pris() {
        Categorie boissons = categorie(1L, "Boissons");
        when(produitRepository.findById(10L)).thenReturn(Optional.of(produit(10L, "BOI-0001", boissons)));
        when(produitRepository.existsBySku("BOI-0002")).thenReturn(true);

        assertThatThrownBy(() -> service.modifier(10L, request("BOI-0002", 1L)))
                .isInstanceOf(DuplicateSkuException.class);
    }

    // ---------- supprimer ----------

    @Test
    @DisplayName("supprimer : produit inexistant, 404 et aucune suppression")
    void supprimer_introuvable() {
        when(produitRepository.existsById(5L)).thenReturn(false);

        assertThatThrownBy(() -> service.supprimer(5L)).isInstanceOf(ResourceNotFoundException.class);

        verify(produitRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("supprimer : produit existant, suppression demandée")
    void supprimer_ok() {
        when(produitRepository.existsById(5L)).thenReturn(true);

        service.supprimer(5L);

        verify(produitRepository).deleteById(5L);
    }
}