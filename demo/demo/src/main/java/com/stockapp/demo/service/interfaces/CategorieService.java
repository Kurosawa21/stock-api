
package com.stockapp.demo.service.interfaces;

import com.stockapp.demo.dto.request.CategorieRequest;
import com.stockapp.demo.dto.response.CategorieDetailResponse;
import com.stockapp.demo.dto.response.CategorieResponse;
import com.stockapp.demo.dto.response.MappedCategorieDetailResponse;
import com.stockapp.demo.dto.response.MappedCategorieResponse;
import com.stockapp.demo.model.Categorie;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author TwAw
 */
public interface CategorieService {

    public MappedCategorieResponse creer(CategorieRequest request);

    public Categorie upsert(Categorie categorie);

    public Categorie getById(Long id);

    public Categorie getByName(String name);

    public List<Categorie> getAllCategorie();

    public String deleteById(Long id);

    public Page<Categorie> getAllPageableCategorie(Pageable pageable);

    public Page<MappedCategorieResponse> getAllPageableCategorieMapped(Pageable pageable);

    public Page<CategorieResponse> getAllPageableSpecCategorie(Specification<Categorie> specificication, Pageable pageable);

    List<CategorieDetailResponse> listerAvecProduitsNaif();

    List<CategorieDetailResponse> listerAvecProduits();

    Page<MappedCategorieDetailResponse> listerAvecProduitsPageable(Pageable pageable);

    Page<CategorieDetailResponse> listerAvecProduitsPagine(Pageable pageable);
}
