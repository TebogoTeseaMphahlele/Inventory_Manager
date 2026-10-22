package com.example.smartpantry.ui.main;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.smartpantry.R;
import com.example.smartpantry.databinding.ActivityMainBinding;
import com.example.smartpantry.ui.pantry.PantryListFragment;
import com.example.smartpantry.ui.settings.SettingsFragment;
import com.example.smartpantry.ui.suggested.SuggestedRecipesFragment;
import com.google.android.material.navigation.NavigationBarView;

/**
 * Host activity containing bottom navigation bar and managing main screen fragments.
 */
public class MainActivity extends AppCompatActivity {

    private static final String KEY_SELECTED_TAB = "key_selected_tab";
    private ActivityMainBinding binding;
    private int selectedNavId = R.id.navigation_pantry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        if (savedInstanceState != null) {
            selectedNavId = savedInstanceState.getInt(KEY_SELECTED_TAB, R.id.navigation_pantry);
        }

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId != selectedNavId || getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment) == null) {
                    selectedNavId = itemId;
                    switchFragment(itemId);
                }
                return true;
            }
        });

        if (savedInstanceState == null) {
            binding.bottomNavigation.setSelectedItemId(selectedNavId);
        } else {
            binding.bottomNavigation.setSelectedItemId(selectedNavId);
        }
    }

    private void switchFragment(int navId) {
        Fragment fragment;
        if (navId == R.id.navigation_suggested) {
            fragment = new SuggestedRecipesFragment();
        } else if (navId == R.id.navigation_settings) {
            fragment = new SettingsFragment();
        } else {
            fragment = new PantryListFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SELECTED_TAB, selectedNavId);
    }
}
