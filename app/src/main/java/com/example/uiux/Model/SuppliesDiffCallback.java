package com.example.uiux.Model;

import androidx.recyclerview.widget.DiffUtil;
import com.example.uiux.Model.Supplies;
import java.util.List;

public class SuppliesDiffCallback extends DiffUtil.Callback {

    private final List<Supplies> oldList;
    private final List<Supplies> newList;

    public SuppliesDiffCallback(List<Supplies> oldList, List<Supplies> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // Kiểm tra xem các item có giống nhau không (thường so sánh id của đối tượng)
        return oldList.get(oldItemPosition).getSupplies_id() == newList.get(newItemPosition).getSupplies_id();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // Kiểm tra xem các nội dung của item có giống nhau không (so sánh tất cả các thuộc tính)
//        Supplies oldSupply = oldList.get(oldItemPosition);
//        Supplies newSupply = newList.get(newItemPosition);
//        return oldSupply.equals(newSupply);  // Đảm bảo Supplies có phương thức equals() phù hợp
        return oldList.get(oldItemPosition).getName() == newList.get(newItemPosition).getName();
    }
}
