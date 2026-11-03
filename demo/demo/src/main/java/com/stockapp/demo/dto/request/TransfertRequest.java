package com.stockapp.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransfertRequest(
        @NotNull Long produitId,
        @NotNull Long sourceId,
        @NotNull Long destinationId,
        @Positive int quantity) {}