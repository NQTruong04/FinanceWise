package com.example.financewise.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import java.util.List;

public abstract class BaseAdapter<T, VB extends ViewBinding, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected List<T> items;
    protected Context context;
    protected LayoutInflater inflater;

    public BaseAdapter(Context context, List<T> items) {
        this.context = context;
        this.items = items;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public abstract VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        T item = items.get(position);
        bind(holder, item, position);
    }

    protected abstract void bind(@NonNull VH holder, T item, int position);

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void submitList(List<T> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    protected abstract VB createBinding(LayoutInflater inflater, ViewGroup parent);
}