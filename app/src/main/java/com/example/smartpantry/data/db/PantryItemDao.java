package com.example.smartpantry.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.smartpantry.data.model.PantryItem;
import java.util.List;

/**
 * Data Access Object for pantry_items table.
 */
@Dao
public interface PantryItemDao {
    @Insert
    long insert(PantryItem item);

    @Update
    void update(PantryItem item);

    @Delete
    void delete(PantryItem item);

    @Query("DELETE FROM pantry_items WHERE id = :id")
    void deleteById(long id);

    @Query("SELECT * FROM pantry_items WHERE id = :id LIMIT 1")
    PantryItem getByIdSync(long id);

    @Query("SELECT * FROM pantry_items ORDER BY name ASC")
    LiveData<List<PantryItem>> getAllLive();

    @Query("SELECT * FROM pantry_items ORDER BY name ASC")
    List<PantryItem> getAllSync();
}
