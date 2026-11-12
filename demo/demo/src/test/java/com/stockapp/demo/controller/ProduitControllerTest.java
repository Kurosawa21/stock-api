package com.stockapp.demo.controllers;

import com.stockapp.demo.controller.ProduitController;
import com.stockapp.demo.dto.response.ProduitResponse;
import com.stockapp.demo.exception.DuplicateSkuException;
import com.stockapp.demo.exception.ResourceNotFoundException;
import com.stockapp.demo.service.interfaces.ProduitService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProduitController.class)
class ProduitControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ProduitService produitService;

    private static final String PRODUIT_VALIDE = """
            {"sku": "BOI-0001", "name": "Eau", "description": null, "price": 1.50, "categorieId": 1}
            """;

    private static ProduitResponse reponse(Long id, String sku) {
        return new ProduitResponse(id, sku, "Eau", null, new BigDecimal("1.50"), 1L, 0L);
    }

    // ---------- GET (liste) ----------

    @Test
    @DisplayName("GET /api/produits : la pagination et le tri arrivent bien au service, la réponse suit PageResponse")
    void lister_ok() throws Exception {
        Pageable demande = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "price"));
        Page<ProduitResponse> page = new PageImpl<>(List.of(reponse(1L, "BOI-0001")), demande, 12);
        when(produitService.lister(any(Pageable.class))).thenReturn(page);

        mvc.perform(get("/api/produits").param("size", "5").param("sort", "price,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].sku").value("BOI-0001"))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(12))
                .andExpect(jsonPath("$.totalPages").value(3));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(produitService).lister(captor.capture());
        assertThat(captor.getValue().getPageSize()).isEqualTo(5);
        assertThat(captor.getValue().getSort().getOrderFor("price").getDirection())
                .isEqualTo(Sort.Direction.DESC);
    }

    @Test
    @DisplayName("GET /api/produits?sort=foo : tri interdit, 400, service jamais appelé")
    void lister_tri_interdit() throws Exception {
        mvc.perform(get("/api/produits").param("sort", "foo"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(produitService);
    }

    // ---------- GET (un produit) ----------

    @Test
    @DisplayName("GET /api/produits/{id} : produit existant, 200")
    void obtenir_ok() throws Exception {
        when(produitService.obtenir(1L)).thenReturn(reponse(1L, "BOI-0001"));

        mvc.perform(get("/api/produits/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("BOI-0001"))
                .andExpect(jsonPath("$.totalStock").value(0));
    }

    @Test
    @DisplayName("GET /api/produits/{id} : produit inexistant, 404 au format ProblemDetail")
    void obtenir_introuvable() throws Exception {
        when(produitService.obtenir(99L)).thenThrow(new ResourceNotFoundException("Produit", 99L));

        mvc.perform(get("/api/produits/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404));
    }

    // ---------- POST ----------

    @Test
    @DisplayName("POST /api/produits : requête valide, 201 avec l'en-tête Location")
    void creer_ok() throws Exception {
        when(produitService.creer(any())).thenReturn(reponse(10L, "BOI-0001"));

        mvc.perform(post("/api/produits").contentType(MediaType.APPLICATION_JSON).content(PRODUIT_VALIDE))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/produits/10")))
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    @DisplayName("POST /api/produits : SKU et prix invalides, 400 avec la liste des champs, service jamais appelé")
    void creer_validation() throws Exception {
        String invalide = """
                {"sku": "abc", "name": "Eau", "price": -1, "categorieId": 1}
                """;

        mvc.perform(post("/api/produits").contentType(MediaType.APPLICATION_JSON).content(invalide))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.sku").exists())
                .andExpect(jsonPath("$.errors.price").exists());

        verifyNoInteractions(produitService);
    }

    @Test
    @DisplayName("POST /api/produits : JSON mal formé, 400 (et non 500)")
    void creer_json_casse() throws Exception {
        mvc.perform(post("/api/produits").contentType(MediaType.APPLICATION_JSON).content("{\"sku\":"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(produitService);
    }

    @Test
    @DisplayName("POST /api/produits : SKU déjà utilisé, 409")
    void creer_sku_double() throws Exception {
        when(produitService.creer(any())).thenThrow(new DuplicateSkuException("BOI-0001"));

        mvc.perform(post("/api/produits").contentType(MediaType.APPLICATION_JSON).content(PRODUIT_VALIDE))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    // ---------- PUT et DELETE ----------

    @Test
    @DisplayName("PUT /api/produits/{id} : modification valide, 200")
    void modifier_ok() throws Exception {
        when(produitService.modifier(any(), any())).thenReturn(reponse(10L, "BOI-0001"));

        mvc.perform(put("/api/produits/10").contentType(MediaType.APPLICATION_JSON).content(PRODUIT_VALIDE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    @DisplayName("DELETE /api/produits/{id} : 204 et suppression demandée au service")
    void supprimer_ok() throws Exception {
        mvc.perform(delete("/api/produits/5")).andExpect(status().isNoContent());

        verify(produitService).supprimer(5L);
    }

    @Test
    @DisplayName("DELETE /api/produits/{id} : produit inexistant, 404")
    void supprimer_introuvable() throws Exception {
        doThrow(new ResourceNotFoundException("Produit", 5L)).when(produitService).supprimer(5L);

        mvc.perform(delete("/api/produits/5")).andExpect(status().isNotFound());
    }
}