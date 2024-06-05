package com.vitalysukhinin.homerecipes.configs;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
                auth.requestMatchers(HttpMethod.POST, "/users").permitAll()
                .requestMatchers(HttpMethod.GET, "/dishes", "/dishes/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated())
        .httpBasic(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        var userDetailsService =
//                new InMemoryUserDetailsManager();
//
//        createUserNoHash("user", "password", userDetailsService);
//        createUserNoHash("test1", "test1", userDetailsService);
//        createUserNoHash("test2", "test2", userDetailsService);
//        createUserSHA256("test5", DigestUtils.sha256Hex("testtest"), userDetailsService);
//        System.out.println(DigestUtils.sha256Hex("testtest"));
//
//        return userDetailsService;
//    }

    private void createUserNoHash(String username, String password, UserDetailsManager manager) {
        var user = User.withUsername(username)
                .password("{noop}" + password)
                .authorities("Test")
                .build();

        manager.createUser(user);
    }

    private void createUserSHA256(String username, String password, UserDetailsManager manager) {
        var user = User.withUsername(username)
                .password("{sha256}" + password)
                .authorities("Test")
                .build();

        manager.createUser(user);
    }

    @Bean
    UserDetailsManager userDetailsManager(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new Sha256PasswordEncoder();
    }


}
