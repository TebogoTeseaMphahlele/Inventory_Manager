package com.example.smartpantry.data.model;

import androidx.room.Embedded;
import androidx.room.Relation;
import java.util.List;

/**
 * Relation model combining a Recipe with its list of RecipeIngredients.
 */
public class RecipeWithIngredients {
    @Embedded
    private Recipe recipe;

    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    private List<RecipeIngredient> ingredients;

    public RecipeWithIngredients() {}

    public RecipeWithIngredients(Recipe recipe, List<RecipeIngredient> ingredients) {
        this.recipe = recipe;
        this.ingredients = ingredients;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }
}
