package com.example.lux;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private TextView textViewProfileName, textViewProfileEmail;
    private Button buttonLogout, buttonEditProfile; // Add buttonEditProfile variable
    private PrefsManager prefsManager;
    private DataRepository dataRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // --- THIS IS THE LOCATION TO INITIALIZE ---
        // Get the application context from the hosting activity
        Context appContext = requireActivity().getApplicationContext();
        dataRepository = DataRepository.getInstance(appContext);
        // -----------------------------------------

        // Initialize other variables like PrefsManager, Views, etc.
        prefsManager = new PrefsManager(requireContext());
        textViewProfileName = view.findViewById(R.id.textViewProfileName);
        textViewProfileEmail = view.findViewById(R.id.textViewProfileEmail);
        buttonLogout = view.findViewById(R.id.buttonLogout);
        buttonEditProfile = view.findViewById(R.id.buttonEditProfile);

        // Now you can safely use dataRepository
        loadUserProfile();

        // Set listeners
        buttonLogout.setOnClickListener(v -> logoutUser());
        buttonEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadUserProfile() {
        String userId = prefsManager.getUserId();
        if (userId != null) {
            User currentUser = dataRepository.getUserById(userId);
            if (currentUser != null) {
                textViewProfileName.setText(currentUser.getName());
                textViewProfileEmail.setText(currentUser.getEmail());
            } else {
                Toast.makeText(getContext(), "Error loading profile", Toast.LENGTH_SHORT).show();
                logoutUser();
            }
        } else {
            Toast.makeText(getContext(), "Not logged in", Toast.LENGTH_SHORT).show();
            logoutUser();
        }
    }

    // Reload profile data when returning from EditProfileActivity
    @Override
    public void onResume() {
        super.onResume();
        loadUserProfile(); // Refresh data in case it was changed
    }

    private void logoutUser() {
        prefsManager.logoutUser();
        Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}