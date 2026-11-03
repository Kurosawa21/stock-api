package com.stockapp.demo.service;

import com.stockapp.demo.dto.request.ProduitRequest;
import com.stockapp.demo.dto.response.ProduitResponse;
import com.stockapp.demo.exception.DuplicateSkuException;
import com.stockapp.demo.exception.ResourceNotFoundException;
import com.stockapp.demo.mapper.ProduitMapper;
import com.stockapp.demo.model.Categorie;
import com.stockapp.demo.model.Produit;
import com.stockapp.demo.repository.CategorieRepository;
import com.stockapp.demo.repository.ProduitRepository;
import com.stockapp.demo.repository.StockEntrepotRepository;
import com.stockapp.demo.repository.StockEntrepotRepository.StockTotal;
import com.stockapp.demo.service.interfaces.ProduitService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProduitServiceImpl implements ProduitService {

    private final ProduitRepository produitRepository;
    private final CategorieRepository categorieRepository;
    private final StockEntrepotRepository stockRepository;
    private final ProduitMapper mapper;

    public ProduitServiceImpl(ProduitRepository produitRepository, CategorieRepository categorieRepository,
                              StockEntrepotRepository stockRepository, ProduitMapper mapper) {
        this.produitRepository = produitRepository;
        this.categorieRepository = categorieRepository;
        this.stockRepository = stockRepository;
        this.mapper = mapper;
    }

    @Override
    public Page<ProduitResponse> lister(Pageable pageable) {
        Page<Produit> page = produitRepository.findAll(pageable);
        Map<Long, Long> totaux = totaux(page.getContent());          // 1 seule requête pour toute la page
        return page.map(p -> mapper.toResponse(p, totaux.getOrDefault(p.getId(), 0L)));
    }

    @Override
    public ProduitResponse obtenir(Long id) {
        Produit p = trouver(id);
        return mapper.toResponse(p, totaux(List.of(p)).getOrDefault(id, 0L));
    }

    @Override
    @Transactional
    public ProduitResponse creer(ProduitRequest request) {
        if (produitRepository.existsBySku(request.sku())) {
            throw new DuplicateSkuException(request.sku());
        }
        Produit p = mapper.toEntity(request);
        p.setCategorie(trouverCategorie(request.categorieId()));
        return mapper.toResponse(produitRepository.save(p), 0L);   // nouveau produit : aucun stock
    }

    @Override
    @Transactional
    public ProduitResponse modifier(Long id, ProduitRequest request) {
        Produit p = trouver(id);
        if (!p.getSku().equals(request.sku()) && produitRepository.existsBySku(request.sku())) {
            throw new DuplicateSkuException(request.sku());
        }
        mapper.updateEntity(request, p);
        p.setCategorie(trouverCategorie(request.categorieId()));
        return mapper.toResponse(p, totaux(List.of(p)).getOrDefault(id, 0L));
    }

    @Override
    @Transactional
    public void supprimer(Long id) {
        if (!produitRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produit", id);
        }
        produitRepository.deleteById(id);
    }

    private Produit trouver(Long id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", id));
    }

    private Categorie trouverCategorie(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie", id));
    }

    private Map<Long, Long> totaux(List<Produit> produits) {
        if (produits.isEmpty()) {
            return Map.of();
        }
        List<Long> ids = produits.stream().map(Produit::getId).toList();
        return stockRepository.totalsByProduitIds(ids).stream()
                .collect(Collectors.toMap(StockTotal::getProduitId, StockTotal::getTotal));
    }
}