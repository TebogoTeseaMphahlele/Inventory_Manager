package com.example.smartpantry.data.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entity representing an ingredient stored in the user's pantry.
 */
@Entity(tableName = "pantry_items", indices = {@Index("normalizedName")})
public class PantryItem {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private String normalizedName;
    private double quantity;
    private String unit;
    private Long expiryDate; // epoch millis or null

    public PantryItem() {}

    public PantryItem(String name, String normalizedName, double quantity, String unit, Long expiryDate) {
        this.name = name;
        this.normalizedName = normalizedName;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNormalizedName() {
        return normalizedName;
    }

    public void setNormalizedName(String normalizedName) {
        this.normalizedName = normalizedName;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }
}
