package com.example.lux;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AttractionsFragment extends Fragment {

    private static final String TAG = "AttractionsFragment";

    private RecyclerView recyclerViewAttractions;
    private AttractionAdapter attractionAdapter;
    private List<Attraction> attractionList = new ArrayList<>();
    private DataRepository dataRepository;
    private ProgressBar progressBar;
    private TextView textViewNoAttractions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attractions, container, false);

        recyclerViewAttractions = view.findViewById(R.id.recyclerViewAttractions);
        progressBar = view.findViewById(R.id.progressBarAttractions);
        textViewNoAttractions = view.findViewById(R.id.textViewNoAttractions);
        // ** Ensure dataRepository is initialized here **
        dataRepository = DataRepository.getInstance(requireActivity().getApplicationContext());

        setupRecyclerView();
        loadAttractions();

        return view;
    }

    private void setupRecyclerView() {
        recyclerViewAttractions.setLayoutManager(new LinearLayoutManager(getContext()));
        attractionAdapter = new AttractionAdapter(getContext(), attractionList);
        recyclerViewAttractions.setAdapter(attractionAdapter);
    }

    private void loadAttractions() {
        Log.d(TAG, "loadAttractions called"); // Log method start
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewAttractions.setVisibility(View.GONE);
        textViewNoAttractions.setVisibility(View.GONE);

        new android.os.Handler().postDelayed(() -> {
            Log.d(TAG, "Handler running loadAttractions"); // Log handler start
            if (dataRepository != null) {
                List<Attraction> fetchedAttractions = dataRepository.getAllAttractions();
                Log.d(TAG, "Fetched attractions count: " + (fetchedAttractions != null ? fetchedAttractions.size() : "null")); // Log count

                attractionList.clear(); // Clear even if fetched is null/empty

                if (fetchedAttractions != null && !fetchedAttractions.isEmpty()) {
                    attractionList.addAll(fetchedAttractions);
                    Log.d(TAG, "attractionList updated, size: " + attractionList.size()); // Log list size

                    if (attractionAdapter != null) {
                        Log.d(TAG, "Notifying AttractionAdapter."); // Log notification
                        attractionAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "attractionAdapter is NULL when trying to notify."); // Log error
                    }
                    textViewNoAttractions.setVisibility(View.GONE);
                    recyclerViewAttractions.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "No attractions fetched or list is empty."); // Log empty case
                    textViewNoAttractions.setVisibility(View.VISIBLE); // Show empty message
                    recyclerViewAttractions.setVisibility(View.GONE); // Hide list
                }
            } else {
                Log.e(TAG, "dataRepository is NULL inside Handler."); // Log error
                Toast.makeText(getContext(), "Error loading attraction data", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE); // Ensure progress bar is hidden

        }, 300);
    }
}