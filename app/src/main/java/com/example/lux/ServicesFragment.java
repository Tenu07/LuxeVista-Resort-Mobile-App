package com.example.lux;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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

public class ServicesFragment extends Fragment {

    private static final String TAG = "ServicesFragment";

    private RecyclerView recyclerViewServices;
    private ServiceAdapter serviceAdapter;
    private List<Service> serviceList = new ArrayList<>();
    private DataRepository dataRepository;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        recyclerViewServices = view.findViewById(R.id.recyclerViewServices);
        progressBar = view.findViewById(R.id.progressBarServices);
        // ** Ensure dataRepository is initialized here **
        dataRepository = DataRepository.getInstance(requireActivity().getApplicationContext());

        setupRecyclerView();
        loadServices();

        return view;
    }


    private void setupRecyclerView() {
        recyclerViewServices.setLayoutManager(new LinearLayoutManager(getContext()));
        serviceAdapter = new ServiceAdapter(getContext(), serviceList);
        recyclerViewServices.setAdapter(serviceAdapter);
    }

    private void loadServices() {
        Log.d(TAG, "loadServices called"); // Log method start
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewServices.setVisibility(View.GONE);

        new android.os.Handler().postDelayed(() -> {
            Log.d(TAG, "Handler running loadServices"); // Log handler start
            if (dataRepository != null) {
                List<Service> fetchedServices = dataRepository.getAllServices();
                Log.d(TAG, "Fetched services count: " + (fetchedServices != null ? fetchedServices.size() : "null")); // Log count

                if (fetchedServices != null && !fetchedServices.isEmpty()) {
                    serviceList.clear();
                    serviceList.addAll(fetchedServices);
                    Log.d(TAG, "serviceList updated, size: " + serviceList.size()); // Log list size

                    if (serviceAdapter != null) {
                        Log.d(TAG, "Notifying ServiceAdapter."); // Log notification
                        serviceAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "serviceAdapter is NULL when trying to notify."); // Log error
                    }
                    recyclerViewServices.setVisibility(View.VISIBLE); // Ensure list is visible
                    // Optional: Hide empty text view if you have one
                } else {
                    Log.d(TAG, "No services fetched or list is empty."); // Log empty case
                    recyclerViewServices.setVisibility(View.GONE); // Hide list
                    // Optional: Show empty text view if you have one
                    Toast.makeText(getContext(), "No services found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "dataRepository is NULL inside Handler."); // Log error
                Toast.makeText(getContext(), "Error loading service data", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE); // Ensure progress bar is hidden
        }, 300);
    }
}