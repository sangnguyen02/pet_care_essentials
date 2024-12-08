package com.example.uiux.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.R;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.DayViewHolder> {

    private final List<Calendar> times;
    private int selectedPosition = 0;

    public TimeAdapter(List<Calendar> times) {
        this.times = times;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        Calendar calendar = times.get(position);
        int startHour = 8 + position; // 8, 9, 10, ...
        int endHour = startHour + 1;

        calendar.set(Calendar.HOUR_OF_DAY, startHour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        String fromTime = new SimpleDateFormat("h.a", Locale.ENGLISH).format(calendar.getTime());

        calendar.set(Calendar.HOUR_OF_DAY, endHour);
        String toTime = new SimpleDateFormat("h.a", Locale.ENGLISH).format(calendar.getTime());

        holder.tv_from_time.setText(fromTime);
        holder.tv_to_time.setText(toTime);

        if (selectedPosition == holder.getBindingAdapterPosition()) {
            holder.mcv_time.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gradient));
            holder.mcv_time.setStrokeWidth(5);
        } else {
            holder.mcv_time.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.light_bg));
            holder.mcv_time.setStrokeWidth(1);
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                selectedPosition = adapterPosition;
                notifyDataSetChanged();
            }
        });
    }



    @Override
    public int getItemCount() {
        return times.size();
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tv_from_time, tv_to_time;
        MaterialCardView mcv_time;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            mcv_time = itemView.findViewById(R.id.mcv_time);
            tv_from_time = itemView.findViewById(R.id.tv_from_time);
            tv_to_time = itemView.findViewById(R.id.tv_to_time);
        }
    }
}

