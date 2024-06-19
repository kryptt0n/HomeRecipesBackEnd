package com.vitalysukhinin.homerecipes.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "ratings")
@IdClass(RatingId.class)
public class Rating {

    @Id
    @Column(name = "dish_id")
    private Integer dishId;

    @Id
    @ManyToOne
    @JoinColumn(name = "username")
    private User user;

    private Integer rating;

    public Rating(Integer dishId, User user, Integer rating) {
        this.dishId = dishId;
        this.user = user;
        this.rating = rating;
    }

    public Rating() {
    }

    public Integer getDishId() {
        return dishId;
    }

    public void setDishId(Integer dishId) {
        this.dishId = dishId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Rating{" +
                "dishId=" + dishId +
                ", user='" + user + '\'' +
                ", rating=" + rating +
                '}';
    }
}
