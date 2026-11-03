package com.stockapp.demo.controller;

import com.stockapp.demo.dto.request.TransfertRequest;
import com.stockapp.demo.service.interfaces.StockService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(name = "Stock", description = "Gestion du stock d'entrepôt")
@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/transfert")
    public ResponseEntity<Void> transferer(@Valid @RequestBody TransfertRequest request) {
        stockService.transferer(request.produitId(), request.sourceId(),
                request.destinationId(), request.quantity());
        return ResponseEntity.noContent().build();
    }
}