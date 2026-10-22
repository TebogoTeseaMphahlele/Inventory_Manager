package com.example.smartpantry.logic;

import java.util.Objects;

/**
 * Represents a quantity converted to a canonical base unit within a dimension.
 */
public class Quantity {
    public static final String DIMENSION_MASS = "MASS";
    public static final String DIMENSION_VOLUME = "VOLUME";
    public static final String DIMENSION_COUNT = "COUNT";

    private final String dimension;
    private final double baseValue;

    public Quantity(String dimension, double baseValue) {
        this.dimension = dimension;
        this.baseValue = baseValue;
    }

    public String getDimension() {
        return dimension;
    }

    public double getBaseValue() {
        return baseValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quantity quantity = (Quantity) o;
        return Math.abs(quantity.baseValue - baseValue) < 1e-6 &&
                Objects.equals(dimension, quantity.dimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dimension, baseValue);
    }

    @Override
    public String toString() {
        return "Quantity{" +
                "dimension='" + dimension + '\'' +
                ", baseValue=" + baseValue +
                '}';
    }
}
