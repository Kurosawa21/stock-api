package com.stockapp.demo.dto;

import com.stockapp.demo.model.Categorie;

public record CategorieResponse(Long id, String nom) {

    public static CategorieResponse from(Categorie categorie) {
        return new CategorieResponse(categorie.getId(), categorie.getName());
    }
}