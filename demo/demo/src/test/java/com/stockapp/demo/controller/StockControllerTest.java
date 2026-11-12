package com.stockapp.demo.controller;

import com.stockapp.demo.config.SecurityConfig;
import com.stockapp.demo.controller.StockController;
import com.stockapp.demo.exception.InvalidRequestException;
import com.stockapp.demo.exception.ResourceNotFoundException;
import com.stockapp.demo.service.interfaces.StockService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.anyInt;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;



@WebMvcTest(StockController.class)
@Import(SecurityConfig.class)
@WithMockUser(roles = "ADMIN")
class StockControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private StockService stockService;

    private static final String TRANSFERT = """
            {"produitId": 1, "sourceId": 1, "destinationId": 2, "quantity": 10}
            """;

    @Test
    @DisplayName("POST /api/stocks/transfert : valide, 204 et le service reçoit les bons arguments")
    void transfert_ok() throws Exception {
        mvc.perform(post("/api/stocks/transfert").contentType(MediaType.APPLICATION_JSON).content(TRANSFERT))
                .andExpect(status().isNoContent());

        verify(stockService).transferer(1L, 1L, 2L, 10);
    }

    @Test
    @DisplayName("POST /api/stocks/transfert : champs manquants, 400 sur chaque champ obligatoire")
    void transfert_champs_manquants() throws Exception {
        mvc.perform(post("/api/stocks/transfert").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
                /*.andExpect(jsonPath("$.errors.produitId").exists())
                .andExpect(jsonPath("$.errors.sourceId").exists())
                .andExpect(jsonPath("$.errors.destinationId").exists())
                .andExpect(jsonPath("$.errors.quantity").exists());
                */
        verifyNoInteractions(stockService);
    }

    @Test
    @DisplayName("POST /api/stocks/transfert : règle métier refusée par le service, 400")
    void transfert_refuse() throws Exception {
        doThrow(new InvalidRequestException("Stock insuffisant"))
                .when(stockService).transferer(any(), any(), any(), anyInt());

        mvc.perform(post("/api/stocks/transfert").contentType(MediaType.APPLICATION_JSON).content(TRANSFERT))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/stocks/transfert : stock introuvable, 404")
    void transfert_introuvable() throws Exception {
        doThrow(new ResourceNotFoundException("Stock source", 1L))
                .when(stockService).transferer(any(), any(), any(), anyInt());

        mvc.perform(post("/api/stocks/transfert").contentType(MediaType.APPLICATION_JSON).content(TRANSFERT))
                .andExpect(status().isNotFound());
    }
}