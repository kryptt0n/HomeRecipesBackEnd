package com.vitalysukhinin.homerecipes.controllers;

import com.vitalysukhinin.homerecipes.entities.Dish;
import com.vitalysukhinin.homerecipes.entities.User;
import com.vitalysukhinin.homerecipes.repositories.DishRepository;
import com.vitalysukhinin.homerecipes.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    @Autowired
    DishRepository dishRepository;

    @Autowired
    UserRepository userRepository;

    public UserController(DishRepository dishRepository, UserRepository userRepository) {
        this.dishRepository = dishRepository;
        this.userRepository = userRepository;
    }

    @GetMapping()
    public ResponseEntity<List<User>> allUsers() {
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> userById(@PathVariable Integer id) {
        Optional<User> found = userRepository.findById(id);
        if (found.isPresent()) {
            return new ResponseEntity<>(found.get(), HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{username}/dishes")
    public ResponseEntity<List<Dish>> allUserDishes(@PathVariable String username) {
        Optional<User> currentUser = userRepository.findByUsername(username);
        if (currentUser.isPresent())
            return new ResponseEntity<>(dishRepository.findAllByUser(currentUser.get()), HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{id}/dishes")
    public ResponseEntity<Dish> addUserDish(@PathVariable Integer id, @RequestBody Dish dish) {
        Optional<User> currentUser = userRepository.findById(id);
        if (currentUser.isPresent()) {
            dish.setUser(currentUser.get());
            dish.setId(null);
            dishRepository.save(dish);
            return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(dish.getId()).toUri()).build();
        }
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}/dishes")
    public ResponseEntity<Dish> putUserDish(@PathVariable Integer id, @RequestBody Dish dish) {
        dishRepository.save(dish);
        return ResponseEntity.ok(dish);
    }
}
