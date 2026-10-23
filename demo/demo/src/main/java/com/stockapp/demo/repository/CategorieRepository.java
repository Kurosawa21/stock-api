package com.stockapp.demo.repository;

import com.stockapp.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;


public interface CategorieRepository extends JpaRepository<Categorie, Long> {

}
