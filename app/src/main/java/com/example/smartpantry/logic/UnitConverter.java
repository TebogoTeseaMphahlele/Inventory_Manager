package com.example.smartpantry.logic;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts quantities to canonical base units (g for Mass, ml for Volume, piece for Count).
 */
public class UnitConverter {

    private static class ConversionRule {
        final String dimension;
        final double factor;

        ConversionRule(String dimension, double factor) {
            this.dimension = dimension;
            this.factor = factor;
        }
    }

    private static final Map<String, ConversionRule> UNIT_MAP = new HashMap<>();

    static {
        // Mass -> g
        addUnit("g", Quantity.DIMENSION_MASS, 1.0);
        addUnit("gram", Quantity.DIMENSION_MASS, 1.0);
        addUnit("grams", Quantity.DIMENSION_MASS, 1.0);
        addUnit("kg", Quantity.DIMENSION_MASS, 1000.0);
        addUnit("kilogram", Quantity.DIMENSION_MASS, 1000.0);
        addUnit("kilograms", Quantity.DIMENSION_MASS, 1000.0);
        addUnit("mg", Quantity.DIMENSION_MASS, 0.001);
        addUnit("milligram", Quantity.DIMENSION_MASS, 0.001);
        addUnit("milligrams", Quantity.DIMENSION_MASS, 0.001);
        addUnit("oz", Quantity.DIMENSION_MASS, 28.3495);
        addUnit("ounce", Quantity.DIMENSION_MASS, 28.3495);
        addUnit("ounces", Quantity.DIMENSION_MASS, 28.3495);
        addUnit("lb", Quantity.DIMENSION_MASS, 453.592);
        addUnit("lbs", Quantity.DIMENSION_MASS, 453.592);
        addUnit("pound", Quantity.DIMENSION_MASS, 453.592);
        addUnit("pounds", Quantity.DIMENSION_MASS, 453.592);

        // Volume -> ml
        addUnit("ml", Quantity.DIMENSION_VOLUME, 1.0);
        addUnit("milliliter", Quantity.DIMENSION_VOLUME, 1.0);
        addUnit("milliliters", Quantity.DIMENSION_VOLUME, 1.0);
        addUnit("millilitre", Quantity.DIMENSION_VOLUME, 1.0);
        addUnit("millilitres", Quantity.DIMENSION_VOLUME, 1.0);
        addUnit("l", Quantity.DIMENSION_VOLUME, 1000.0);
        addUnit("liter", Quantity.DIMENSION_VOLUME, 1000.0);
        addUnit("liters", Quantity.DIMENSION_VOLUME, 1000.0);
        addUnit("litre", Quantity.DIMENSION_VOLUME, 1000.0);
        addUnit("litres", Quantity.DIMENSION_VOLUME, 1000.0);
        addUnit("tsp", Quantity.DIMENSION_VOLUME, 5.0);
        addUnit("teaspoon", Quantity.DIMENSION_VOLUME, 5.0);
        addUnit("teaspoons", Quantity.DIMENSION_VOLUME, 5.0);
        addUnit("tbsp", Quantity.DIMENSION_VOLUME, 15.0);
        addUnit("tablespoon", Quantity.DIMENSION_VOLUME, 15.0);
        addUnit("tablespoons", Quantity.DIMENSION_VOLUME, 15.0);
        addUnit("cup", Quantity.DIMENSION_VOLUME, 240.0);
        addUnit("cups", Quantity.DIMENSION_VOLUME, 240.0);
        addUnit("fl oz", Quantity.DIMENSION_VOLUME, 29.5735);
        addUnit("floz", Quantity.DIMENSION_VOLUME, 29.5735);

        // Count -> piece
        addUnit("piece", Quantity.DIMENSION_COUNT, 1.0);
        addUnit("pieces", Quantity.DIMENSION_COUNT, 1.0);
        addUnit("pc", Quantity.DIMENSION_COUNT, 1.0);
        addUnit("pcs", Quantity.DIMENSION_COUNT, 1.0);
        addUnit("whole", Quantity.DIMENSION_COUNT, 1.0);
        addUnit("item", Quantity.DIMENSION_COUNT, 1.0);
        addUnit("items", Quantity.DIMENSION_COUNT, 1.0);
        addUnit("clove", Quantity.DIMENSION_COUNT, 1.0);
        addUnit("cloves", Quantity.DIMENSION_COUNT, 1.0);
        addUnit("slice", Quantity.DIMENSION_COUNT, 1.0);
        addUnit("slices", Quantity.DIMENSION_COUNT, 1.0);
        addUnit("can", Quantity.DIMENSION_COUNT, 1.0);
        addUnit("cans", Quantity.DIMENSION_COUNT, 1.0);
        addUnit("tin", Quantity.DIMENSION_COUNT, 1.0);
        addUnit("tins", Quantity.DIMENSION_COUNT, 1.0);
        addUnit("pack", Quantity.DIMENSION_COUNT, 1.0);
        addUnit("packs", Quantity.DIMENSION_COUNT, 1.0);
        addUnit("bunch", Quantity.DIMENSION_COUNT, 1.0);
        addUnit("bunches", Quantity.DIMENSION_COUNT, 1.0);
    }

    private static void addUnit(String name, String dimension, double factor) {
        UNIT_MAP.put(name.toLowerCase().trim(), new ConversionRule(dimension, factor));
    }

    public static Quantity toBase(double value, String unit) {
        if (unit == null || unit.trim().isEmpty()) {
            return new Quantity(Quantity.DIMENSION_COUNT, value);
        }

        String cleanedUnit = unit.toLowerCase().trim();
        ConversionRule rule = UNIT_MAP.get(cleanedUnit);

        if (rule == null) {
            // Unknown unit -> default safely to COUNT x 1.0
            return new Quantity(Quantity.DIMENSION_COUNT, value);
        }

        return new Quantity(rule.dimension, value * rule.factor);
    }

    public static boolean isComparable(Quantity a, Quantity b) {
        if (a == null || b == null) {
            return false;
        }
        return a.getDimension().equals(b.getDimension());
    }
}
