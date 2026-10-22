package com.example.smartpantry.data.db;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.smartpantry.data.model.PantryItem;
import com.example.smartpantry.data.model.Recipe;
import com.example.smartpantry.data.model.RecipeIngredient;
import com.example.smartpantry.data.seed.RecipeSeeder;
import com.example.smartpantry.util.AppExecutors;

/**
 * Singleton Room Database for Smart Pantry Manager.
 */
@Database(
    entities = {PantryItem.class, Recipe.class, RecipeIngredient.class},
    version = 1,
    exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static volatile AppDatabase INSTANCE;

    public abstract PantryItemDao pantryItemDao();
    public abstract RecipeDao recipeDao();

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                    )
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    RecipeSeeder.seedDatabase(context.getApplicationContext(), INSTANCE);
                                }
                            });
                        }
                    })
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
