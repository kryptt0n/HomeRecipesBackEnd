package com.vitalysukhinin.homerecipes.repositories;

import com.vitalysukhinin.homerecipes.entities.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {

}
