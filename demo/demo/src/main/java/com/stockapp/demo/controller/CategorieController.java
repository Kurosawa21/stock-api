package com.stockapp.categorie;

import com.stockapp.categorie.dto.CategorieRequest;
import com.stockapp.categorie.dto.CategorieResponse;
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

@RestController
@RequestMapping("/api/categories")
public class CategorieController {

    private final Map<Long, Categorie> categories = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    @GetMapping
    public List<CategorieResponse> lister() {
        return categories.values().stream()
                .sorted(Comparator.comparing(Categorie::id))
                .map(CategorieResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public CategorieResponse lire(@PathVariable Long id) {
        return CategorieResponse.from(trouver(id));
    }

    @PostMapping
    public ResponseEntity<CategorieResponse> creer(@Valid @RequestBody CategorieRequest request) {
        long id = sequence.incrementAndGet();
        Categorie categorie = new Categorie(id, request.nom());
        categories.put(id, categorie);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(location).body(CategorieResponse.from(categorie));
    }

    @PutMapping("/{id}")
    public CategorieResponse modifier(@PathVariable Long id,
                                      @Valid @RequestBody CategorieRequest request) {
        trouver(id);
        Categorie misAJour = new Categorie(id, request.nom());
        categories.put(id, misAJour);
        return CategorieResponse.from(misAJour);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        trouver(id);
        categories.remove(id);
    }

    private Categorie trouver(Long id) {
        Categorie categorie = categories.get(id);
        if (categorie == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Catégorie introuvable : " + id);
        }
        return categorie;
    }
}