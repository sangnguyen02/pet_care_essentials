package com.example.uiux.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Model.Service;
import com.example.uiux.R;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {

    private final List<Calendar> days;
    private int selectedPosition = 0;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public DayAdapter(List<Calendar> days) {
        this.days = days;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        Calendar calendar = days.get(position);

        String day = new SimpleDateFormat("EEE", Locale.ENGLISH).format(calendar.getTime());

        String date = new SimpleDateFormat("dd", Locale.ENGLISH).format(calendar.getTime());
        String month = new SimpleDateFormat("dd", Locale.ENGLISH).format(calendar.getTime());
        String year = new SimpleDateFormat("dd", Locale.ENGLISH).format(calendar.getTime());

        holder.tvDay.setText(day);
        holder.tvDate.setText(date);


        if (selectedPosition == holder.getBindingAdapterPosition()) {
            holder.mcv_day.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gradient));
            holder.mcv_day.setStrokeWidth(5);
        } else {
            holder.mcv_day.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.light_bg));
            holder.mcv_day.setStrokeWidth(1);
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            int clickedPosition = holder.getBindingAdapterPosition();
            if (clickedPosition != RecyclerView.NO_POSITION) {
                selectedPosition = clickedPosition;
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onItemClick(clickedPosition);
                    Log.e("Log","Test");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return days.size();
    }
    public String getSelectedDay() {
        if (selectedPosition != -1) {
            // Lấy đối tượng Calendar của ngày đã chọn
            Calendar selectedCalendar = days.get(selectedPosition);

            // Định dạng ngày đã chọn thành chuỗi
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd/MM/yyyy", Locale.ENGLISH); // Ví dụ: "Mon, 12/12/2024"
            return dateFormat.format(selectedCalendar.getTime());
        }
        return null;  // Nếu không có ngày nào được chọn
    }


    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvDate;
        MaterialCardView mcv_day;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            mcv_day = itemView.findViewById(R.id.mcv_day);
            tvDay = itemView.findViewById(R.id.tv_day);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }
}

