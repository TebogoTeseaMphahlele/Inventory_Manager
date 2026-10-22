package com.example.smartpantry.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.smartpantry.data.model.PantryItem;
import com.example.smartpantry.data.repository.PantryRepository;
import java.util.List;

/**
 * ViewModel for managing pantry list state and operations.
 */
public class PantryViewModel extends AndroidViewModel {

    private final PantryRepository repository;
    private final LiveData<List<PantryItem>> pantryItems;

    public PantryViewModel(@NonNull Application application) {
        super(application);
        repository = new PantryRepository(application);
        pantryItems = repository.getAllPantryItems();
    }

    public LiveData<List<PantryItem>> getPantryItems() {
        return pantryItems;
    }

    public void insert(PantryItem item) {
        repository.insert(item);
    }

    public void update(PantryItem item) {
        repository.update(item);
    }

    public void delete(PantryItem item) {
        repository.delete(item);
    }

    public void deleteById(long id) {
        repository.deleteById(id);
    }

    public void getItemById(long id, PantryRepository.Callback<PantryItem> callback) {
        repository.getById(id, callback);
    }
}
