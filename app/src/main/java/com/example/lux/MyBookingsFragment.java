package com.example.lux;

import android.content.Context;
import android.os.Bundle;
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

public class MyBookingsFragment extends Fragment implements BookingAdapter.OnBookingCancelledListener {

    private RecyclerView recyclerViewMyBookings;
    private BookingAdapter bookingAdapter;
    private List<Booking> userBookingList = new ArrayList<>();
    private DataRepository dataRepository;
    private PrefsManager prefsManager;
    private ProgressBar progressBar;
    private TextView textViewNoBookings; // To show when the list is empty

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_bookings, container, false);

        recyclerViewMyBookings = view.findViewById(R.id.recyclerViewMyBookings);
        progressBar = view.findViewById(R.id.progressBarMyBookings);
        textViewNoBookings = view.findViewById(R.id.textViewNoBookings);

        // --- ENSURE THIS INITIALIZATION IS PRESENT AND CORRECT ---
        Context appContext = requireActivity().getApplicationContext();
        dataRepository = DataRepository.getInstance(appContext);
        // ---------------------------------------------------------

        // Initialize other things AFTER the repository if they depend on it indirectly
        prefsManager = new PrefsManager(requireContext());

        // Now it's safe to call methods that use dataRepository
        setupRecyclerView();
        loadUserBookings(); // Calls dataRepository.getBookingsByUserId internally

        return view;
    }

    private void setupRecyclerView() {
        recyclerViewMyBookings.setLayoutManager(new LinearLayoutManager(getContext()));
        // Pass 'this' as the listener for cancellation callbacks
        // Ensure adapter is initialized correctly too
        if (getContext() != null) {
            bookingAdapter = new BookingAdapter(getContext(), userBookingList, this);
            recyclerViewMyBookings.setAdapter(bookingAdapter);
        }
    }

    private void loadUserBookings() {
        String userId = prefsManager.getUserId(); // Needs prefsManager initialized

        if (userId == null) {
            // ... (handle not logged in state)
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        // ... (rest of the UI setup)

        new android.os.Handler().postDelayed(() -> {
            // Add null check for safety, although it should be initialized now
            if (dataRepository != null) {
                List<Booking> fetchedBookings = dataRepository.getBookingsByUserId(userId); // Should not crash now
                userBookingList.clear();

                if (fetchedBookings != null && !fetchedBookings.isEmpty()) {
                    userBookingList.addAll(fetchedBookings);
                    if (bookingAdapter != null) { // Check adapter too
                        bookingAdapter.notifyDataSetChanged();
                    }
                    // ... (update UI visibility)
                } else {
                    // ... (handle no bookings found)
                }
            } else {
                Toast.makeText(getContext(), "Error accessing booking data", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);

        }, 500);
    }


    // Implementation of the listener interface from BookingAdapter
    @Override
    public void onBookingCancelled(int position) {
        // Remove the cancelled booking from the local list and notify the adapter
        if (position >= 0 && position < userBookingList.size()) {
            userBookingList.remove(position);
            bookingAdapter.notifyItemRemoved(position);
            // Optional: Re-adjust positions if needed for subsequent items
            bookingAdapter.notifyItemRangeChanged(position, userBookingList.size());

            // Show empty message if the list becomes empty
            if (userBookingList.isEmpty()) {
                textViewNoBookings.setText("You have no active bookings.");
                textViewNoBookings.setVisibility(View.VISIBLE);
                recyclerViewMyBookings.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh bookings when the fragment becomes visible again,
        // in case a booking was made/cancelled elsewhere.
        loadUserBookings();
    }
}