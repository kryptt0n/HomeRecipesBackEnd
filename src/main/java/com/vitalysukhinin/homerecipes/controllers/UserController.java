package com.vitalysukhinin.homerecipes.controllers;

import com.vitalysukhinin.homerecipes.entities.Dish;
import com.vitalysukhinin.homerecipes.entities.User;
import com.vitalysukhinin.homerecipes.entities.UserRequest;
import com.vitalysukhinin.homerecipes.entities.UserResponse;
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

    @GetMapping
    public ResponseEntity<List<User>> allUsers() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> {
            user.setPassword(null);
            user.setEmail(null);
        });
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserRequest user) {
        User savedUser = new User(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail()
        );
        userRepository.save(savedUser);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedUser.getId()).toUri()).build();
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> userByUsername(@PathVariable String username) {
        Optional<User> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            User user = found.get();
            UserResponse userResponse = new UserResponse(user.getId(), username);
            return new ResponseEntity<>(userResponse, HttpStatus.OK);
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
