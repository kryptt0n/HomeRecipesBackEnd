package com.vitalysukhinin.homerecipes.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitalysukhinin.homerecipes.entities.*;
import com.vitalysukhinin.homerecipes.repositories.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
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

    @Autowired
    StepRepository stepRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;

    private static final String UPLOAD_DIR = "images/";

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

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> dishImage(@PathVariable Integer id) throws MalformedURLException {
        Optional<Dish> found = dishRepository.findById(id);
        if (found.isPresent()) {
            Dish dish = found.get();
//            Path path = Paths.get("/home/ubuntu/" + dish.getImageUrl());
            Path path = Paths.get(System.getProperty("user.dir")).resolve(dish.getImageUrl());
//            System.out.println("Resolved path: " + path.toAbsolutePath());
            if (Files.exists(path)) {
                Resource resource = new UrlResource(path.toUri());
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/steps")
    public ResponseEntity<List<Steps>> stepsForDish(@PathVariable Integer id) {
        Optional<Dish> found = dishRepository.findById(id);
        if (found.isPresent()) {
            return new ResponseEntity<>(found.get().getSteps(), HttpStatus.OK);
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

    @GetMapping("/{id}/rating")
    public ResponseEntity<String> ratingForDish(@PathVariable Integer id) {
        Double rating = dishRepository.findAverageRating(id);
        if (rating != null) {
            return ResponseEntity.ok(new DecimalFormat("#.#")
                    .format(dishRepository.findAverageRating(id)));
        } else {
            return ResponseEntity.ok("0");
        }
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<Comment>> commentsForDish(@PathVariable Integer id) {
        Optional<Dish> dish = dishRepository.findById(id);
        if (dish.isPresent()) {
            return ResponseEntity.ok(commentRepository.findAllByDish(dish.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/comments")
    public ResponseEntity<Comment> addCommentForDish(@RequestBody Comment comment) {
        Optional<Dish> dish = dishRepository.findById(comment.getDish().getId());
        if (dish.isPresent()) {
            comment.setDish(dish.get());
            System.out.println(comment.getUser());

            if (comment.getUser() != null && comment.getUser().getUsername() == null) {
                return ResponseEntity.badRequest().build();
            }
            Optional<User> user = userRepository.findById(comment.getUser().getUsername());

            if (user.isPresent()) {
                comment.setUser(user.get());
                Comment savedComment = commentRepository.save(comment);
                URI uri = URI.create(ServletUriComponentsBuilder
                        .fromCurrentContextPath().path("/comments/" + savedComment.getId())
                        .toUriString());
                return ResponseEntity.created(uri).build();
            } else {
                return ResponseEntity.badRequest().build();
            }

        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Dish> addDish(@RequestParam("dish") String dishJson, @RequestParam("image") MultipartFile imageFile) {
        try {

            Dish dish = new ObjectMapper().readValue(dishJson, Dish.class);
            Dish saved = dishRepository.save(dish);
            if (!imageFile.isEmpty()) {
                byte[] bytes = imageFile.getBytes();
//                Path path = Paths.get("/home/ubuntu/" + UPLOAD_DIR + "dish" + saved.getId());
                Path path = Paths.get(UPLOAD_DIR + "dish" + saved.getId());
                System.out.println(path);
                Files.write(path, bytes);
                System.out.println("Files were written");
            }

            List<Steps> steps = dish.getSteps();
            for (int i = 0; i < steps.size(); i++) {
                Steps step = steps.get(i);
                step.setDish(saved);
                step = entityManager.merge(step);
                steps.set(i, step);
            }
            saved.setImageUrl(UPLOAD_DIR + "dish" + saved.getId());
            dishRepository.save(saved);

            return new ResponseEntity<>(new Dish(), HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    @Transactional
    public ResponseEntity<Dish> updateDish(@RequestParam("dish") String dishJson, @RequestParam("image") MultipartFile imageFile) throws IOException {
        Dish dish = new ObjectMapper().readValue(dishJson, Dish.class);
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

        List<Steps> steps = dish.getSteps();
        for (int i = 0; i < steps.size(); i++) {
            Steps step = steps.get(i);
            if (step.getStepsId() == null) {
                // New step
                step.setDish(dish);
                stepRepository.save(step);
            } else {
                // Existing step
                step = entityManager.merge(step);
                steps.set(i, step);
            }
        }

        if (!imageFile.isEmpty()) {
            byte[] bytes = imageFile.getBytes();
//                Path path = Paths.get("/home/ubuntu/" + UPLOAD_DIR + "dish" + dish.getId());
            Path path = Paths.get(UPLOAD_DIR + "dish" + dish.getId());
            System.out.println(path);
            Files.write(path, bytes);
            System.out.println("Files were written");
            dish.setImageUrl(UPLOAD_DIR + "dish" + dish.getId());
        }

        dish.setProducts(products);
        dish.setSteps(steps);
        Dish saved = entityManager.merge(dish);
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Dish> deleteDish(@PathVariable Integer id) {
        dishRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
