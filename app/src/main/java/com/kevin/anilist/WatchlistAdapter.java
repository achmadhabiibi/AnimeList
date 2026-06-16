package com.kevin.anilist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.ViewHolder> {

    private List<Anime> animeList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Anime anime);
        void onDeleteClick(Anime anime);
    }

    public WatchlistAdapter(List<Anime> animeList, Context context, OnItemClickListener listener) {
        this.animeList = animeList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_watchlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Anime anime = animeList.get(position);
        holder.title.setText(anime.getTitle());
        holder.status.setText(anime.getStatus());

        Glide.with(context)
                .load(anime.getImageUrl())
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(anime));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(anime));
    }

    @Override
    public int getItemCount() {
        return animeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, status;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.watchlist_image);
            title = itemView.findViewById(R.id.watchlist_title);
            status = itemView.findViewById(R.id.watchlist_status);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
