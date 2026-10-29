package com.stockapp.demo.model;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.*;
import jakarta.persistence.Column;

//import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

//import com.stockapp.demo.model.Produit;

//@Data //deprecated
@NoArgsConstructor
@Entity // This tells Hibernate to make a table out of this class
@Getter
@Setter
@Table(name = "categorie")
public class Categorie {//implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private Long prix;

    public Categorie (String name, Long prix){
        this.name = name;
        this.prix = prix;
    }

    // Côté inverse : lecture seule. Pas de cascade : supprimer une catégorie ne doit pas supprimer ses produits.
    @OneToMany(mappedBy = "categorie")
    private List<Produit> produits = new ArrayList<>();

    public void addProduit(Produit produit) {
        produits.add(produit);
        produit.setCategorie(this);
    }
}
