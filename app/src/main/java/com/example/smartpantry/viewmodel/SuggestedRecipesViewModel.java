package com.example.smartpantry.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.example.smartpantry.data.model.PantryItem;
import com.example.smartpantry.data.model.RecipeWithIngredients;
import com.example.smartpantry.data.repository.PantryRepository;
import com.example.smartpantry.data.repository.RecipeRepository;
import com.example.smartpantry.logic.StrictRecipeMatcher;
import com.example.smartpantry.util.AppExecutors;
import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for evaluating and exposing suggested recipes based on pantry contents.
 */
public class SuggestedRecipesViewModel extends AndroidViewModel {

    private final PantryRepository pantryRepository;
    private final RecipeRepository recipeRepository;
//    private final MutableLiveData<List<RecipeWithIngredients>> suggestedRecipes = new MutableLiveData<>(new ArrayList<>());
    private final MediatorLiveData<List<PantryItem>> pantryLiveData = new MediatorLiveData<>();

    private final MediatorLiveData<List<RecipeWithIngredients>> suggestedRecipes = new MediatorLiveData<>();

//    public SuggestedRecipesViewModel(@NonNull Application application) {
//        super(application);
//        pantryRepository = new PantryRepository(application);
//        recipeRepository = new RecipeRepository(application);
//
//        LiveData<List<PantryItem>> livePantry = pantryRepository.getAllPantryItems();
//        pantryLiveData.addSource(livePantry, new Observer<List<PantryItem>>() {
//            @Override
//            public void onChanged(List<PantryItem> pantryItems) {
//                reevaluateSuggestions(pantryItems);
//            }
//        });
//    }
public SuggestedRecipesViewModel(@NonNull Application application) {
    super(application);
    pantryRepository = new PantryRepository(application);
    recipeRepository = new RecipeRepository(application);

    suggestedRecipes.setValue(new ArrayList<>());

    LiveData<List<PantryItem>> livePantry = pantryRepository.getAllPantryItems();
    suggestedRecipes.addSource(livePantry, new Observer<List<PantryItem>>() {
        @Override
        public void onChanged(List<PantryItem> pantryItems) {
            reevaluateSuggestions(pantryItems);
        }
    });
}

    public LiveData<List<RecipeWithIngredients>> getSuggestedRecipes() {
        return suggestedRecipes;
    }

    public void reevaluateSuggestions(final List<PantryItem> pantryItems) {
        recipeRepository.getAllRecipesWithIngredients(new RecipeRepository.Callback<List<RecipeWithIngredients>>() {
            @Override
            public void onResult(final List<RecipeWithIngredients> allRecipes) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        List<RecipeWithIngredients> matches = StrictRecipeMatcher.getStrictMatches(pantryItems, allRecipes);
                        suggestedRecipes.postValue(matches);
                    }
                });
            }
        });
    }
}
