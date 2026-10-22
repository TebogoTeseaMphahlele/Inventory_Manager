package com.example.smartpantry.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity representing a recipe.
 */
@Entity(tableName = "recipes")
public class Recipe {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private String description;
    private String steps; // Steps joined by newline

    public Recipe() {}

    public Recipe(String name, String description, String steps) {
        this.name = name;
        this.description = description;
        this.steps = steps;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }
}
