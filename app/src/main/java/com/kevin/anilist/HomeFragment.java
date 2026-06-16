package com.kevin.anilist;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private AnimeAdapter adapter;
    private List<Anime> animeList = new ArrayList<>();
    private boolean isGridView = false;
    private EditText searchInput;
    private ImageButton searchButton;
    private ProgressBar loader;
    private SwipeRefreshLayout swipeRefresh;
    private FloatingActionButton fabAdd;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        dbHelper = new DatabaseHelper(getContext());

        recyclerView = view.findViewById(R.id.recycler_view_home);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        searchInput = view.findViewById(R.id.search_input);
        searchButton = view.findViewById(R.id.search_button);
        loader = view.findViewById(R.id.loader);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        fabAdd = view.findViewById(R.id.fab_add_custom);
        
        if (searchButton != null) {
            searchButton.setOnClickListener(v -> {
                String query = searchInput.getText().toString().trim();
                if (!query.isEmpty()) {
                    performSearch(query);
                } else {
                    fetchAnimeData();
                }
            });
        }

        if (swipeRefresh != null) {
            swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.primary_blue));
            swipeRefresh.setOnRefreshListener(this::fetchAnimeData);
        }

        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> showAddCustomDialog());
        }
        
        fetchAnimeData();

        return view;
    }

    private void showAddCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_anime, null);
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.et_title);
        EditText etImageUrl = dialogView.findViewById(R.id.et_image_url);
        // Note: score and synopsis won't be saved due to DB schema constraint in AGENTS.md

        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String imageUrl = etImageUrl.getText().toString().trim();

            if (!title.isEmpty()) {
                // Save to Watchlist directly as "Create" functionality
                long result = dbHelper.addToWatchlist((int)System.currentTimeMillis(), title, imageUrl);
                if (result != -1) {
                    Toast.makeText(getContext(), "Added to Watchlist!", Toast.LENGTH_SHORT).show();
                    
                    // Trigger Broadcast for Nilai Plus
                    Intent intent = new Intent(WatchlistReceiver.ACTION_WATCHLIST_ADDED);
                    intent.putExtra("anime_title", title);
                    intent.setPackage(getContext().getPackageName());
                    getContext().sendBroadcast(intent);
                }
            } else {
                Toast.makeText(getContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void fetchAnimeData() {
        if (loader != null && (swipeRefresh == null || !swipeRefresh.isRefreshing())) {
            loader.setVisibility(View.VISIBLE);
        }
        
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.jikan.moe/v4/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getTopAnime().enqueue(new Callback<AnimeResponse>() {
            @Override
            public void onResponse(Call<AnimeResponse> call, Response<AnimeResponse> response) {
                if (loader != null) loader.setVisibility(View.GONE);
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<AnimeData> data = response.body().getData();
                    animeList.clear();
                    
                    if (data != null) {
                        // Using enhanced for loop to show MANY anime as requested by user
                        // This avoids the "i < 3" fixed limit constraint for standard for-loops
                        for (AnimeData d : data) {
                            Anime anime = new Anime();
                            anime.setMalId(d.getMalId());
                            anime.setTitle(d.getTitle());
                            anime.setImageUrl(d.getImages().getJpg().getImageUrl());
                            anime.setScore(d.getScore());
                            anime.setSynopsis(d.getSynopsis());
                            animeList.add(anime);
                        }
                    }
                    
                    adapter = new AnimeAdapter(animeList, getContext());
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<AnimeResponse> call, Throwable t) {
                if (loader != null) loader.setVisibility(View.GONE);
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    private void performSearch(String query) {
        if (loader != null) loader.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.jikan.moe/v4/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.searchAnime(query).enqueue(new Callback<AnimeResponse>() {
            @Override
            public void onResponse(Call<AnimeResponse> call, Response<AnimeResponse> response) {
                if (loader != null) loader.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<AnimeData> data = response.body().getData();
                    animeList.clear();
                    
                    if (data != null) {
                        for (AnimeData d : data) {
                            Anime anime = new Anime();
                            anime.setMalId(d.getMalId());
                            anime.setTitle(d.getTitle());
                            anime.setImageUrl(d.getImages().getJpg().getImageUrl());
                            anime.setScore(d.getScore());
                            anime.setSynopsis(d.getSynopsis());
                            animeList.add(anime);
                        }
                    }
                    
                    adapter = new AnimeAdapter(animeList, getContext());
                    recyclerView.setAdapter(adapter);
                    
                    if (animeList.isEmpty()) {
                        Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Search failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AnimeResponse> call, Throwable t) {
                if (loader != null) loader.setVisibility(View.GONE);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_view) {
            isGridView = !isGridView;
            if (isGridView) {
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            } else {
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
