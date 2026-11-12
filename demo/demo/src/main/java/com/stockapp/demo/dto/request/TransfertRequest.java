package com.stockapp.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransfertRequest(
        @NotNull(message = "Le produitId est obligatoire")
        Long produitId,
        @NotNull(message = "La sourceId est obligatoire")
        Long sourceId,
        @NotNull(message = "La destinationId est obligatoire")
        Long destinationId,
        @NotNull(message = "La quantite est obligatoire")
        @Positive int quantity) {}