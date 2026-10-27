package com.stockapp.demo.exception;

public class DuplicateCategorieException extends RuntimeException {

    public DuplicateCategorieException(String name) {

        super("Cette categorie avec le nom " + name + " existe déjà");
    }
}