package com.example.smartpantry.logic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Utility for normalizing ingredient names to ensure consistent matching.
 * Performs lowercasing, punctuation stripping, descriptor removal, singularization,
 * and synonym mapping.
 */
public class IngredientNormalizer {

    private static final Set<String> DESCRIPTORS = new HashSet<>(Arrays.asList(
            "fresh", "large", "small", "medium", "chopped", "diced", "sliced", "minced"
    ));

    private static final Map<String, String> IRREGULAR_PLURALS = new HashMap<>();
    static {
        IRREGULAR_PLURALS.put("leaves", "leaf");
        IRREGULAR_PLURALS.put("loaves", "loaf");
        IRREGULAR_PLURALS.put("potatoes", "potato");
        IRREGULAR_PLURALS.put("tomatoes", "tomato");
        IRREGULAR_PLURALS.put("cherries", "cherry");
        IRREGULAR_PLURALS.put("berries", "berry");
        IRREGULAR_PLURALS.put("knives", "knife");
        IRREGULAR_PLURALS.put("halves", "half");
    }

    private static final Set<String> UNCOUNTABLE_KEEP = new HashSet<>(Arrays.asList(
            "hummus", "asparagus", "couscous", "molasses", "swiss"
    ));

    private static final Map<String, String> SYNONYMS = new HashMap<>();
    static {
        SYNONYMS.put("courgette", "zucchini");
        SYNONYMS.put("aubergine", "eggplant");
        SYNONYMS.put("capsicum", "bell pepper");
        SYNONYMS.put("coriander", "cilantro");
        SYNONYMS.put("spring onion", "green onion");
        SYNONYMS.put("scallion", "green onion");
        SYNONYMS.put("plain flour", "flour");
        SYNONYMS.put("all-purpose flour", "flour");
        SYNONYMS.put("all purpose flour", "flour");
    }

    public static String normalize(String raw) {
        if (raw == null) {
            return "";
        }

        // 1. Lowercase, strip punctuation except spaces, collapse whitespace
        String cleaned = raw.toLowerCase().trim();
        cleaned = cleaned.replaceAll("[^a-z0-9\\s]", " ");
        cleaned = cleaned.replaceAll("\\s+", " ").trim();

        if (cleaned.isEmpty()) {
            return "";
        }

        // 2. Remove common descriptors and singularize word-by-word
        String[] words = cleaned.split(" ");
        StringBuilder sb = new StringBuilder();

        for (String word : words) {
            if (DESCRIPTORS.contains(word)) {
                continue; // strip descriptor
            }

            String singular = singularize(word);
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(singular);
        }

        String result = sb.toString().trim();

        // 3. Synonym map lookup on final string
        if (SYNONYMS.containsKey(result)) {
            result = SYNONYMS.get(result);
        }

        return result;
    }

    private static String singularize(String word) {
        if (word == null || word.isEmpty()) {
            return "";
        }

        if (UNCOUNTABLE_KEEP.contains(word)) {
            return word;
        }

        if (IRREGULAR_PLURALS.containsKey(word)) {
            return IRREGULAR_PLURALS.get(word);
        }

        if (word.endsWith("ies") && word.length() > 3) {
            return word.substring(0, word.length() - 3) + "y";
        }

        if (word.endsWith("oes") && word.length() > 3) {
            return word.substring(0, word.length() - 3) + "o";
        }

        if ((word.endsWith("ches") || word.endsWith("shes") || word.endsWith("sses") || word.endsWith("xes"))
                && word.length() > 4) {
            return word.substring(0, word.length() - 2);
        }

        if (word.endsWith("s") && !word.endsWith("ss") && !word.endsWith("us") && word.length() > 1) {
            return word.substring(0, word.length() - 1);
        }

        return word;
    }
}
