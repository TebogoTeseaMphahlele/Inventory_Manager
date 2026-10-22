package com.example.smartpantry.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.smartpantry.R;
import com.example.smartpantry.databinding.FragmentSettingsBinding;
import com.example.smartpantry.util.Prefs;

/**
 * Fragment for managing application settings such as expiry alert warnings and display unit system.
 */
public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set initial state from Prefs
        boolean alertsEnabled = Prefs.isExpiringSoonAlertsEnabled(requireContext());
        binding.switchExpiringSoon.setChecked(alertsEnabled);

        String unitsPref = Prefs.getUnitsPreference(requireContext());
        if (Prefs.UNIT_PREF_IMPERIAL.equalsIgnoreCase(unitsPref)) {
            binding.radioImperial.setChecked(true);
        } else {
            binding.radioMetric.setChecked(true);
        }

        // Listen for changes
        binding.switchExpiringSoon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Prefs.setExpiringSoonAlertsEnabled(requireContext(), isChecked);
            }
        });

        binding.radioGroupUnits.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_imperial) {
                    Prefs.setUnitsPreference(requireContext(), Prefs.UNIT_PREF_IMPERIAL);
                } else {
                    Prefs.setUnitsPreference(requireContext(), Prefs.UNIT_PREF_METRIC);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
