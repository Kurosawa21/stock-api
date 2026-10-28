package com.stockapp.demo.repository;

import com.stockapp.demo.model.Categorie;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class CategorieSpecifications {

    private CategorieSpecifications() {
        // classe utilitaire : on ne l'instancie jamais
    }

    /*public static Specification<Categorie> categorieId(Long categorieId) {
        return (root, query, cb) -> cb.equal(root.get("categorie").get("id"), categorieId);
    }*/

    public static Specification<Categorie> nomContient(String texte) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + texte.toLowerCase() + "%");
    }

   public static Specification<Categorie> prix(BigDecimal min) {
       //return (root, query, cb) -> cb.greaterThanOrEqualTo(root.<BigDecimal>get("prix"), min);
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.<BigDecimal>get("prix"), min);
    }

     /*public static Specification<Categorie> prixMax(BigDecimal max) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.<BigDecimal>get("prix"), max);
    }*/
}