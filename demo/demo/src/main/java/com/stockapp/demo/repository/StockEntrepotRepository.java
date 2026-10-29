package com.stockapp.demo.repository;

import com.stockapp.demo.model.StockEntrepot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockEntrepotRepository extends JpaRepository<StockEntrepot, Long> {

    Optional<StockEntrepot> findByProduitIdAndEntrepotId(Long produitId, Long entrepotId);
}