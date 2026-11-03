package com.stockapp.demo.dto.response;

import java.math.BigDecimal;

public record ProduitResponse(Long id, String sku, String name, String description,
                              BigDecimal price, Long categorieId, long totalStock) {}