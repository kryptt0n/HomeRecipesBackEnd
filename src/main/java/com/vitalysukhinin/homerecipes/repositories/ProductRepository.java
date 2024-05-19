package com.vitalysukhinin.homerecipes.repositories;

import com.vitalysukhinin.homerecipes.entities.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer> {
    public Product findByName(String name);
}