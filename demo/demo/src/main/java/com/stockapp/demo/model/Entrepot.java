package com.stockapp.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "entrepot")
@Getter
@Setter
@NoArgsConstructor
public class Entrepot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String city;

    // Composition : les lignes de stock n'existent que dans un entrepôt
    @OneToMany(mappedBy = "entrepot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockEntrepot> stocks = new ArrayList<>();

    public Entrepot(String name, String city) {
        this.name = name;
        this.city = city;
    }

    public void addStock(StockEntrepot stock) {
        stocks.add(stock);
        stock.setEntrepot(this);
    }

    public void removeStock(StockEntrepot stock) {
        stocks.remove(stock);
        stock.setEntrepot(null);
    }
}