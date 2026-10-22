package com.example.smartpantry.logic;

import com.example.smartpantry.data.model.PantryItem;
import com.example.smartpantry.data.model.Recipe;
import com.example.smartpantry.data.model.RecipeIngredient;
import com.example.smartpantry.data.model.RecipeWithIngredients;

import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StrictRecipeMatcherTest {

    private RecipeWithIngredients createRecipe(String name, RecipeIngredient... ingredients) {
        Recipe r = new Recipe(name, "Description for " + name, "Step 1\nStep 2");
        return new RecipeWithIngredients(r, Arrays.asList(ingredients));
    }

    @Test
    public void test1_AllIngredientsSufficient_RecipeSuggested() {
        List<PantryItem> pantry = Arrays.asList(
                new PantryItem("pasta", "pasta", 200, "g", null),
                new PantryItem("tomatoes", "tomato", 3, "piece", null)
        );

        RecipeWithIngredients recipe = createRecipe("Pasta",
                new RecipeIngredient(1, "pasta", "pasta", 200, "g"),
                new RecipeIngredient(1, "tomato", "tomato", 2, "piece")
        );

        List<RecipeWithIngredients> matches = StrictRecipeMatcher.getStrictMatches(pantry, Arrays.asList(recipe));
        assertEquals(1, matches.size());
        assertEquals("Pasta", matches.get(0).getRecipe().getName());
    }

    @Test
    public void test2_RecipeNeeds5PantryHas4_NotSuggested() {
        List<PantryItem> pantry = Arrays.asList(
                new PantryItem("pasta", "pasta", 200, "g", null),
                new PantryItem("tomatoes", "tomato", 3, "piece", null),
                new PantryItem("garlic", "garlic", 2, "clove", null),
                new PantryItem("olive oil", "olive oil", 2, "tbsp", null)
        ); // missing 5th ingredient: cheese

        RecipeWithIngredients recipe = createRecipe("5-Ingredient Pasta",
                new RecipeIngredient(1, "pasta", "pasta", 200, "g"),
                new RecipeIngredient(1, "tomato", "tomato", 2, "piece"),
                new RecipeIngredient(1, "garlic", "garlic", 1, "clove"),
                new RecipeIngredient(1, "olive oil", "olive oil", 1, "tbsp"),
                new RecipeIngredient(1, "cheese", "cheese", 50, "g")
        );

        List<RecipeWithIngredients> matches = StrictRecipeMatcher.getStrictMatches(pantry, Arrays.asList(recipe));
        assertTrue(matches.isEmpty());
    }

    @Test
    public void test3_PantryHasIngredientButTooLittle_NotSuggested() {
        List<PantryItem> pantry = Arrays.asList(
                new PantryItem("flour", "flour", 100, "g", null)
        );

        RecipeWithIngredients recipe = createRecipe("Cake",
                new RecipeIngredient(1, "flour", "flour", 200, "g")
        );

        List<RecipeWithIngredients> matches = StrictRecipeMatcher.getStrictMatches(pantry, Arrays.asList(recipe));
        assertTrue(matches.isEmpty());
    }

    @Test
    public void test4_TomatoInPantryVsTomatoesInRecipe_Suggested() {
        List<PantryItem> pantry = Arrays.asList(
                new PantryItem("tomato", "tomato", 5, "piece", null)
        );

        RecipeWithIngredients recipe = createRecipe("Tomato Soup",
                new RecipeIngredient(1, "tomatoes", "tomato", 3, "piece")
        );

        List<RecipeWithIngredients> matches = StrictRecipeMatcher.getStrictMatches(pantry, Arrays.asList(recipe));
        assertEquals(1, matches.size());
    }

    @Test
    public void test5_Pantry1KgFlourVsRecipe500gFlour_Suggested() {
        List<PantryItem> pantry = Arrays.asList(
                new PantryItem("flour", "flour", 1, "kg", null)
        );

        RecipeWithIngredients recipe = createRecipe("Bread",
                new RecipeIngredient(1, "flour", "flour", 500, "g")
        );

        List<RecipeWithIngredients> matches = StrictRecipeMatcher.getStrictMatches(pantry, Arrays.asList(recipe));
        assertEquals(1, matches.size());
    }

    @Test
    public void test6_Pantry500mlMilkVsRecipe1CupMilk_Suggested() {
        List<PantryItem> pantry = Arrays.asList(
                new PantryItem("milk", "milk", 500, "ml", null)
        );

        RecipeWithIngredients recipe = createRecipe("Pancakes",
                new RecipeIngredient(1, "milk", "milk", 1, "cup")
        ); // 1 cup = 240 ml

        List<RecipeWithIngredients> matches = StrictRecipeMatcher.getStrictMatches(pantry, Arrays.asList(recipe));
        assertEquals(1, matches.size());
    }

    @Test
    public void test7_TwoPantryEntriesOfSameIngredientAddUp_Suggested() {
        List<PantryItem> pantry = Arrays.asList(
                new PantryItem("milk", "milk", 250, "ml", null),
                new PantryItem("milk", "milk", 250, "ml", null)
        ); // total = 500 ml

        RecipeWithIngredients recipe = createRecipe("Milk Shake",
                new RecipeIngredient(1, "milk", "milk", 400, "ml")
        );

        List<RecipeWithIngredients> matches = StrictRecipeMatcher.getStrictMatches(pantry, Arrays.asList(recipe));
        assertEquals(1, matches.size());
    }

    @Test
    public void test8_IncompatibleUnits_NotSuggested() {
        List<PantryItem> pantry = Arrays.asList(
                new PantryItem("flour", "flour", 500, "ml", null) // volume
        );

        RecipeWithIngredients recipe = createRecipe("Bread",
                new RecipeIngredient(1, "flour", "flour", 500, "g") // mass
        );

        List<RecipeWithIngredients> matches = StrictRecipeMatcher.getStrictMatches(pantry, Arrays.asList(recipe));
        assertTrue(matches.isEmpty());
    }

    @Test
    public void test9_EmptyPantryAndZeroIngredientRecipe_NotSuggested() {
        List<PantryItem> pantry = new ArrayList<>();
        RecipeWithIngredients emptyRecipe = createRecipe("Empty Recipe");

        List<RecipeWithIngredients> matches = StrictRecipeMatcher.getStrictMatches(pantry, Arrays.asList(emptyRecipe));
        assertTrue(matches.isEmpty());
    }

    @Test
    public void test10_CaseAndWhitespaceDifferences_Suggested() {
        List<PantryItem> pantry = Arrays.asList(
                new PantryItem(" EGGS ", "egg", 6, "piece", null)
        );

        RecipeWithIngredients recipe = createRecipe("Omelette",
                new RecipeIngredient(1, "egg", "egg", 2, "piece")
        );

        List<RecipeWithIngredients> matches = StrictRecipeMatcher.getStrictMatches(pantry, Arrays.asList(recipe));
        assertEquals(1, matches.size());
    }
}
