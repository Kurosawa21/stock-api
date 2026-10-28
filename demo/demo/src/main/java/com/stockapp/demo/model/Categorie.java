package com.stockapp.demo.model;

import java.io.Serializable;
import java.util.List;
import jakarta.persistence.*;
import jakarta.persistence.Column;

//import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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

    @Column(nullable = false, length = 11)
    private Long prix;

    public Categorie (String name, Long prix){
        this.name = name;
        this.prix = prix;
    }
}