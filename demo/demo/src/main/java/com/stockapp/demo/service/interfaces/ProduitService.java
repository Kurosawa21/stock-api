package com.stockapp.demo.service.interfaces;

import com.stockapp.demo.dto.request.ProduitRequest;
import com.stockapp.demo.dto.response.ProduitResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProduitService {
    Page<ProduitResponse> lister(Pageable pageable);
    ProduitResponse obtenir(Long id);
    ProduitResponse creer(ProduitRequest request);
    ProduitResponse modifier(Long id, ProduitRequest request);
    void supprimer(Long id);
}