/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.stockapp.demo.service;

import com.stockapp.demo.dto.request.CategorieRequest;
import com.stockapp.demo.dto.response.CategorieDetailResponse;
import com.stockapp.demo.dto.response.CategorieResponse;
import com.stockapp.demo.dto.response.MappedCategorieDetailResponse;
import com.stockapp.demo.dto.response.MappedCategorieResponse;
import com.stockapp.demo.exception.DuplicateCategorieException;
import com.stockapp.demo.exception.ResourceNotFoundException;
import com.stockapp.demo.model.*;
import com.stockapp.demo.repository.*;
import com.stockapp.demo.mapper.CategorieMapper;

import java.util.List;
import java.util.Optional;

import com.stockapp.demo.service.interfaces.CategorieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author TwAw
 */
@Service
@Profile("dev")
public class CategorieServiceImpl implements CategorieService {

    //@Autowired //not recommanded
    //private CategorieRepository categorieRepository;

    //recommanded
    private final CategorieRepository categorieRepository;
    private final CategorieMapper mapper;
    public CategorieServiceImpl(CategorieRepository categorieRepository, CategorieMapper mapper) {
        this.categorieRepository = categorieRepository;
        this.mapper = mapper;
    }
    
    @Override
    @Transactional
    public Categorie upsert(Categorie categorie) {
        categorie = categorieRepository.save(categorie);
        return categorie;
    }

    // Exemple d'écriture : @Transactional sur la méthode, retour d'un DTO
   /* @Override
    @Transactional
    public MappedCategorieResponse creer(CategorieRequest request) {
        // ta vérification de doublon existante reste ici, inchangée
        return mapper.toResponse(categorieRepository.save(mapper.toEntity(request)));
    }*/
    @Override
    @Transactional
    public MappedCategorieResponse creer(CategorieRequest request) {
        if (categorieRepository.findByName(request.name()).isPresent()) {
            throw new DuplicateCategorieException(request.name());   // adapte au constructeur réel
        }
        return mapper.toResponse(categorieRepository.save(mapper.toEntity(request)));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MappedCategorieDetailResponse> listerAvecProduitsPageable(Pageable pageable) {
        return categorieRepository.findAll(pageable).map(mapper::toDetail);   // remplace versDetail()
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Categorie> getAllPageableCategorie(Pageable pageable) {//DEPRECATED: PROHIBITED X
        return categorieRepository.findAll(pageable);
    }


    @Transactional(readOnly = true)
    @Override
    public Page<MappedCategorieResponse> getAllPageableCategorieMapped(Pageable pageable) {//got to return only the DTO, NEVER the entity
        return categorieRepository.findAll(pageable).map(mapper::toResponse);
    }

    /*@Transactional(readOnly = true)
    @Override
    public Categorie getById(Long id) {
        Optional<Categorie> findById = categorieRepository.findById(id);
        if(findById.isPresent())
            return findById.get();
        return null;
    }*/

    @Transactional(readOnly = true)
    @Override
    public Categorie getById(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie", id));
    }

    @Transactional(readOnly = true)
    @Override
    public Categorie getByName(String name) {
        Optional<Categorie> findByName = categorieRepository.findByName(name);
        if(findByName.isPresent())
            return findByName.get();
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Categorie> getAllCategorie() {
        return categorieRepository.findAll();
    }


    @Transactional(readOnly = true)
    @Override
    public Page<CategorieResponse> getAllPageableSpecCategorie(Specification<Categorie> specification, Pageable pageable) {
        return categorieRepository.findAll(specification, pageable).map(CategorieResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategorieDetailResponse> listerAvecProduitsPagine(Pageable pageable) {
        // 1) page de catégories seules, 2) versDetail() déclenche le chargement par lots des produits
        return categorieRepository.findAll(pageable).map(CategorieDetailResponse::versDetail);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategorieDetailResponse> listerAvecProduitsNaif() {//A EVITER
        // findAll() ne charge PAS les produits : chaque getProduits() lancera une requête
        return categorieRepository.findAll().stream()
                .map(CategorieDetailResponse::versDetail)
                .toList();
    }


    @Transactional(readOnly = true)
    @Override
    public List<CategorieDetailResponse> listerAvecProduits() {//RECOMMANDE POUR LES JOINTURES
        // une seule requête avec jointure
        return categorieRepository.findAllWithProduits().stream()
                .map(CategorieDetailResponse::versDetail)
                .toList();
    }

    /*@Override
    public String deleteById(Long id) {
        if(categorieRepository.existsById(id)){
            categorieRepository.deleteById(id);
            return "deleted!";
        }
        return "not deleted...";
    }*/

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!categorieRepository.existsById(id)) {
            throw new ResourceNotFoundException("Catégorie", id);
        }
        categorieRepository.deleteById(id);
    }
    
}
