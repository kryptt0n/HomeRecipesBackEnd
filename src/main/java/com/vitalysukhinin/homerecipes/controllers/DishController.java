package com.vitalysukhinin.homerecipes.controllers;

import com.vitalysukhinin.homerecipes.entities.Dish;
import com.vitalysukhinin.homerecipes.repositories.DishRepository;
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

    @GetMapping
    public ResponseEntity<List<Dish>> allDishes() {
        return new ResponseEntity<>(dishRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dish> dishById(@PathVariable Integer id) {
        Optional<Dish> found = dishRepository.findById(id);
        if (found.isPresent()) {
            return new ResponseEntity<>(found.get(), HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Dish> addDish(@RequestBody Dish dish) {
        Dish saved = dishRepository.save(dish);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Dish> deleteDish(@PathVariable Integer id) {
        dishRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
