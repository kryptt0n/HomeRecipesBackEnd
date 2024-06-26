package com.vitalysukhinin.homerecipes.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "stepsId")
public class Steps {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "steps_id")
    private Integer stepsId;

    @ManyToOne(targetEntity = Dish.class)
    @JoinColumn(name = "dish_id")
    private Dish dish;

    @Column(name = "step_number")
    private int stepNumber;

    private String description;

    public Steps(Integer stepsId, Dish dish, int stepNumber, String description) {
        this.stepsId = stepsId;
        this.dish = dish;
        this.stepNumber = stepNumber;
        this.description = description;
    }

    public Steps() {}

    public Integer getStepsId() {
        return stepsId;
    }

    public void setStepsId(Integer stepsId) {
        this.stepsId = stepsId;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Steps{" +
                "stepsId=" + stepsId +
                ", dish=" + dish +
                ", stepNumber=" + stepNumber +
                ", description='" + description + '\'' +
                '}';
    }
}
