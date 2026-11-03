package com.stockapp.demo.exception;

public class DuplicateSkuException extends RuntimeException {

    public DuplicateSkuException(String name) {

        super("Cet SKU avec le nom " + name + " existe déjà");
    }
}