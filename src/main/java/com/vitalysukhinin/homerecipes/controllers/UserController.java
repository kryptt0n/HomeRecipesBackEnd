package com.vitalysukhinin.homerecipes.controllers;

import com.vitalysukhinin.homerecipes.entities.*;
import com.vitalysukhinin.homerecipes.repositories.AuthorityRepository;
import com.vitalysukhinin.homerecipes.repositories.DishRepository;
import com.vitalysukhinin.homerecipes.repositories.RatingRepository;
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

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    RatingRepository ratingRepository;

    public UserController(DishRepository dishRepository, UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.dishRepository = dishRepository;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
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
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.isEnabled()
        );

        Authority authority = new Authority(user.getUsername(), "ROLE_USER");
        userRepository.save(savedUser);
        authorityRepository.save(authority);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedUser.getUsername()).toUri()).build();
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> userByUsername(@PathVariable String username) {
        Optional<User> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            User user = found.get();
            UserResponse userResponse = new UserResponse(username);
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

    @GetMapping("/{username}/ratings/{id}")
    public ResponseEntity<Integer> ratingForDish(@PathVariable String username, @PathVariable Integer id) {
        Optional<User> currentUser = userRepository.findByUsername(username);
        if (currentUser.isPresent()) {
            Optional<Rating> rating = ratingRepository.findRatingByDishIdAndUser(id, currentUser.get());
            return ResponseEntity.ok(rating.isPresent() ? rating.get().getRating() : 0);
        }
        else
            return ResponseEntity.ok(0);
    }

    @PostMapping("/ratings")
    public ResponseEntity<Rating> addDishRating(@RequestBody Rating rating) {
        Optional<User> user = userRepository.findByUsername(rating.getUser().getUsername());
        if (user.isPresent())
            rating.setUser(user.get());
        Rating saved = ratingRepository.save(rating);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getDishId()).toUri()).build();
    }

    @PostMapping("/{username}/dishes")
    public ResponseEntity<Dish> addUserDish(@PathVariable String username, @RequestBody Dish dish) {
        Optional<User> currentUser = userRepository.findById(username);
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
