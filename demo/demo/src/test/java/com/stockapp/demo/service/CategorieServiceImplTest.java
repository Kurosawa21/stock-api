package com.stockapp.demo.service;

import com.stockapp.demo.dto.request.CategorieRequest;
import com.stockapp.demo.dto.response.MappedCategorieDetailResponse;
import com.stockapp.demo.dto.response.MappedCategorieResponse;
import com.stockapp.demo.exception.DuplicateCategorieException;
import com.stockapp.demo.exception.ResourceNotFoundException;
import com.stockapp.demo.mapper.CategorieMapper;
import com.stockapp.demo.mapper.CategorieMapperImpl;
import com.stockapp.demo.model.Categorie;
import com.stockapp.demo.model.Produit;
import com.stockapp.demo.repository.CategorieRepository;
import com.stockapp.demo.service.CategorieServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategorieServiceImplTest {

    @Mock
    private CategorieRepository categorieRepository;

    @Spy
    private CategorieMapper mapper = new CategorieMapperImpl();

    @InjectMocks
    private CategorieServiceImpl service;

    // ---------- données de test ----------

    private static Categorie categorie(Long id, String name) {
        Categorie c = new Categorie(name, null);
        c.setId(id);
        return c;
    }

    // ---------- creer ----------

    @Test
    @DisplayName("creer : enregistre une entité construite depuis la requête et renvoie un DTO")
    void creer_ok() {
        // le faux repository imite la base : il attribue un id à l'entité enregistrée
        when(categorieRepository.save(any(Categorie.class))).thenAnswer(invocation -> {
            Categorie c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        MappedCategorieResponse reponse = service.creer(new CategorieRequest("Boissons", 1000L));

        ArgumentCaptor<Categorie> captor = ArgumentCaptor.forClass(Categorie.class);
        verify(categorieRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Boissons");
        assertThat(reponse.id()).isEqualTo(1L);
        assertThat(reponse.name()).isEqualTo("Boissons");
    }

    @Test
    @DisplayName("creer : nom déjà utilisé, exception et rien d'enregistré")
    void creer_doublon() {
        when(categorieRepository.findByName("Boissons")).thenReturn(Optional.of(categorie(1L, "Boissons")));

        assertThatThrownBy(() -> service.creer(new CategorieRequest("Boissons", 1000L)))
                .isInstanceOf(DuplicateCategorieException.class);

        verify(categorieRepository, never()).save(any());
    }

    // ---------- upsert ----------

    @Test
    @DisplayName("upsert : renvoie l'entité retournée par le repository (celle qui porte l'id)")
    void upsert_ok() {
        Categorie aEnregistrer = categorie(null, "Hygiène");
        when(categorieRepository.save(aEnregistrer)).thenReturn(categorie(7L, "Hygiène"));

        Categorie resultat = service.upsert(aEnregistrer);

        assertThat(resultat.getId()).isEqualTo(7L);
        assertThat(resultat.getName()).isEqualTo("Hygiène");
    }

    // ---------- listes paginées ----------

    @Test
    @DisplayName("getAllPageableCategorieMapped : chaque catégorie de la page devient un DTO")
    void page_mappee() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Categorie> page = new PageImpl<>(
                List.of(categorie(1L, "Boissons"), categorie(2L, "Épicerie")), pageable, 2);
        when(categorieRepository.findAll(pageable)).thenReturn(page);

        Page<MappedCategorieResponse> resultat = service.getAllPageableCategorieMapped(pageable);

        assertThat(resultat.getTotalElements()).isEqualTo(2);
        assertThat(resultat.getContent())
                .extracting(MappedCategorieResponse::id, MappedCategorieResponse::name)
                .containsExactly(tuple(1L, "Boissons"), tuple(2L, "Épicerie"));
    }

    @Test
    @DisplayName("listerAvecProduitsPageable : les produits sont convertis en liste de SKU")
    void page_avec_produits() {
        Categorie boissons = categorie(1L, "Boissons");
        Produit eau = new Produit();
        eau.setSku("BOI-0001");
        boissons.getProduits().add(eau);
        Categorie epicerie = categorie(2L, "Épicerie");   // sans produit

        Pageable pageable = PageRequest.of(0, 10);
        when(categorieRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(boissons, epicerie), pageable, 2));

        Page<MappedCategorieDetailResponse> resultat = service.listerAvecProduitsPageable(pageable);

        assertThat(resultat.getContent())
                .extracting(MappedCategorieDetailResponse::name, MappedCategorieDetailResponse::produits)
                .containsExactly(tuple("Boissons", List.of("BOI-0001")), tuple("Épicerie", List.of()));
    }

    // ---------- lectures simples ----------

    @Test
    @DisplayName("getById : renvoie l'entité quand elle existe")
    void getById_trouve() {
        Categorie boissons = categorie(1L, "Boissons");
        when(categorieRepository.findById(1L)).thenReturn(Optional.of(boissons));

        assertThat(service.getById(1L)).isSameAs(boissons);
    }

    @Test
    @DisplayName("getById : catégorie inexistante, ResourceNotFoundException")
    void getById_absent() {
        when(categorieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("getByName : renvoie l'entité quand elle existe, null sinon (comportement actuel)")
    void getByName() {
        Categorie boissons = categorie(1L, "Boissons");
        when(categorieRepository.findByName("Boissons")).thenReturn(Optional.of(boissons));
        when(categorieRepository.findByName("Inconnue")).thenReturn(Optional.empty());

        assertThat(service.getByName("Boissons")).isSameAs(boissons);
        assertThat(service.getByName("Inconnue")).isNull();
    }

    @Test
    @DisplayName("getAllCategorie : renvoie la liste du repository telle quelle")
    void getAll() {
        List<Categorie> categories = List.of(categorie(1L, "Boissons"), categorie(2L, "Épicerie"));
        when(categorieRepository.findAll()).thenReturn(categories);

        assertThat(service.getAllCategorie()).containsExactlyElementsOf(categories);
    }

    // ---------- deleteById ----------

    @Test
    @DisplayName("deleteById : catégorie existante, suppression demandée")
    void delete_existante() {
        when(categorieRepository.existsById(1L)).thenReturn(true);

        service.deleteById(1L);

        //assertThat(resultat).isEqualTo("deleted!");
        verify(categorieRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteById : catégorie inexistante, exception et aucune suppression")
    void delete_inexistante() {
        when(categorieRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteById(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(categorieRepository, never()).deleteById(any());
    }
}