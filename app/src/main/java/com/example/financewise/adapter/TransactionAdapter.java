package com.example.financewise.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financewise.R;
import com.example.financewise.databinding.ItemTransactionBinding;
import com.example.financewise.viewmodel.HomeViewModel;
import com.example.financewise.viewmodel.TransactionViewModel;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends BaseAdapter<HomeViewModel.TransactionItem, ItemTransactionBinding, TransactionAdapter.TransactionViewHolder> {
    private final String[] categories = context.getResources().getStringArray(R.array.category_array);

    public TransactionAdapter(Context context, List<HomeViewModel.TransactionItem> transactionItemList){
        super(context, transactionItemList);
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransactionBinding binding = createBinding(inflater, parent);
        return new TransactionViewHolder(binding);
    }


    @Override
    protected void bind(@NonNull TransactionViewHolder holder, HomeViewModel.TransactionItem item, int position) {
        holder.binding.tvCategoryName.setText(item.getCategory());
        holder.binding.tvDate.setText(formatDate(item.getDate()));
        holder.binding.tvTitle.setText(item.getTitle());
        holder.binding.tvAmount.setText(String.format("%,d", item.getAmount()));
        setAmountColor(holder.binding.tvAmount, item.getAmount());
        // Set icon based on category
        int iconRes = getCategoryIcon(item.getCategory());
        holder.binding.ivCategoryIcon.setImageResource(iconRes);
    }

    @Override
    protected ItemTransactionBinding createBinding(LayoutInflater inflater, ViewGroup parent) {
        return ItemTransactionBinding.inflate(inflater, parent, false);
    }
    private int getCategoryIcon(String category) {
        switch (category.toLowerCase()) {
            case "salary": return R.drawable.ic_salary;
            case "food": return R.drawable.ic_food_white;
            case "medicine": return R.drawable.ic_medicine;
            case "transport": return R.drawable.ic_transport;
            case "groceries": return R.drawable.ic_groceries;
            case "rent": return R.drawable.ic_rent;
            case "entertainment": return R.drawable.ic_entertainment;
            case "gift": return R.drawable.ic_gifts;
            case "other": return R.drawable.ic_more;
            default: return R.drawable.ic_white_salary;
        }
    }
    private void setAmountColor(TextView tvAmount, long amount){
        if(amount >= 0){
            tvAmount.setTextColor(ContextCompat.getColor(context, R.color.black));
        }else{
            tvAmount.setTextColor(ContextCompat.getColor(context, R.color.ocean_blue));
        }
    }
    private String formatDate(Timestamp timestamp){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - MMMM dd", Locale.ENGLISH);
        return sdf.format(timestamp.toDate());
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        ItemTransactionBinding binding;
        public TransactionViewHolder(@NonNull ItemTransactionBinding binding){
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
