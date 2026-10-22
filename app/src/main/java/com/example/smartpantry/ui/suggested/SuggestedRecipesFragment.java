package com.example.smartpantry.ui.suggested;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.smartpantry.R;
import com.example.smartpantry.data.model.RecipeWithIngredients;
import com.example.smartpantry.databinding.FragmentSuggestedRecipesBinding;
import com.example.smartpantry.ui.recipe.RecipeDetailActivity;
import com.example.smartpantry.util.Constants;
import com.example.smartpantry.viewmodel.SuggestedRecipesViewModel;
import java.util.List;

/**
 * Fragment displaying recipes that strictly match the user's available pantry ingredients.
 */
public class SuggestedRecipesFragment extends Fragment implements RecipeAdapter.OnRecipeClickListener {

    private FragmentSuggestedRecipesBinding binding;
    private SuggestedRecipesViewModel viewModel;
    private RecipeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSuggestedRecipesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SuggestedRecipesViewModel.class);

        adapter = new RecipeAdapter(this);
        binding.recyclerSuggested.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerSuggested.setAdapter(adapter);

        viewModel.getSuggestedRecipes().observe(getViewLifecycleOwner(), new Observer<List<RecipeWithIngredients>>() {
            @Override
            public void onChanged(List<RecipeWithIngredients> recipes) {
                if (recipes == null || recipes.isEmpty()) {
                    binding.recyclerSuggested.setVisibility(View.GONE);
                    binding.emptyState.getRoot().setVisibility(View.VISIBLE);
                    binding.emptyState.textEmptyMessage.setText(R.string.empty_recipes_message);
                } else {
                    binding.recyclerSuggested.setVisibility(View.VISIBLE);
                    binding.emptyState.getRoot().setVisibility(View.GONE);
                    adapter.setRecipes(recipes);
                }
            }
        });
    }

    @Override
    public void onRecipeClick(RecipeWithIngredients recipeWithIng) {
        if (recipeWithIng != null && recipeWithIng.getRecipe() != null) {
            Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
            intent.putExtra(Constants.EXTRA_RECIPE_ID, recipeWithIng.getRecipe().getId());
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
