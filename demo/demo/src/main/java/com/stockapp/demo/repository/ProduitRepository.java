package com.stockapp.demo.repository;

import com.stockapp.demo.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProduitRepository extends JpaRepository<Produit, Long> {

    boolean existsBySku(String sku);
}