package com.kevin.anilist;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WatchlistFragment extends Fragment {

    private RecyclerView recyclerView;
    private WatchlistAdapter adapter;
    private List<Anime> watchlist = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private SwipeRefreshLayout swipeRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watchlist, container, false);
        dbHelper = new DatabaseHelper(getContext());
        
        recyclerView = view.findViewById(R.id.recycler_view_watchlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        swipeRefresh = view.findViewById(R.id.swipe_refresh_watchlist);
        if (swipeRefresh != null) {
            swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.primary_blue));
            swipeRefresh.setOnRefreshListener(this::loadWatchlist);
        }

        loadWatchlist();
        return view;
    }

    private void loadWatchlist() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (swipeRefresh != null && !swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(true);
                }
            });
        }
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Anime> listFromDb = dbHelper.getWatchlist();
            
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    watchlist.clear();
                    if (listFromDb != null) {
                        watchlist.addAll(listFromDb);
                    }

                    if (swipeRefresh != null) {
                        swipeRefresh.setRefreshing(false);
                    }
                    
                    adapter = new WatchlistAdapter(watchlist, getContext(), new WatchlistAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Anime anime) {
                            showUpdateDialog(anime);
                        }

                        @Override
                        public void onDeleteClick(Anime anime) {
                            deleteFromWatchlist(anime.getId());
                        }
                    });
                    recyclerView.setAdapter(adapter);
                });
            }
        });
    }

    private void showUpdateDialog(Anime anime) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Update Status");

        String[] statuses = {"Plan to Watch", "Watching", "Completed"};
        Spinner spinner = new Spinner(getContext());
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, statuses);
        spinner.setAdapter(spinnerAdapter);
        
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equals(anime.getStatus())) {
                spinner.setSelection(i);
            }
        }

        builder.setView(spinner);
        builder.setPositiveButton("Update", (dialog, which) -> {
            String newStatus = spinner.getSelectedItem().toString();
            updateStatusInDb(anime.getId(), newStatus);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void updateStatusInDb(int id, String status) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            dbHelper.updateStatus(id, status);
            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Status Diperbarui", Toast.LENGTH_SHORT).show();
                    loadWatchlist();
                });
            }
        });
    }

    private void deleteFromWatchlist(int id) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            dbHelper.deleteAnime(id);
            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Dihapus dari Watchlist", Toast.LENGTH_SHORT).show();
                    loadWatchlist();
                });
            }
        });
    }
}
