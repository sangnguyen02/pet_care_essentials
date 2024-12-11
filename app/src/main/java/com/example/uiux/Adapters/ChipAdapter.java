package com.example.uiux.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.R;
import com.google.android.material.chip.Chip;

import java.util.List;

public class ChipAdapter extends RecyclerView.Adapter<ChipAdapter.ChipViewHolder> {
    private List<String> chipList;
    private Context context;
    private int selectedPosition = 0;

    private OnChipSelectedListener chipSelectedListener;

    public interface OnChipSelectedListener {
        void onChipSelected(String chipText);
    }


    public ChipAdapter(List<String> chipList, Context context, OnChipSelectedListener chipSelectedListener) {
        this.chipList = chipList;
        this.context = context;
        this.chipSelectedListener = chipSelectedListener;

    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int position) {
        if (position >= 0 && position < chipList.size()) {
            selectedPosition = position;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ChipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chip, parent, false);
        return new ChipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChipViewHolder holder, int position) {
        String chipText = chipList.get(position);
        holder.chip.setText(chipText);


        if (selectedPosition == holder.getBindingAdapterPosition()) {
            holder.chip.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.gradient)));
            holder.chip.setChipStrokeWidth(5);
        } else {
            holder.chip.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.light_bg)));
            holder.chip.setChipStrokeWidth(0);
        }


        holder.chip.setOnClickListener(v -> {
            if (chipSelectedListener != null) {
                selectedPosition = holder.getBindingAdapterPosition();
                chipSelectedListener.onChipSelected(chipText);
                notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        return chipList.size();
    }

    public static class ChipViewHolder extends RecyclerView.ViewHolder {
        public Chip chip;

        public ChipViewHolder(View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.chip);
        }

        public Chip getChip() {
            return chip;
        }
    }
}

