package com.stockapp.demo.mapper;

import com.stockapp.demo.dto.request.ProduitRequest;
import com.stockapp.demo.dto.response.ProduitResponse;
import com.stockapp.demo.model.Produit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ProduitMapper {

    // deux sources : l'entité et le stock total calculé par le service
    @Mapping(target = "categorieId", source = "produit.categorie.id")
    @Mapping(target = "totalStock", source = "totalStock")
    ProduitResponse toResponse(Produit produit, long totalStock);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categorie", ignore = true)       // rattachée par le service
    Produit toEntity(ProduitRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categorie", ignore = true)
    void updateEntity(ProduitRequest request, @MappingTarget Produit produit);
}