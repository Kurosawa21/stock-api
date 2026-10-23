
package com.stockapp.demo.service;

import com.stockapp.demo.model.*;
import java.util.List;

/**
 *
 * @author TwAw
 */
public interface CategorieService {

    public Categorie upsert(Categorie categorie);

    public Categorie getById(Long id);

    public List<Categorie> getAllCategorie();

    public String deleteById(Long id);

}
