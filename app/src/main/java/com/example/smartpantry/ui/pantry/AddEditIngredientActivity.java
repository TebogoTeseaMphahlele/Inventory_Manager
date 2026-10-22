package com.example.smartpantry.ui.pantry;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.smartpantry.R;
import com.example.smartpantry.data.model.PantryItem;
import com.example.smartpantry.data.repository.PantryRepository;
import com.example.smartpantry.databinding.ActivityAddEditIngredientBinding;
import com.example.smartpantry.logic.IngredientNormalizer;
import com.example.smartpantry.util.Constants;
import com.example.smartpantry.util.DateUtils;
import com.example.smartpantry.viewmodel.PantryViewModel;
import java.util.Calendar;

/**
 * Activity for adding a new ingredient or editing an existing one in the pantry.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    private ActivityAddEditIngredientBinding binding;
    private PantryViewModel viewModel;
    private long pantryItemId = -1;
    private PantryItem currentItem;
    private Long selectedExpiryDate = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditIngredientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewModel = new ViewModelProvider(this).get(PantryViewModel.class);

        // Populate Spinner with units array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.ingredient_units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerUnit.setAdapter(adapter);

        if (getIntent() != null && getIntent().hasExtra(Constants.EXTRA_PANTRY_ITEM_ID)) {
            pantryItemId = getIntent().getLongExtra(Constants.EXTRA_PANTRY_ITEM_ID, -1);
        }

        if (pantryItemId != -1) {
            setTitle(R.string.title_edit_ingredient);
            loadExistingItem(pantryItemId);
        } else {
            setTitle(R.string.title_add_ingredient);
            updateDateDisplay();
        }

        binding.btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        binding.btnClearDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedExpiryDate = null;
                updateDateDisplay();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveIngredient();
            }
        });
    }

    private void loadExistingItem(long itemId) {
        viewModel.getItemById(itemId, new PantryRepository.Callback<PantryItem>() {
            @Override
            public void onResult(PantryItem item) {
                if (item == null) {
                    finish();
                    return;
                }
                currentItem = item;
                binding.editName.setText(item.getName());
                binding.editQuantity.setText(String.valueOf(item.getQuantity()));

                // Pre-select unit in spinner
                if (item.getUnit() != null) {
                    ArrayAdapter adapter = (ArrayAdapter) binding.spinnerUnit.getAdapter();
                    if (adapter != null) {
                        int pos = adapter.getPosition(item.getUnit());
                        if (pos >= 0) {
                            binding.spinnerUnit.setSelection(pos);
                        }
                    }
                }

                selectedExpiryDate = item.getExpiryDate();
                updateDateDisplay();
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        if (selectedExpiryDate != null && selectedExpiryDate > 0) {
            calendar.setTimeInMillis(selectedExpiryDate);
        }

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);

                        selectedExpiryDate = cal.getTimeInMillis();
                        updateDateDisplay();

                        if (DateUtils.isExpired(selectedExpiryDate)) {
                            Toast.makeText(AddEditIngredientActivity.this,
                                    "Note: The selected expiry date is in the past.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void updateDateDisplay() {
        if (selectedExpiryDate != null && selectedExpiryDate > 0) {
            binding.textSelectedDate.setText(DateUtils.formatDate(selectedExpiryDate));
        } else {
            binding.textSelectedDate.setText("No date selected");
        }
    }

    private void saveIngredient() {
        binding.inputLayoutName.setError(null);
        binding.inputLayoutQuantity.setError(null);

        String rawName = binding.editName.getText() != null ? binding.editName.getText().toString().trim() : "";
        String rawQuantity = binding.editQuantity.getText() != null ? binding.editQuantity.getText().toString().trim() : "";
        String selectedUnit = binding.spinnerUnit.getSelectedItem() != null ? binding.spinnerUnit.getSelectedItem().toString() : "";

        boolean isValid = true;

        // Validation 1: Name required, 2-50 chars, must contain at least one letter
        if (TextUtils.isEmpty(rawName) || rawName.length() < 2 || rawName.length() > 50 || !rawName.matches(".*[a-zA-Z].*")) {
            binding.inputLayoutName.setError(getString(R.string.error_name_required));
            isValid = false;
        }

        // Validation 2: Quantity required, number > 0 and <= 100000
        double quantity = -1;
        try {
            quantity = Double.parseDouble(rawQuantity);
        } catch (NumberFormatException ignored) {}

        if (quantity <= 0 || quantity > 100000) {
            binding.inputLayoutQuantity.setError(getString(R.string.error_quantity_invalid));
            isValid = false;
        }

        // Validation 3: Unit must be selected
        if (TextUtils.isEmpty(selectedUnit)) {
            Toast.makeText(this, R.string.error_unit_required, Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        String normName = IngredientNormalizer.normalize(rawName);

        if (pantryItemId != -1 && currentItem != null) {
            currentItem.setName(rawName);
            currentItem.setNormalizedName(normName);
            currentItem.setQuantity(quantity);
            currentItem.setUnit(selectedUnit);
            currentItem.setExpiryDate(selectedExpiryDate);
            viewModel.update(currentItem);
        } else {
            PantryItem newItem = new PantryItem(rawName, normName, quantity, selectedUnit, selectedExpiryDate);
            viewModel.insert(newItem);
        }

        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
