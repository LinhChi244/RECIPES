package com.example.recipes.models;

import java.io.Serializable;


public class Recipe implements Serializable {
    private String id;
    private String name;
    private String image;
    private String description;
    private String ingredients;
    private String directions;
    private String category;
    private String calories;
    private String time;
    private String authorId;

    public Recipe() {}

    public Recipe(String name, String description, String directions, String ingredients, String time, String category, String calories, String image, String authorId) {
        this.name = name;
        this.description = description;
        this.directions = directions;
        this.ingredients = ingredients;
        this.time = time;
        this.category = category;
        this.calories = calories;
        this.image = image;
        this.authorId = authorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getDirections() {return directions; }
    public void setDirections(String directions) {this.directions = directions; }

    public String getIngredients() {return ingredients; }
    public void setIngredients(String ingredients) {this.ingredients = ingredients; }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
}
