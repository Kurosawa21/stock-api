package com.stockapp.demo.controller;

import com.stockapp.demo.dto.response.PageResponse;
import com.stockapp.demo.dto.request.ProduitRequest;
import com.stockapp.demo.dto.response.ProduitResponse;
import com.stockapp.demo.service.interfaces.ProduitService;
import com.stockapp.demo.util.TriAutorise;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping("/api/produits")
public class ProduitController {

    private static final Set<String> TRIS_AUTORISES = Set.of("id", "sku", "name", "price");

    private final ProduitService produitService;

    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    @GetMapping
    public PageResponse<ProduitResponse> lister(
            @ParameterObject @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        TriAutorise.verifier(pageable, TRIS_AUTORISES);
        return PageResponse.from(produitService.lister(pageable));
    }

    @GetMapping("/{id}")
    public ProduitResponse obtenir(@PathVariable Long id) {
        return produitService.obtenir(id);
    }

    @PostMapping
    public ResponseEntity<ProduitResponse> creer(@Valid @RequestBody ProduitRequest request) {
        ProduitResponse cree = produitService.creer(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(cree.id()).toUri();
        return ResponseEntity.created(location).body(cree);
    }

    @PutMapping("/{id}")
    public ProduitResponse modifier(@PathVariable Long id, @Valid @RequestBody ProduitRequest request) {
        return produitService.modifier(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        produitService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}