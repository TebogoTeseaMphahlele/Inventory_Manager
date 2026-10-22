package com.example.smartpantry.ui.suggested;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartpantry.R;
import com.example.smartpantry.data.model.RecipeWithIngredients;
import com.example.smartpantry.databinding.ItemRecipeBinding;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying suggested recipes in a RecyclerView.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(RecipeWithIngredients recipe);
    }

    private final List<RecipeWithIngredients> recipes = new ArrayList<>();
    private final OnRecipeClickListener listener;

    public RecipeAdapter(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    public void setRecipes(List<RecipeWithIngredients> newRecipes) {
        this.recipes.clear();
        if (newRecipes != null) {
            this.recipes.addAll(newRecipes);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecipeBinding binding = ItemRecipeBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new RecipeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.bind(recipes.get(position));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final ItemRecipeBinding binding;

        RecipeViewHolder(ItemRecipeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(final RecipeWithIngredients recipeWithIng) {
            if (recipeWithIng == null || recipeWithIng.getRecipe() == null) {
                return;
            }

            Context context = itemView.getContext();
            binding.textRecipeName.setText(recipeWithIng.getRecipe().getName());
            binding.textRecipeDescription.setText(recipeWithIng.getRecipe().getDescription());

            int count = recipeWithIng.getIngredients() != null ? recipeWithIng.getIngredients().size() : 0;
            binding.textIngredientCount.setText(context.getString(R.string.label_ingredients_count, count));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onRecipeClick(recipeWithIng);
                    }
                }
            });
        }
    }
}
