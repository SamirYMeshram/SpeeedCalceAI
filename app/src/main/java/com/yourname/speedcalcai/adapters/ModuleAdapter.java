package com.yourname.speedcalcai.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yourname.speedcalcai.R;
import com.yourname.speedcalcai.models.ModuleItem;
import java.util.List;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder> {
    public interface OnModuleClickListener { void onModuleClick(ModuleItem item); }
    private final List<ModuleItem> items;
    private final OnModuleClickListener listener;

    public ModuleAdapter(List<ModuleItem> items, OnModuleClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_module, parent, false);
        return new ModuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        ModuleItem item = items.get(position);
        holder.tvIcon.setText(item.getIconText());
        holder.tvName.setText(item.getModuleName());
        holder.tvCategory.setText(item.getCategoryName());
        holder.itemView.setOnClickListener(v -> listener.onModuleClick(item));
    }

    @Override public int getItemCount() { return items.size(); }

    static class ModuleViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvName, tvCategory;
        ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tvIcon);
            tvName = itemView.findViewById(R.id.tvModuleName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }
}
