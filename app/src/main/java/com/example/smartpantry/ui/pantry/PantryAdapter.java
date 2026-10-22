package com.example.smartpantry.ui.pantry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartpantry.R;
import com.example.smartpantry.data.model.PantryItem;
import com.example.smartpantry.databinding.ItemPantryBinding;
import com.example.smartpantry.util.DateUtils;
import com.example.smartpantry.util.Prefs;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying pantry items in a RecyclerView.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
        void onDeleteClick(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public PantryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<PantryItem> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPantryBinding binding = ItemPantryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PantryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class PantryViewHolder extends RecyclerView.ViewHolder {
        private final ItemPantryBinding binding;

        PantryViewHolder(ItemPantryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(final PantryItem item) {
            Context context = itemView.getContext();
            binding.textItemName.setText(item.getName());

            String quantityStr = formatQuantityDisplay(item.getQuantity()) + " " + item.getUnit();
            binding.textItemQuantity.setText(quantityStr);

            Long expiryDate = item.getExpiryDate();
            if (expiryDate != null && expiryDate > 0) {
                binding.textItemExpiry.setVisibility(View.VISIBLE);
                boolean isExpiringSoonAlerts = Prefs.isExpiringSoonAlertsEnabled(context);

                if (DateUtils.isExpired(expiryDate)) {
                    binding.textItemExpiry.setText(context.getString(R.string.label_expired) + " (" + DateUtils.formatDate(expiryDate) + ")");
                    binding.textItemExpiry.setTextColor(ContextCompat.getColor(context, R.color.color_expired));
                } else if (isExpiringSoonAlerts && DateUtils.isExpiringSoon(expiryDate, 3)) {
                    binding.textItemExpiry.setText(context.getString(R.string.label_expiring_soon) + " (" + DateUtils.formatDate(expiryDate) + ")");
                    binding.textItemExpiry.setTextColor(ContextCompat.getColor(context, R.color.color_expiring_soon));
                } else {
                    binding.textItemExpiry.setText("Expires: " + DateUtils.formatDate(expiryDate));
                    binding.textItemExpiry.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
                }
            } else {
                binding.textItemExpiry.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(item);
                    }
                }
            });

            binding.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onDeleteClick(item);
                    }
                }
            });
        }

        private String formatQuantityDisplay(double qty) {
            if (qty == (long) qty) {
                return String.format(Locale.getDefault(), "%d", (long) qty);
            } else {
                return String.format(Locale.getDefault(), "%.2f", qty);
            }
        }
    }
}
