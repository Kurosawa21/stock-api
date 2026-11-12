package com.stockapp.demo.service;

import com.stockapp.demo.exception.InvalidRequestException;
import com.stockapp.demo.exception.ResourceNotFoundException;
import com.stockapp.demo.model.StockEntrepot;
import com.stockapp.demo.repository.StockEntrepotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private StockEntrepotRepository stockRepository;

    @InjectMocks
    private StockServiceImpl service;

    private StockEntrepot source;
    private StockEntrepot destination;

    @BeforeEach
    void init() {
        source = stock(50);
        destination = stock(5);
    }

    private static StockEntrepot stock(int quantity) {
        StockEntrepot s = new StockEntrepot();
        s.setQuantity(quantity);
        return s;
    }

    @Test
    @DisplayName("un transfert valide débite la source et crédite la destination")
    void transfert_valide() {
        when(stockRepository.findByProduitIdAndEntrepotId(1L, 1L)).thenReturn(Optional.of(source));
        when(stockRepository.findByProduitIdAndEntrepotId(1L, 2L)).thenReturn(Optional.of(destination));

        service.transferer(1L, 1L, 2L, 10);

        assertThat(source.getQuantity()).isEqualTo(40);
        assertThat(destination.getQuantity()).isEqualTo(15);
    }

    @ParameterizedTest(name = "transfert de {0} : source {1}, destination {2}")
    @CsvSource({"1,49,6", "50,0,55"})
    @DisplayName("les valeurs limites sont acceptées (1 unité, tout le stock)")
    void transfert_valeurs_limites(int quantity, int attenduSource, int attenduDestination) {
        when(stockRepository.findByProduitIdAndEntrepotId(1L, 1L)).thenReturn(Optional.of(source));
        when(stockRepository.findByProduitIdAndEntrepotId(1L, 2L)).thenReturn(Optional.of(destination));

        service.transferer(1L, 1L, 2L, quantity);

        assertThat(source.getQuantity()).isEqualTo(attenduSource);
        assertThat(destination.getQuantity()).isEqualTo(attenduDestination);
    }

    @Test
    @DisplayName("source et destination identiques : refusé avant tout accès à la base")
    void meme_entrepot() {
        assertThatThrownBy(() -> service.transferer(1L, 3L, 3L, 5))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("identiques");

        verifyNoInteractions(stockRepository);
    }

    @Test
    @DisplayName("stock source introuvable : 404 et rien d'enregistré")
    void source_introuvable() {
        when(stockRepository.findByProduitIdAndEntrepotId(1L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.transferer(1L, 1L, 2L, 5))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(stockRepository, never()).save(any());
    }

    @Test
    @DisplayName("stock destination introuvable : la source n'est pas débitée")
    void destination_introuvable() {
        when(stockRepository.findByProduitIdAndEntrepotId(1L, 1L)).thenReturn(Optional.of(source));
        when(stockRepository.findByProduitIdAndEntrepotId(1L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.transferer(1L, 1L, 2L, 5))
                .isInstanceOf(ResourceNotFoundException.class);

        assertThat(source.getQuantity()).isEqualTo(50);
        verify(stockRepository, never()).save(any());
    }

    @Test
    @DisplayName("stock insuffisant : refusé, aucune quantité modifiée")
    void stock_insuffisant() {
        when(stockRepository.findByProduitIdAndEntrepotId(1L, 1L)).thenReturn(Optional.of(source));
        when(stockRepository.findByProduitIdAndEntrepotId(1L, 2L)).thenReturn(Optional.of(destination));

        assertThatThrownBy(() -> service.transferer(1L, 1L, 2L, 51))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Stock insuffisant");

        assertThat(source.getQuantity()).isEqualTo(50);
        assertThat(destination.getQuantity()).isEqualTo(5);
        verify(stockRepository, never()).save(any());
    }
}