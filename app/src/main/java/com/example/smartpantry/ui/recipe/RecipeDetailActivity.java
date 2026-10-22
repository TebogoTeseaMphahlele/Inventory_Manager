package com.example.smartpantry.ui.recipe;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartpantry.R;
import com.example.smartpantry.data.model.Recipe;
import com.example.smartpantry.data.model.RecipeIngredient;
import com.example.smartpantry.data.model.RecipeWithIngredients;
import com.example.smartpantry.data.repository.RecipeRepository;
import com.example.smartpantry.databinding.ActivityRecipeDetailBinding;
import com.example.smartpantry.util.Constants;
import java.util.List;
import java.util.Locale;

/**
 * Activity for displaying the full details of a recipe, including ingredients and steps.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    private ActivityRecipeDetailBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecipeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(R.string.title_recipe_detail);
        }

        long recipeId = -1;
        if (getIntent() != null && getIntent().hasExtra(Constants.EXTRA_RECIPE_ID)) {
            recipeId = getIntent().getLongExtra(Constants.EXTRA_RECIPE_ID, -1);
        }

        if (recipeId == -1) {
            Toast.makeText(this, R.string.error_recipe_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadRecipeDetail(recipeId);
    }

    private void loadRecipeDetail(long recipeId) {
        RecipeRepository repository = new RecipeRepository(this);
        repository.getRecipeWithIngredientsById(recipeId, new RecipeRepository.Callback<RecipeWithIngredients>() {
            @Override
            public void onResult(RecipeWithIngredients recipeWithIng) {
                if (recipeWithIng == null || recipeWithIng.getRecipe() == null) {
                    Toast.makeText(RecipeDetailActivity.this, R.string.error_recipe_not_found, Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                populateUI(recipeWithIng);
            }
        });
    }

    private void populateUI(RecipeWithIngredients recipeWithIng) {
        Recipe recipe = recipeWithIng.getRecipe();
        binding.textRecipeTitle.setText(recipe.getName());
        binding.textRecipeDescription.setText(recipe.getDescription());

        // Ingredients
        binding.layoutIngredientsList.removeAllViews();
        List<RecipeIngredient> ingredients = recipeWithIng.getIngredients();
        if (ingredients != null) {
            for (RecipeIngredient ing : ingredients) {
                TextView tv = new TextView(this);
                String formattedQty = formatQuantity(ing.getQuantity());
                String text = "• " + formattedQty + " " + ing.getUnit() + " " + ing.getName();
                tv.setText(text);
                tv.setTextSize(16);
                tv.setTextColor(getColor(R.color.text_primary));
                tv.setPadding(0, 8, 0, 8);
                binding.layoutIngredientsList.addView(tv);
            }
        }

        // Steps
        binding.layoutStepsList.removeAllViews();
        if (recipe.getSteps() != null && !recipe.getSteps().trim().isEmpty()) {
            String[] stepsArray = recipe.getSteps().split("\n");
            int stepNum = 1;
            for (String step : stepsArray) {
                if (step.trim().isEmpty()) {
                    continue;
                }
                TextView tv = new TextView(this);
                String text = stepNum + ". " + step.trim();
                tv.setText(text);
                tv.setTextSize(16);
                tv.setTextColor(getColor(R.color.text_primary));
                tv.setPadding(0, 8, 0, 12);
                binding.layoutStepsList.addView(tv);
                stepNum++;
            }
        }
    }

    private String formatQuantity(double qty) {
        if (qty == (long) qty) {
            return String.format(Locale.getDefault(), "%d", (long) qty);
        } else {
            return String.format(Locale.getDefault(), "%.2f", qty);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
