package com.stockapp.demo.util;

import com.stockapp.demo.exception.InvalidRequestException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class TriAutorise {

    private TriAutorise() {
    }

    public static void verifier(Pageable pageable, Set<String> autorises) {
        for (Sort.Order ordre : pageable.getSort()) {
            if (!autorises.contains(ordre.getProperty())) {
                throw new InvalidRequestException("Tri non autorisé : " + ordre.getProperty()
                        + ". Champs possibles : " + autorises);
            }
        }
    }
}