package com.kelompok2.anilist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AnimeAdapter extends RecyclerView.Adapter<AnimeAdapter.ViewHolder> {

    private List<Anime> animeList;
    private Context context;

    public AnimeAdapter(List<Anime> animeList, Context context) {
        this.animeList = animeList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anime, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Anime anime = animeList.get(position);
        holder.title.setText(anime.getTitle());
        holder.score.setText("Score: " + anime.getScore());

        Glide.with(context)
                .load(anime.getImageUrl())
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("anime", anime);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return animeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, score;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.anime_image);
            title = itemView.findViewById(R.id.anime_title);
            score = itemView.findViewById(R.id.anime_score);
        }
    }
}
