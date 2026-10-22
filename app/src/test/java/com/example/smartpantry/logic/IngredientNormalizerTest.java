package com.example.smartpantry.logic;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class IngredientNormalizerTest {

    @Test
    public void testPluralAndSingularNormalization() {
        assertEquals("tomato", IngredientNormalizer.normalize("Tomato"));
        assertEquals("tomato", IngredientNormalizer.normalize("tomatoes"));
        assertEquals("tomato", IngredientNormalizer.normalize(" Tomatoes "));
        assertEquals("leaf", IngredientNormalizer.normalize("leaves"));
        assertEquals("potato", IngredientNormalizer.normalize("potatoes"));
        assertEquals("cherry", IngredientNormalizer.normalize("cherries"));
    }

    @Test
    public void testDescriptorRemoval() {
        assertEquals("tomato", IngredientNormalizer.normalize(" Fresh chopped tomatoes "));
        assertEquals("garlic", IngredientNormalizer.normalize("minced garlic"));
        assertEquals("onion", IngredientNormalizer.normalize("diced medium onion"));
    }

    @Test
    public void testGroundBeefPreserved() {
        // "ground" should NOT be removed
        assertEquals("ground beef", IngredientNormalizer.normalize("ground beef"));
    }

    @Test
    public void testUncountableWordsKept() {
        assertEquals("asparagus", IngredientNormalizer.normalize("asparagus"));
        assertEquals("hummus", IngredientNormalizer.normalize("hummus"));
        assertEquals("couscous", IngredientNormalizer.normalize("couscous"));
    }

    @Test
    public void testSynonyms() {
        assertEquals("zucchini", IngredientNormalizer.normalize("courgette"));
        assertEquals("eggplant", IngredientNormalizer.normalize("aubergine"));
        assertEquals("green onion", IngredientNormalizer.normalize("scallion"));
        assertEquals("flour", IngredientNormalizer.normalize("all-purpose flour"));
    }
}
