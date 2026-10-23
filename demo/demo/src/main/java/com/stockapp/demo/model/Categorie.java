package com.stockapp.demo.model;

import java.io.Serializable;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity // This tells Hibernate to make a table out of this class
@Table(name = "categorie")
public class Categorie {//implements Serializable {


    public Categorie (String name){
        this.name = name;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

}