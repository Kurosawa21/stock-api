package com.stockapp.demo.repository;

import com.stockapp.demo.model.StockEntrepot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockEntrepotRepository extends JpaRepository<StockEntrepot, Long> {

    Optional<StockEntrepot> findByProduitIdAndEntrepotId(Long produitId, Long entrepotId);

    interface StockTotal {
        Long getProduitId();
        Long getTotal();
    }

    @Query("select s.produit.id as produitId, sum(s.quantity) as total " +
            "from StockEntrepot s where s.produit.id in :ids group by s.produit.id")
    List<StockTotal> totalsByProduitIds(@Param("ids") Collection<Long> ids);
}