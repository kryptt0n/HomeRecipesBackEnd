package com.vitalysukhinin.homerecipes.controllers;

import com.vitalysukhinin.homerecipes.entities.Product;
import com.vitalysukhinin.homerecipes.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<Iterable<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @GetMapping("/{name}")
    public ResponseEntity<Product> getProductByName(@PathVariable String name) {
        Product found = productRepository.findByName(name);
        HttpHeaders responseHeaders = new HttpHeaders();
        if (found != null) {
            return ResponseEntity.ok().headers(responseHeaders).body(found);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer id) {
        Optional<Product> found = productRepository.findById(id);
        HttpHeaders responseHeaders = new HttpHeaders();
        if (found.isPresent()) {
            return ResponseEntity.ok(found.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
//    @CrossOrigin
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product savedProduct = productRepository.save(product);
        HttpHeaders responseHeaders = new HttpHeaders();
        URI uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/products/" + savedProduct.getId())
                .toUriString());
        return ResponseEntity.ok().headers(responseHeaders).body(savedProduct);
    }

}