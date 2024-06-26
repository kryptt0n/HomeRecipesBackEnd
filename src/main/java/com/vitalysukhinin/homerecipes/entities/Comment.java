package com.vitalysukhinin.homerecipes.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer id;

    @ManyToOne(targetEntity = Dish.class, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id")
    private Dish dish;


    @ManyToOne(targetEntity = User.class, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "username")
    private User user;

    @Column(length = 2000)
    private String comment;

    @Column(name = "date_posted", columnDefinition = "TIMESTAMP")
    private LocalDateTime postedDate;

    public Comment(Integer id, Dish dish, User user, String comment, LocalDateTime postedDate) {
        this.id = id;
        this.dish = dish;
        this.user = user;
        this.comment = comment;
        this.postedDate = postedDate;
    }

    public Comment() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(LocalDateTime postedDate) {
        this.postedDate = postedDate;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", dish=" + dish +
                ", user=" + user +
                ", comment='" + comment + '\'' +
                ", postedDate=" + postedDate +
                '}';
    }
}
