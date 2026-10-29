package com.stockapp.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stock_entrepot",
        uniqueConstraints = @UniqueConstraint(columnNames = {"produit_id", "entrepot_id"}))
@Getter
@Setter
@NoArgsConstructor
public class StockEntrepot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entrepot_id", nullable = false)
    private Entrepot entrepot;

    @Column(nullable = false)
    private int quantity;
}