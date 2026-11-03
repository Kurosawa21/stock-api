package com.stockapp.demo.service;

import com.stockapp.demo.exception.InvalidRequestException;
import com.stockapp.demo.exception.ResourceNotFoundException;
import com.stockapp.demo.model.StockEntrepot;
import com.stockapp.demo.repository.StockEntrepotRepository;
import com.stockapp.demo.service.interfaces.StockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockServiceImpl implements StockService {

    private final StockEntrepotRepository stockRepository;

    public StockServiceImpl(StockEntrepotRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    @Transactional
    public void transferer(Long produitId, Long sourceId, Long destinationId, int quantity) {
        if (sourceId.equals(destinationId)) {
            throw new InvalidRequestException("L'entrepôt source et l'entrepôt destination sont identiques");
        }

        StockEntrepot source = stockRepository.findByProduitIdAndEntrepotId(produitId, sourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock source", sourceId));
        StockEntrepot destination = stockRepository.findByProduitIdAndEntrepotId(produitId, destinationId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock destination", destinationId));

        if (source.getQuantity() < quantity) {
            throw new InvalidRequestException("Stock insuffisant : " + source.getQuantity()
                    + " disponible(s), " + quantity + " demandé(s)");
        }

        source.setQuantity(source.getQuantity() - quantity);
        stockRepository.save(source);

        // EXPÉRIENCE (étapes 4.5 et 4.6) : décommente cette ligne pour simuler une panne
        // if (true) throw new IllegalStateException("panne simulée");

        destination.setQuantity(destination.getQuantity() + quantity);
        stockRepository.save(destination);
    }
}