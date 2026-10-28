
package com.stockapp.demo.service;

import com.stockapp.demo.model.*;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author TwAw
 */
public interface CategorieService {

    public Categorie upsert(Categorie categorie);

    public Categorie getById(Long id);

    public Categorie getByName(String name);

    public List<Categorie> getAllCategorie();

    public String deleteById(Long id);

    Page<Categorie> getAllPageableCategorie(Pageable pageable);

    Page<Categorie> getAllPageableSpecCategorie(Specification<Categorie> specificication, Pageable pageable);
}
