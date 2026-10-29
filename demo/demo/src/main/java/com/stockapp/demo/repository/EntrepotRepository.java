package com.stockapp.demo.repository;

import com.stockapp.demo.model.Entrepot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntrepotRepository extends JpaRepository<Entrepot, Long> {
}