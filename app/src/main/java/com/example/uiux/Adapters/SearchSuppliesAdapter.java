package com.example.uiux.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Activities.SupplyDetailActivity;
import com.example.uiux.Activities.User.Profile.EditAddressActivity;
import com.example.uiux.Model.Supplies;
import com.example.uiux.R;

import java.util.List;

public class SearchSuppliesAdapter extends RecyclerView.Adapter<SearchSuppliesAdapter.SearchSuppliesViewHolder> {
    private List<Supplies> searchSuppliesList;
    private Context context;

    public SearchSuppliesAdapter(List<Supplies> searchSuppliesList, Context context) {
        this.searchSuppliesList = searchSuppliesList;
        this.context = context;
    }

    @Override
    public SearchSuppliesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new SearchSuppliesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchSuppliesViewHolder holder, int position) {
        Supplies supply = searchSuppliesList.get(position);
        holder.tv_search_result_name.setText(supply.getName());
    }

    @Override
    public int getItemCount() {
        return searchSuppliesList.size();
    }

     class SearchSuppliesViewHolder extends RecyclerView.ViewHolder {
        TextView tv_search_result_name;

        public SearchSuppliesViewHolder(View itemView) {
            super(itemView);
            tv_search_result_name = itemView.findViewById(R.id.tv_search_result_name);

            itemView.setOnClickListener(view -> {
                Context context = itemView.getContext(); // Lấy context từ itemView
                int position = getBindingAdapterPosition();
                Supplies supplies = searchSuppliesList.get(position);
                Intent intent = new Intent(context, SupplyDetailActivity.class);
                intent.putExtra("supply_id", supplies.getSupplies_id());
                context.startActivity(intent);
            });
        }
    }
}

