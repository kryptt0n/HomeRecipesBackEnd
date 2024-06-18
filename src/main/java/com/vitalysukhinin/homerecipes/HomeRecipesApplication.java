package com.vitalysukhinin.homerecipes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableJpaRepositories
@EntityScan(basePackages = "com.vitalysukhinin.homerecipes.entities")
public class HomeRecipesApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomeRecipesApplication.class, args);
	}

}
