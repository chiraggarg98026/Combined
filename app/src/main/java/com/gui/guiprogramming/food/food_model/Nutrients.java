package com.gui.guiprogramming.food.food_model;

import java.io.Serializable;

/**
 * Nutrients is a BEAN/MODEL class contains all the information about Nutrients of a single Food
 * It has getters and setter use them to set/get values of Food class attributes
 * Useful in custom ListAdapter and to store or handle database value of a Food
 * It has to be serializable, to pass an instance as EXTRA in the Intent
 * Used inside Food as instance, to separate Nutrient fields
 * */
public class Nutrients implements Serializable {
    double energy, protein, fat, carbs;

    public Nutrients() {
    }

    public Nutrients(double energy, double protein, double fat, double carbs) {
        this.energy = energy;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }
}
