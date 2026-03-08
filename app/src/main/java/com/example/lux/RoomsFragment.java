package com.example.lux;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RoomsFragment extends Fragment {

    private RecyclerView recyclerViewRooms;
    private RoomAdapter roomAdapter;
    private List<Room> roomList = new ArrayList<>();
    private DataRepository dataRepository;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rooms, container, false);

        recyclerViewRooms = view.findViewById(R.id.recyclerViewRooms);
        progressBar = view.findViewById(R.id.progressBarRooms);

        // --- ENSURE THIS INITIALIZATION IS PRESENT AND CORRECT ---
        Context appContext = requireActivity().getApplicationContext();
        dataRepository = DataRepository.getInstance(appContext);
        // ---------------------------------------------------------

        // Now that dataRepository is initialized, it's safe to call methods that use it.
        setupRecyclerView();
        loadRooms(); // Calls dataRepository.getAllRooms() internally

        return view;
    }

    private void setupRecyclerView() {
        // ... (setup code using context)
        recyclerViewRooms.setLayoutManager(new LinearLayoutManager(getContext()));
        // Adapter initialization might need context too, ensure it's correct
        roomAdapter = new RoomAdapter(getContext(), roomList);
        recyclerViewRooms.setAdapter(roomAdapter);
    }

    private void loadRooms() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewRooms.setVisibility(View.GONE);

        new android.os.Handler().postDelayed(() -> {
            // Check if dataRepository is still valid (optional, but good practice)
            if (dataRepository != null) {
                List<Room> fetchedRooms = dataRepository.getAllRooms(); // Should not crash now
                if (fetchedRooms != null && !fetchedRooms.isEmpty()) {
                    roomList.clear();
                    roomList.addAll(fetchedRooms);
                    // Ensure adapter is not null before notifying
                    if (roomAdapter != null) {
                        roomAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getContext(), "No rooms found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Error loading data repository", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
            recyclerViewRooms.setVisibility(View.VISIBLE);
        }, 500);
    }
}