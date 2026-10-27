package com.stockapp.demo.repository;

import com.stockapp.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.Optional;


public interface CategorieRepository extends JpaRepository<Categorie, Long> {

    Optional<Categorie> findByName(String name);

    //Optional<Categorie> findById(Long id);
}
