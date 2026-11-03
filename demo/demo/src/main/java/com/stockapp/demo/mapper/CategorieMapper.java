package com.stockapp.demo.mapper;

import com.stockapp.demo.dto.response.MappedCategorieDetailResponse;
import com.stockapp.demo.dto.request.CategorieRequest;
import com.stockapp.demo.dto.response.MappedCategorieResponse;
import com.stockapp.demo.model.Categorie;
import com.stockapp.demo.model.Produit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CategorieMapper {

    //@Mapping(source = "name", target = "nom") //if I wanted to keep the field "nom" in CategorieResponse
    MappedCategorieResponse toResponse(Categorie categorie);

    MappedCategorieDetailResponse toDetail(Categorie categorie);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produits", ignore = true)
    //@Mapping(target = "name", ignore = true)
    Categorie toEntity(CategorieRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produits", ignore = true)
    void updateEntity(CategorieRequest request, @MappingTarget Categorie categorie);

    // utilisée automatiquement pour convertir List<Produit> en List<String> dans toDetail
    default List<String> skus(List<Produit> produits) {
        return produits.stream().map(Produit::getSku).toList();
    }
}