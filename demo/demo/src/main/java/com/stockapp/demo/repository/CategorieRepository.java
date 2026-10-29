package com.stockapp.demo.repository;

import com.stockapp.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.io.Serializable;
import java.util.Optional;
import java.util.List;


public interface CategorieRepository extends JpaRepository<Categorie, Long>, JpaSpecificationExecutor<Categorie> {

    Optional<Categorie> findByName(String name);

    /* pour lister tous les produits d'une catégories pendant le listing des catégories
    // Solution 1 : JOIN FETCH (JPQL)
    // "DISTINCT" évite les doublons si une catégorie a plusieurs produits
    */
    @Query("SELECT DISTINCT c FROM Categorie c LEFT JOIN FETCH c.produits")
     List<Categorie> findAllWithProduits();



    // Solution 2 : EntityGraph (plus moderne, basé sur le nom des attributs)
    // @EntityGraph(attributePaths = {"produits"})
    // List<Categorie> findAll();


}
