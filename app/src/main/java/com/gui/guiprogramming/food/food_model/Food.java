package com.gui.guiprogramming.food.food_model;

import java.io.Serializable;

/**
 * Food is a BEAN/MODEL class contains all the information about a single Food
 * It has getters and setter use them to set/get values of Food class attributes
 * Useful in custom ListAdapter and to store or handle database value of a Food
 * It has to be serializable, to pass an instance as EXTRA in the Intent
 * */
public class Food implements Serializable {

    String foodId;
    String label;
    String category;
    String categoryLabel;
    Nutrients nutrients;
    String tag;

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryLabel() {
        return categoryLabel;
    }

    public void setCategoryLabel(String categoryLabel) {
        this.categoryLabel = categoryLabel;
    }

    public Nutrients getNutrients() {
        return nutrients;
    }

    public void setNutrients(Nutrients nutrients) {
        this.nutrients = nutrients;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}