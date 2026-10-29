package com.stockapp.demo.dto;

import com.stockapp.demo.model.Categorie;
import com.stockapp.demo.model.Produit;

import java.util.List;

public record CategorieDetailResponse(Long id, String name, List<String> produits) {

    public static CategorieDetailResponse versDetail(Categorie c) {
        List<String> skus = c.getProduits().stream().map(Produit::getSku).toList();
        return new CategorieDetailResponse(c.getId(), c.getName(), skus);
    }
}