package com.stockapp.demo.service.interfaces;

public interface StockService {

    void transferer(Long produitId, Long sourceId, Long destinationId, int quantity);
}