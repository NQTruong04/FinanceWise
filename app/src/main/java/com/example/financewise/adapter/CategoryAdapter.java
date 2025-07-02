package com.example.financewise.adapter;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financewise.R;
import com.example.financewise.viewmodel.CategoriesViewModel;

public class CategoryAdapter extends ListAdapter<CategoriesViewModel.CategoryItem, CategoryAdapter.CategoryViewHolder> {

    private static final DiffUtil.ItemCallback<CategoriesViewModel.CategoryItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<CategoriesViewModel.CategoryItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull CategoriesViewModel.CategoryItem oldItem, @NonNull CategoriesViewModel.CategoryItem newItem) {
            return oldItem.getName().equals(newItem.getName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull CategoriesViewModel.CategoryItem oldItem, @NonNull CategoriesViewModel.CategoryItem newItem) {
            return oldItem.getName().equals(newItem.getName()) && oldItem.getIconResId() == newItem.getIconResId();
        }
    };
    private final OnItemClickListener listener;

    public CategoryAdapter(OnItemClickListener listener){
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryViewHolder holder, int position) {
        CategoriesViewModel.CategoryItem item = getItem(position);
        holder.bind(item, listener);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder{
        private final ImageView ivCategoryIcon;
        private final TextView tvCategoryName;
        public CategoryViewHolder(@NonNull View parent) {
            super(parent);
            ivCategoryIcon = parent.findViewById(R.id.ivCategoryIcon);
            tvCategoryName = parent.findViewById(R.id.tvCategoryName);
        }
        public void bind(CategoriesViewModel.CategoryItem item, OnItemClickListener listener) {
            ivCategoryIcon.setImageResource(item.getIconResId());
            tvCategoryName.setText(item.getName());
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
    public interface OnItemClickListener {
        void onItemClick(CategoriesViewModel.CategoryItem item);
    }
}
