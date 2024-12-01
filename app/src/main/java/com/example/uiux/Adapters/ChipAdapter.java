package com.example.uiux.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.R;
import com.google.android.material.chip.Chip;

import java.util.List;

public class ChipAdapter extends RecyclerView.Adapter<ChipAdapter.ChipViewHolder> {
    private List<String> chipList;
    private Context context;

    public ChipAdapter(List<String> chipList, Context context) {
        this.chipList = chipList;
        this.context = context;
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
    }

    @Override
    public int getItemCount() {
        return chipList.size();
    }

    public static class ChipViewHolder extends RecyclerView.ViewHolder {
        Chip chip;

        public ChipViewHolder(View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.chip);
        }
    }
}

