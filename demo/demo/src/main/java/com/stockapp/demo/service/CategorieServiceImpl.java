/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.stockapp.demo.service;

import com.stockapp.demo.model.*;
import com.stockapp.demo.repository.*;
import com.stockapp.demo.service.*;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author TwAw
 */
@Service
@Profile("dev")
public class CategorieServiceImpl implements CategorieService{

    //@Autowired //not recommanded
    //private CategorieRepository categorieRepository;

    //recommanded
    private final CategorieRepository categorieRepository;
    public CategorieServiceImpl(CategorieRepository categorieRepository) {
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
        Optional<Categorie> findByName = categorieRepository.findByName(name);
        if(findByName.isPresent())
            return findByName.get();
        return null;
    }

    @Override
    public List<Categorie> getAllCategorie() {
        return categorieRepository.findAll();
    }

    @Override
    public Page<Categorie> getAllPageableCategorie(Pageable pageable) {
        return categorieRepository.findAll(pageable);
    }

    @Override
    public Page<Categorie> getAllPageableSpecCategorie(Specification specification, Pageable pageable) {
        return categorieRepository.findAll(specification, pageable);
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
