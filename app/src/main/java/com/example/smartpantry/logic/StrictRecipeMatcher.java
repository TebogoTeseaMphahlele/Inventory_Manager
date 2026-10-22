package com.example.smartpantry.logic;

import com.example.smartpantry.data.model.PantryItem;
import com.example.smartpantry.data.model.RecipeIngredient;
import com.example.smartpantry.data.model.RecipeWithIngredients;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Core matching logic for evaluating whether recipes can be made from available pantry items.
 */
public class StrictRecipeMatcher {

    private static final double EPSILON = 1e-6;

    public static List<RecipeWithIngredients> getStrictMatches(
            List<PantryItem> pantry, List<RecipeWithIngredients> recipes) {

        if (recipes == null || recipes.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, Map<String, Double>> pantryMap = buildPantryMap(pantry);
        List<RecipeWithIngredients> matchedRecipes = new ArrayList<>();

        for (RecipeWithIngredients recipeWithIng : recipes) {
            MatchResult result = evaluateWithMap(pantryMap, recipeWithIng);
            if (result.isFullyMatched()) {
                matchedRecipes.add(recipeWithIng);
            }
        }

        // Sort alphabetically by recipe name
        Collections.sort(matchedRecipes, new Comparator<RecipeWithIngredients>() {
            @Override
            public int compare(RecipeWithIngredients r1, RecipeWithIngredients r2) {
                String name1 = (r1 != null && r1.getRecipe() != null && r1.getRecipe().getName() != null)
                        ? r1.getRecipe().getName() : "";
                String name2 = (r2 != null && r2.getRecipe() != null && r2.getRecipe().getName() != null)
                        ? r2.getRecipe().getName() : "";
                return name1.compareToIgnoreCase(name2);
            }
        });

        return matchedRecipes;
    }

    public static MatchResult evaluate(List<PantryItem> pantry, RecipeWithIngredients recipe) {
        Map<String, Map<String, Double>> pantryMap = buildPantryMap(pantry);
        return evaluateWithMap(pantryMap, recipe);
    }

    private static Map<String, Map<String, Double>> buildPantryMap(List<PantryItem> pantry) {
        Map<String, Map<String, Double>> map = new HashMap<>();

        if (pantry == null || pantry.isEmpty()) {
            return map;
        }

        for (PantryItem item : pantry) {
            if (item == null || item.getName() == null) {
                continue;
            }

            String normName = item.getNormalizedName();
            if (normName == null || normName.trim().isEmpty()) {
                normName = IngredientNormalizer.normalize(item.getName());
            }

            Quantity qty = UnitConverter.toBase(item.getQuantity(), item.getUnit());
            String dim = qty.getDimension();
            double baseVal = qty.getBaseValue();

            Map<String, Double> dimMap = map.get(normName);
            if (dimMap == null) {
                dimMap = new HashMap<>();
                map.put(normName, dimMap);
            }

            double currentTotal = dimMap.getOrDefault(dim, 0.0);
            dimMap.put(dim, currentTotal + baseVal);
        }

        return map;
    }

    private static MatchResult evaluateWithMap(
            Map<String, Map<String, Double>> pantryMap, RecipeWithIngredients recipeWithIng) {

        if (recipeWithIng == null || recipeWithIng.getIngredients() == null
                || recipeWithIng.getIngredients().isEmpty()) {
            // A recipe with zero ingredients must never be suggested
            return new MatchResult(false, new ArrayList<>(), new ArrayList<>());
        }

        List<RecipeIngredient> ingredients = recipeWithIng.getIngredients();
        List<RecipeIngredient> missing = new ArrayList<>();
        List<RecipeIngredient> insufficient = new ArrayList<>();

        for (RecipeIngredient ing : ingredients) {
            if (ing == null || ing.getName() == null) {
                continue;
            }

            String normName = ing.getNormalizedName();
            if (normName == null || normName.trim().isEmpty()) {
                normName = IngredientNormalizer.normalize(ing.getName());
            }

            Quantity reqQty = UnitConverter.toBase(ing.getQuantity(), ing.getUnit());
            String reqDim = reqQty.getDimension();
            double reqBaseVal = reqQty.getBaseValue();
            System.out.println("MATCH? recipe ingredient='" + normName + "' dim=" + reqDim
                    + " need=" + reqBaseVal + " pantryKeys=" + pantryMap.keySet()
                    + " have=" + pantryMap.get(normName));
            Map<String, Double> availableDimMap = pantryMap.get(normName);
            if (availableDimMap == null || !availableDimMap.containsKey(reqDim)) {
                missing.add(ing);
            } else {
                double availableBaseVal = availableDimMap.get(reqDim);
                if (availableBaseVal + EPSILON < reqBaseVal) {
                    insufficient.add(ing);
                }
            }
        }

        boolean fullyMatched = missing.isEmpty() && insufficient.isEmpty();
        return new MatchResult(fullyMatched, missing, insufficient);
    }
}
