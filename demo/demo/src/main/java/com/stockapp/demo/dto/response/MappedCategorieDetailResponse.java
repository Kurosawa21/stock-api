package com.stockapp.demo.dto.response;

import java.util.List;

public record MappedCategorieDetailResponse(Long id, String name, List<String> produits) {


}