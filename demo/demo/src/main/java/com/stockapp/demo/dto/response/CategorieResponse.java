package com.stockapp.demo.dto.response;

import com.stockapp.demo.model.Categorie;

public record CategorieResponse(Long id, String name, Long prix) {

    public static CategorieResponse from(Categorie categorie) {
        return new CategorieResponse(categorie.getId(), categorie.getName(), categorie.getPrix());
    }
}