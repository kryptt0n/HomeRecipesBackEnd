package com.vitalysukhinin.HomeRecipes.repositories;

import com.example.demo.entities.Dish;
import com.example.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Integer> {
    List<Dish> findAllByUser(User user);
}
