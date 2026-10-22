package com.example.smartpantry.data.seed;

import android.content.Context;
import com.example.smartpantry.data.db.AppDatabase;
import com.example.smartpantry.data.model.Recipe;
import com.example.smartpantry.data.model.RecipeIngredient;
import com.example.smartpantry.logic.IngredientNormalizer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeder helper for populating the database with initial recipe seed data from assets.
 */
public class RecipeSeeder {

    public static synchronized void seedDatabase(Context context, AppDatabase db) {
        if (db.recipeDao().count() > 0) {
            return; // Already seeded
        }

        try {
            InputStream is = context.getAssets().open("recipes_seed.json");
            InputStreamReader reader = new InputStreamReader(is);
            Gson gson = new Gson();
            Type listType = new TypeToken<List<SeedRecipe>>() {}.getType();
            List<SeedRecipe> seedRecipes = gson.fromJson(reader, listType);

            if (seedRecipes == null) {
                return;
            }

            for (SeedRecipe sr : seedRecipes) {
                if (sr.name == null || sr.steps == null || sr.ingredients == null) {
                    continue;
                }

                String joinedSteps = String.join("\n", sr.steps);
                Recipe recipe = new Recipe(sr.name, sr.description != null ? sr.description : "", joinedSteps);
                long recipeId = db.recipeDao().insertRecipe(recipe);

                List<RecipeIngredient> ingredientsToInsert = new ArrayList<>();
                for (SeedRecipe.SeedIngredient si : sr.ingredients) {
                    if (si.name == null) {
                        continue;
                    }
                    String normName = IngredientNormalizer.normalize(si.name);
                    RecipeIngredient ri = new RecipeIngredient(
                            recipeId,
                            si.name,
                            normName,
                            si.quantity,
                            si.unit != null ? si.unit : "piece"
                    );
                    ingredientsToInsert.add(ri);
                }

                if (!ingredientsToInsert.isEmpty()) {
                    db.recipeDao().insertIngredients(ingredientsToInsert);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
