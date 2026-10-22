package com.example.smartpantry.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.example.smartpantry.data.db.AppDatabase;
import com.example.smartpantry.data.db.PantryItemDao;
import com.example.smartpantry.data.model.PantryItem;
import com.example.smartpantry.util.AppExecutors;
import java.util.List;

/**
 * Repository for managing pantry items data operations.
 */
public class PantryRepository {

    private final PantryItemDao pantryItemDao;
    private final AppExecutors executors;

    public PantryRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.pantryItemDao = db.pantryItemDao();
        this.executors = AppExecutors.getInstance();
    }

    public LiveData<List<PantryItem>> getAllPantryItems() {
        return pantryItemDao.getAllLive();
    }

    public void insert(final PantryItem item) {
        executors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                pantryItemDao.insert(item);
            }
        });
    }

    public void update(final PantryItem item) {
        executors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                pantryItemDao.update(item);
            }
        });
    }

    public void delete(final PantryItem item) {
        executors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                pantryItemDao.delete(item);
            }
        });
    }

    public void deleteById(final long id) {
        executors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                pantryItemDao.deleteById(id);
            }
        });
    }

    public interface Callback<T> {
        void onResult(T result);
    }

    public void getById(final long id, final Callback<PantryItem> callback) {
        executors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final PantryItem item = pantryItemDao.getByIdSync(id);
                executors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onResult(item);
                        }
                    }
                });
            }
        });
    }
}
