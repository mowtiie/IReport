package com.mowtiie.ireport.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mowtiie.ireport.databinding.ItemRowBinding;

import java.util.List;

public class GenericAdapter<T> extends RecyclerView.Adapter<GenericAdapter.RowHolder> {

    public interface Binder<T> {
        void bind(ItemRowBinding row, T item);
    }

    private final Binder<T> binder;
    private List<T> items;

    public GenericAdapter(List<T> items, Binder<T> binder) {
        this.items = items;
        this.binder = binder;
    }

    public void setItems(List<T> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RowHolder(ItemRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RowHolder holder, int position) {
        binder.bind(holder.binding, items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class RowHolder extends RecyclerView.ViewHolder {

        final ItemRowBinding binding;

        RowHolder(ItemRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
