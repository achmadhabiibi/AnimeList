package com.kevin.anilist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dbHelper = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        ImageView imageView = findViewById(R.id.detail_image);
        TextView titleView = findViewById(R.id.detail_title);
        TextView scoreView = findViewById(R.id.detail_score);
        TextView synopsisView = findViewById(R.id.detail_synopsis);
        Button btnAdd = findViewById(R.id.btn_add_watchlist);

        Anime anime = (Anime) getIntent().getSerializableExtra("anime");

        if (anime != null) {
            collapsingToolbarLayout.setTitle(anime.getTitle());
            if (titleView != null) {
                titleView.setText(anime.getTitle());
            }
            
            scoreView.setText("Score: " + anime.getScore());
            synopsisView.setText(anime.getSynopsis());
            Glide.with(this).load(anime.getImageUrl()).into(imageView);

            btnAdd.setOnClickListener(v -> {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    if (dbHelper.isAnimeInWatchlist(anime.getMalId())) {
                        runOnUiThread(() -> Toast.makeText(DetailActivity.this, "Already in Watchlist", Toast.LENGTH_SHORT).show());
                        return;
                    }
                    
                    long result = dbHelper.addToWatchlist(anime.getMalId(), anime.getTitle(), anime.getImageUrl());
                    runOnUiThread(() -> {
                        if (result != -1) {
                            Toast.makeText(DetailActivity.this, "Added to Watchlist", Toast.LENGTH_SHORT).show();
                            
                            // BROADCAST RECEIVER (Nilai Plus)
                            Intent broadcastIntent = new Intent(WatchlistReceiver.ACTION_WATCHLIST_ADDED);
                            broadcastIntent.putExtra("anime_title", anime.getTitle());
                            broadcastIntent.setPackage(getPackageName());
                            sendBroadcast(broadcastIntent);

                        } else {
                            Toast.makeText(DetailActivity.this, "Error adding to Watchlist", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
