package com.example.smartpantry.logic;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UnitConverterTest {

    @Test
    public void testMassConversions() {
        Quantity g = UnitConverter.toBase(500, "g");
        assertEquals(Quantity.DIMENSION_MASS, g.getDimension());
        assertEquals(500.0, g.getBaseValue(), 1e-6);

        Quantity kg = UnitConverter.toBase(1.5, "kg");
        assertEquals(Quantity.DIMENSION_MASS, kg.getDimension());
        assertEquals(1500.0, kg.getBaseValue(), 1e-6);

        Quantity oz = UnitConverter.toBase(2, "oz");
        assertEquals(Quantity.DIMENSION_MASS, oz.getDimension());
        assertEquals(56.699, oz.getBaseValue(), 1e-3);
    }

    @Test
    public void testVolumeConversions() {
        Quantity ml = UnitConverter.toBase(250, "ml");
        assertEquals(Quantity.DIMENSION_VOLUME, ml.getDimension());
        assertEquals(250.0, ml.getBaseValue(), 1e-6);

        Quantity cup = UnitConverter.toBase(2, "cups");
        assertEquals(Quantity.DIMENSION_VOLUME, cup.getDimension());
        assertEquals(480.0, cup.getBaseValue(), 1e-6);

        Quantity tbsp = UnitConverter.toBase(1, "tbsp");
        assertEquals(Quantity.DIMENSION_VOLUME, tbsp.getDimension());
        assertEquals(15.0, tbsp.getBaseValue(), 1e-6);
    }

    @Test
    public void testCountConversions() {
        Quantity pcs = UnitConverter.toBase(3, "pieces");
        assertEquals(Quantity.DIMENSION_COUNT, pcs.getDimension());
        assertEquals(3.0, pcs.getBaseValue(), 1e-6);

        Quantity cloves = UnitConverter.toBase(2, "cloves");
        assertEquals(Quantity.DIMENSION_COUNT, cloves.getDimension());
        assertEquals(2.0, cloves.getBaseValue(), 1e-6);
    }

    @Test
    public void testIncompatibleDimensions() {
        Quantity mass = UnitConverter.toBase(100, "g");
        Quantity volume = UnitConverter.toBase(100, "ml");
        assertFalse(UnitConverter.isComparable(mass, volume));

        Quantity mass2 = UnitConverter.toBase(1, "kg");
        assertTrue(UnitConverter.isComparable(mass, mass2));
    }

    @Test
    public void testUnknownUnitDefaultsToCountSafely() {
        Quantity unknown = UnitConverter.toBase(5, "unknown_unit");
        assertEquals(Quantity.DIMENSION_COUNT, unknown.getDimension());
        assertEquals(5.0, unknown.getBaseValue(), 1e-6);
    }
}
