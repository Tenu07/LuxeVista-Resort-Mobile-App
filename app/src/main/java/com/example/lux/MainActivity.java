package com.example.lux;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        // Load the default fragment
        if (savedInstanceState == null) {
            loadFragment(new RoomsFragment()); // Start with Rooms screen
        }
    }

    private final BottomNavigationView.OnItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_rooms) {
                    selectedFragment = new RoomsFragment();
                } else if (itemId == R.id.navigation_services) {
                    selectedFragment = new ServicesFragment();
                } else if (itemId == R.id.navigation_my_bookings) {
                    selectedFragment = new MyBookingsFragment();
                } else if (itemId == R.id.navigation_attractions) {
                    selectedFragment = new AttractionsFragment();
                } else if (itemId == R.id.navigation_profile) {
                    selectedFragment = new ProfileFragment();
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true; // Item selection handled
                }
                return false; // Item selection not handled
            };

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        // transaction.addToBackStack(null); // Optional: if you want back navigation between fragments
        transaction.commit();
    }
}