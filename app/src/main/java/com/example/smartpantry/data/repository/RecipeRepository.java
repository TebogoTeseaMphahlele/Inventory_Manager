package com.example.smartpantry.data.repository;

import android.content.Context;
import com.example.smartpantry.data.db.AppDatabase;
import com.example.smartpantry.data.db.RecipeDao;
import com.example.smartpantry.data.model.RecipeWithIngredients;
import com.example.smartpantry.util.AppExecutors;
import java.util.List;

/**
 * Repository for managing recipe data operations.
 */
public class RecipeRepository {

    private final RecipeDao recipeDao;
    private final AppExecutors executors;

    public RecipeRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.recipeDao = db.recipeDao();
        this.executors = AppExecutors.getInstance();
    }

    public interface Callback<T> {
        void onResult(T result);
    }

    public void getAllRecipesWithIngredients(final Callback<List<RecipeWithIngredients>> callback) {
        executors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<RecipeWithIngredients> recipes = recipeDao.getAllWithIngredientsSync();
                executors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onResult(recipes);
                        }
                    }
                });
            }
        });
    }

    public void getRecipeWithIngredientsById(final long id, final Callback<RecipeWithIngredients> callback) {
        executors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final RecipeWithIngredients recipe = recipeDao.getWithIngredientsByIdSync(id);
                executors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onResult(recipe);
                        }
                    }
                });
            }
        });
    }
}
