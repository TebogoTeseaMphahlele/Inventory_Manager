package com.example.smartpantry.ui.pantry;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.smartpantry.R;
import com.example.smartpantry.data.model.PantryItem;
import com.example.smartpantry.databinding.FragmentPantryListBinding;
import com.example.smartpantry.util.Constants;
import com.example.smartpantry.viewmodel.PantryViewModel;
import java.util.List;

/**
 * Fragment displaying the list of ingredients in the user's pantry.
 */
public class PantryListFragment extends Fragment implements PantryAdapter.OnItemClickListener {

    private FragmentPantryListBinding binding;
    private PantryViewModel viewModel;
    private PantryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPantryListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PantryViewModel.class);

        adapter = new PantryAdapter(this);
        binding.recyclerPantry.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerPantry.setAdapter(adapter);

        viewModel.getPantryItems().observe(getViewLifecycleOwner(), new Observer<List<PantryItem>>() {
            @Override
            public void onChanged(List<PantryItem> items) {
                if (items == null || items.isEmpty()) {
                    binding.recyclerPantry.setVisibility(View.GONE);
                    binding.emptyState.getRoot().setVisibility(View.VISIBLE);
                    binding.emptyState.textEmptyMessage.setText(R.string.empty_pantry_message);
                } else {
                    binding.recyclerPantry.setVisibility(View.VISIBLE);
                    binding.emptyState.getRoot().setVisibility(View.GONE);
                    adapter.setItems(items);
                }
            }
        });

        binding.fabAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), AddEditIngredientActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(PantryItem item) {
        Intent intent = new Intent(requireContext(), AddEditIngredientActivity.class);
        intent.putExtra(Constants.EXTRA_PANTRY_ITEM_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(final PantryItem item) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_ingredient_title)
                .setMessage(R.string.delete_ingredient_confirm)
                .setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.delete(item);
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
