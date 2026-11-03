package com.stockapp.demo.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProduitRequest(
        @NotBlank @Pattern(regexp = "[A-Z]{3}-\\d{4}", message = "format attendu : ABC-1234") String sku,
        @NotBlank @Size(min = 2, max = 100) String name,
        @Size(max = 500) String description,
        @NotNull @Positive BigDecimal price,
        @NotNull Long categorieId) {}