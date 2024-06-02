package com.vitalysukhinin.homerecipes.controllers;

import com.vitalysukhinin.homerecipes.entities.Dish;
import com.vitalysukhinin.homerecipes.entities.Product;
import com.vitalysukhinin.homerecipes.repositories.DishRepository;
import com.vitalysukhinin.homerecipes.repositories.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dishes")
public class DishController {

    @Autowired
    DishRepository dishRepository;

    @Autowired
    ProductRepository productRepository;

    @PersistenceContext
    EntityManager entityManager;

    @GetMapping
    public ResponseEntity<List<Dish>> allDishes() {
        return new ResponseEntity<>(dishRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dish> dishById(@PathVariable Integer id) {
        Optional<Dish> found = dishRepository.findById(id);
        if (found.isPresent()) {
            Dish dish = found.get();
            return new ResponseEntity<>(found.get(), HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<List<Product>> productsForDish(@PathVariable Integer id) {
        Optional<Dish> found = dishRepository.findById(id);
        if (found.isPresent()) {
            return new ResponseEntity<>(found.get().getProducts(), HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Dish> addDish(@RequestBody Dish dish) {
        Dish saved = dishRepository.save(dish);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<Dish> updateDish(@RequestBody Dish dish) {
        entityManager.detach(dish);

        List<Product> products = dish.getProducts();
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            if (product.getId() == null) {
                // New product
                productRepository.save(product);
            } else {
                // Existing product
                product = entityManager.merge(product);
                products.set(i, product);
            }
        }
        dish.setProducts(products);
        Dish saved = entityManager.merge(dish);
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Dish> deleteDish(@PathVariable Integer id) {
        dishRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
