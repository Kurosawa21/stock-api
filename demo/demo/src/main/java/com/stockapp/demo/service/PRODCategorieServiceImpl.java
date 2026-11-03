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
import com.stockapp.demo.model.*;
import com.stockapp.demo.repository.*;

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
@Profile("prod")
public class PRODCategorieServiceImpl implements CategorieService {

    //@Autowired //not recommanded
    //private CategorieRepository categorieRepository;

    //recommanded
    private final CategorieRepository categorieRepository;
    public PRODCategorieServiceImpl(CategorieRepository categorieRepository) {
        this.categorieRepository = categorieRepository;
    }
    
    @Override
    public Categorie upsert(Categorie categorie) {
        categorie = categorieRepository.save(categorie);
        return categorie;
    }

    @Override
    public Categorie getById(Long id) {
        Optional<Categorie> findById = categorieRepository.findById(id);
        if(findById.isPresent())
            return findById.get();
        return null;
    }

    @Override
    public Categorie getByName(String name) {
        return categorieRepository.findByName(name).orElse(null);
    }

    @Override
    public List<Categorie> getAllCategorie() {
        System.out.println("getting categories from PROD");
        return categorieRepository.findAll();
    }

    @Override
    public Page<Categorie> getAllPageableCategorie(Pageable pageable) {
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CategorieResponse> getAllPageableSpecCategorie(Specification specification, Pageable pageable) {
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategorieDetailResponse> listerAvecProduits() {
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategorieDetailResponse> listerAvecProduitsNaif() {
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CategorieDetailResponse> listerAvecProduitsPagine(Pageable pageable){
        return null;
    }

    @Override
    @Transactional
    public MappedCategorieResponse creer(CategorieRequest request) {
        // ta vérification de doublon existante reste ici, inchangée
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MappedCategorieDetailResponse> listerAvecProduitsPageable(Pageable pageable) {
        return null;
    }

    @Override
    public Page<MappedCategorieResponse> getAllPageableCategorieMapped(Pageable pageable) {
        return null; // À implémenter plus tard
    }


    @Override
    public String deleteById(Long id) {
        if(categorieRepository.existsById(id)){
            categorieRepository.deleteById(id);
            return "deleted!";
        }
        return "not deleted...";
    }
    
}
