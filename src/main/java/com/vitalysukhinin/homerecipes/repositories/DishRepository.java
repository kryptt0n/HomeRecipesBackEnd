package com.vitalysukhinin.homerecipes.repositories;

import com.vitalysukhinin.homerecipes.entities.Dish;
import com.vitalysukhinin.homerecipes.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Integer> {
    List<Dish> findAllByUser(User user);

    @Query(value = "SELECT get_average_rating(?1)", nativeQuery = true)
    Double findAverageRating(Integer dishId);
}
