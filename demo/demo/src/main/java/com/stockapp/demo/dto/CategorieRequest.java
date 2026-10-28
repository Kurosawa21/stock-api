package com.stockapp.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record CategorieRequest(
        @NotBlank(message = "Le nom est obligatoire")
        @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
        @Schema(description = "Nom unique de la catégorie", example = "cat-ticket-3108KJL")
        String name,
        @NotBlank(message = "Le prix est obligatoire")
        @Size(min = 2, max = 50, message = "Le prix est de minimum 10")
        @Schema(description = "Prix unique de la catégorie", example = "1000")
        Long prix
) {}