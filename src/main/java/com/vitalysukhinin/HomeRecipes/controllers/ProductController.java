package com.vitalysukhinin.HomeRecipes.controllers;

import com.example.demo.entities.Product;
import com.example.demo.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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

    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product savedProduct = productRepository.save(product);
        HttpHeaders responseHeaders = new HttpHeaders();
        URI uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/products/" + savedProduct.getId())
                .toUriString());
        return ResponseEntity.ok().headers(responseHeaders).body(savedProduct);
    }

}