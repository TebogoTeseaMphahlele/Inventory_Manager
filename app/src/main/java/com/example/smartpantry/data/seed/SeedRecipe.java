package com.example.smartpantry.data.seed;

import java.util.List;

/**
 * POJO model for parsing recipes_seed.json.
 */
public class SeedRecipe {
    public String name;
    public String description;
    public List<String> steps;
    public List<SeedIngredient> ingredients;

    public static class SeedIngredient {
        public String name;
        public double quantity;
        public String unit;
    }
}
