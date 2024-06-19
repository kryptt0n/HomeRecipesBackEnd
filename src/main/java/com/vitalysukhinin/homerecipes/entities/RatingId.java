package com.vitalysukhinin.homerecipes.entities;


import java.io.Serializable;
import java.util.Objects;

public class RatingId implements Serializable {
    private Integer dishId;

    private User user;

    public RatingId(Integer dishId, User user) {
        this.dishId = dishId;
        this.user = user;
    }

    public RatingId() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RatingId ratingId = (RatingId) o;
        return dishId.equals(ratingId.dishId) && user.equals(ratingId.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dishId, user);
    }


}
