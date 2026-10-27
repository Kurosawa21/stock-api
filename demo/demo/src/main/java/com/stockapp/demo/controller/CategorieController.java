package com.stockapp.demo.controller;

import com.stockapp.demo.dto.CategorieRequest;
import com.stockapp.demo.dto.CategorieResponse;
import com.stockapp.demo.service.CategorieService;
import com.stockapp.demo.config.AppProperties;
import com.stockapp.demo.model.Categorie;
import com.stockapp.demo.exception.ResourceNotFoundException;
import com.stockapp.demo.exception.DuplicateCategorieException;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Categorie", description = "Gestion des categorie de stock")
@RestController
@RequestMapping("/api/categories")
public class CategorieController {

    //private final Map<Long, Categorie> categories = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    private final CategorieService categorieService;
    private final AppProperties properties;
    public CategorieController(CategorieService categorieService, AppProperties properties) {

        this.categorieService = categorieService;
        this.properties = properties;
    }

    @GetMapping
    public ResponseEntity<List<CategorieResponse>> lister() {
        String appname = properties.name();
        System.out.println("getting all categorie from Dev for "+appname);

        List<CategorieResponse> allCategories = categorieService.getAllCategorie().stream()
                .sorted(Comparator.comparing(Categorie::getId))
                .map(CategorieResponse::from)
                .toList();
        return new ResponseEntity<>(
                allCategories,
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategorieResponse>  lire(@PathVariable Long id) {

        System.out.println("getting categorie "+id);
        Categorie categorie = categorieService.getById(id);
        if (categorie == null) {
            //return ResponseEntity.notFound().build();
            throw new ResourceNotFoundException("Categorie", id);
        }

        return ResponseEntity.ok(CategorieResponse.from(categorie));
    }

    @Operation(summary = "Créer une catégorie", description = "La catégorie doit être unique.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Catégorie créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Catégorie déjà enregistrée")
    })
    @PostMapping("/add")
    public ResponseEntity<CategorieResponse> creer(@Valid @RequestBody CategorieRequest request) {
        System.out.println("begining categorie adding.. : "+request);

        //boolean skuExiste = produits.values().stream()
        //        .anyMatch(p -> p.sku().equals(request.sku()));
        Categorie categorie = categorieService.getByName(request.name());
        if (categorie != null && categorie.getId() != 0) {
            throw new DuplicateCategorieException(request.name());
        }

        //long id = sequence.incrementAndGet();
        categorie = categorieService.upsert(new Categorie(request.name()));
        //categories.put(id, categorie);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(categorie.getId()).toUri();
        return ResponseEntity.created(location).body(CategorieResponse.from(categorie));
        //return new ResponseEntity<>(categorie, HttpStatus.CREATED);

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CategorieResponse> modifier(@PathVariable Long id, @Valid @RequestBody CategorieRequest request) {
        System.out.println("getting categorie "+id);
        Categorie categorie = categorieService.getById(id);
        if (categorie == null) {
            //return ResponseEntity.notFound().build();
            throw new ResourceNotFoundException("Categorie", id);
        }
        categorie.setName(request.name());
        categorie = categorieService.upsert(categorie);
        return ResponseEntity.ok(CategorieResponse.from(categorie));
    }

    //@ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> supprimer(@PathVariable Long id) {
        // This returns a JSON or XML with the users
        System.out.println("deleting categorie "+id);
        String status = categorieService.deleteById(id);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    /*private Categorie trouver(Long id) {
        Categorie categorie = categories.get(id);
        if (categorie == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Catégorie introuvable : " + id);
        }
        return categorie;
    }*/
}