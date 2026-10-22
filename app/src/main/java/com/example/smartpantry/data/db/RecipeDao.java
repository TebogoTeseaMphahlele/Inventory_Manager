package com.example.smartpantry.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import com.example.smartpantry.data.model.Recipe;
import com.example.smartpantry.data.model.RecipeIngredient;
import com.example.smartpantry.data.model.RecipeWithIngredients;
import java.util.List;

/**
 * Data Access Object for recipes and recipe_ingredients tables.
 */
@Dao
public interface RecipeDao {
    @Insert
    long insertRecipe(Recipe recipe);

    @Insert
    void insertIngredients(List<RecipeIngredient> ingredients);

    @Transaction
    @Query("SELECT * FROM recipes ORDER BY name ASC")
    List<RecipeWithIngredients> getAllWithIngredientsSync();

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :id LIMIT 1")
    RecipeWithIngredients getWithIngredientsByIdSync(long id);

    @Query("SELECT COUNT(*) FROM recipes")
    int count();
}
