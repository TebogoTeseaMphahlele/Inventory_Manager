package com.example.smartpantry.logic;

import com.example.smartpantry.data.model.RecipeIngredient;
import java.util.ArrayList;
import java.util.List;

/**
 * Result of evaluating a recipe against the pantry.
 */
public class MatchResult {
    private final boolean fullyMatched;
    private final List<RecipeIngredient> missing;
    private final List<RecipeIngredient> insufficient;

    public MatchResult(boolean fullyMatched, List<RecipeIngredient> missing, List<RecipeIngredient> insufficient) {
        this.fullyMatched = fullyMatched;
        this.missing = missing != null ? missing : new ArrayList<>();
        this.insufficient = insufficient != null ? insufficient : new ArrayList<>();
    }

    public boolean isFullyMatched() {
        return fullyMatched;
    }

    public List<RecipeIngredient> getMissing() {
        return missing;
    }

    public List<RecipeIngredient> getInsufficient() {
        return insufficient;
    }
}
