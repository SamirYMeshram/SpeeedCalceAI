package com.yourname.speedcalcai.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yourname.speedcalcai.R;
import java.util.List;

public class RevisionAdapter extends RecyclerView.Adapter<RevisionAdapter.RevisionViewHolder> {
    public static class RevisionItem {
        public final String title;
        public final String body;
        public RevisionItem(String title, String body) { this.title = title; this.body = body; }
    }

    private final List<RevisionItem> items;
    public RevisionAdapter(List<RevisionItem> items) { this.items = items; }

    @NonNull
    @Override
    public RevisionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_revision, parent, false);
        return new RevisionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RevisionViewHolder holder, int position) {
        RevisionItem item = items.get(position);
        holder.tvTitle.setText(item.title);
        holder.tvBody.setText(item.body);
    }

    @Override public int getItemCount() { return items.size(); }

    static class RevisionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvBody;
        RevisionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvBody = itemView.findViewById(R.id.tvBody);
        }
    }
}
