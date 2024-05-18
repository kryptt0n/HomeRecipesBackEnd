package com.vitalysukhinin.HomeRecipes.repositories;

import com.example.demo.entities.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer> {
    public Product findByName(String name);
}