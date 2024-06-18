package com.vitalysukhinin.homerecipes.repositories;

import com.vitalysukhinin.homerecipes.entities.Steps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StepRepository extends JpaRepository<Steps, Integer> {
}