package com.vitalysukhinin.HomeRecipes.controllers;

import com.example.demo.entities.Dish;
import com.example.demo.entities.User;
import com.example.demo.repositories.DishRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}/dishes")
    public ResponseEntity<List<Dish>> allUserDishes(@PathVariable Integer id) {
        Optional<User> currentUser = userRepository.findById(id);
        if (currentUser.isPresent())
            return new ResponseEntity<>(dishRepository.findAllByUser(currentUser.get()), HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
}
