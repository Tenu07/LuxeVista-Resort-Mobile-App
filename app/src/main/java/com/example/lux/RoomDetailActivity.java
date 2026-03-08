package com.example.lux;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // Optional: Add a Toolbar for back navigation

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RoomDetailActivity extends AppCompatActivity {

    private ImageView imageViewRoomDetail;
    private TextView textViewRoomNameDetail, textViewRoomPriceDetail, textViewRoomDescriptionDetail;
    private TextView textViewCheckInDate, textViewCheckOutDate;
    private Button buttonBookRoom;

    private DataRepository dataRepository;
    private PrefsManager prefsManager;
    private Room currentRoom;
    private String roomId;

    private Calendar checkInCalendar = Calendar.getInstance();
    private Calendar checkOutCalendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        // Optional: Add Toolbar for back button
        // Toolbar toolbar = findViewById(R.id.toolbar); // Add toolbar to your layout
        // setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setTitle("Room Details");

        dataRepository = DataRepository.getInstance(getApplicationContext());
        prefsManager = new PrefsManager(this);

        imageViewRoomDetail = findViewById(R.id.imageViewRoomDetail);
        textViewRoomNameDetail = findViewById(R.id.textViewRoomNameDetail);
        textViewRoomPriceDetail = findViewById(R.id.textViewRoomPriceDetail);
        textViewRoomDescriptionDetail = findViewById(R.id.textViewRoomDescriptionDetail);
        textViewCheckInDate = findViewById(R.id.textViewCheckInDate);
        textViewCheckOutDate = findViewById(R.id.textViewCheckOutDate);
        buttonBookRoom = findViewById(R.id.buttonBookRoom);

        // Get Room ID passed from the adapter
        roomId = getIntent().getStringExtra("ROOM_ID");
        if (roomId == null) {
            Toast.makeText(this, "Error: Room details not found.", Toast.LENGTH_LONG).show();
            finish(); // Close activity if no ID is provided
            return;
        }

        loadRoomDetails();
        setupDatePickers();

        buttonBookRoom.setOnClickListener(v -> attemptBooking());

    }

    private void loadRoomDetails() {
        currentRoom = dataRepository.getRoomById(roomId);
        if (currentRoom != null) {
            textViewRoomNameDetail.setText(currentRoom.getName());
            textViewRoomPriceDetail.setText(String.format(Locale.getDefault(), "$%.2f / night", currentRoom.getPricePerNight()));
            textViewRoomDescriptionDetail.setText(currentRoom.getDescription());

            // --- Load image using Glide ---
            Glide.with(this) // Use 'this' for Activity context
                    .load(currentRoom.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .centerCrop()
                    .into(imageViewRoomDetail);
            // ----------------------------

            // ... (availability check remains the same)
            if (!currentRoom.isAvailable()) {
                buttonBookRoom.setEnabled(false);
                buttonBookRoom.setText("Currently Unavailable");
                textViewCheckInDate.setEnabled(false);
                textViewCheckOutDate.setEnabled(false);
            }

        } else {
            Toast.makeText(this, "Error loading room details.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupDatePickers() {
        // Set minimum date to today for check-in
        long todayMillis = System.currentTimeMillis() - 1000; // Allow today

        DatePickerDialog.OnDateSetListener checkInDateListener = (view, year, month, dayOfMonth) -> {
            checkInCalendar.set(Calendar.YEAR, year);
            checkInCalendar.set(Calendar.MONTH, month);
            checkInCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(textViewCheckInDate, checkInCalendar);

            // Automatically set checkout minimum date to day after check-in
            checkOutCalendar.setTimeInMillis(checkInCalendar.getTimeInMillis());
            checkOutCalendar.add(Calendar.DAY_OF_MONTH, 1); // Minimum one night stay
            updateLabel(textViewCheckOutDate, checkOutCalendar); // Update checkout display as well
        };

        DatePickerDialog.OnDateSetListener checkOutDateListener = (view, year, month, dayOfMonth) -> {
            checkOutCalendar.set(Calendar.YEAR, year);
            checkOutCalendar.set(Calendar.MONTH, month);
            checkOutCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(textViewCheckOutDate, checkOutCalendar);
        };


        textViewCheckInDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(RoomDetailActivity.this, checkInDateListener,
                    checkInCalendar.get(Calendar.YEAR), checkInCalendar.get(Calendar.MONTH),
                    checkInCalendar.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMinDate(todayMillis); // Can't book past dates
            dialog.show();
        });

        textViewCheckOutDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(RoomDetailActivity.this, checkOutDateListener,
                    checkOutCalendar.get(Calendar.YEAR), checkOutCalendar.get(Calendar.MONTH),
                    checkOutCalendar.get(Calendar.DAY_OF_MONTH));
            // Ensure checkout date is after check-in date
            dialog.getDatePicker().setMinDate(checkInCalendar.getTimeInMillis() + 86400000); // Minimum one day after check-in
            dialog.show();
        });
    }

    private void updateLabel(TextView textView, Calendar calendar) {
        textView.setText(dateFormat.format(calendar.getTime()));
    }


    private void attemptBooking() {
        String userId = prefsManager.getUserId();
        if (userId == null) {
            Toast.makeText(this, "Please log in to book a room.", Toast.LENGTH_LONG).show();
            // Optional: Redirect to LoginActivity
            // Intent intent = new Intent(this, LoginActivity.class);
            // startActivity(intent);
            return;
        }

        if (currentRoom == null) {
            Toast.makeText(this, "Cannot book, room details unavailable.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate selected dates
        String checkInStr = textViewCheckInDate.getText().toString();
        String checkOutStr = textViewCheckOutDate.getText().toString();
        Date checkInDate = null;
        Date checkOutDate = null;

        if (checkInStr.equals(getString(R.string.select_date_prompt)) || checkOutStr.equals(getString(R.string.select_date_prompt))) {
            Toast.makeText(this, "Please select check-in and check-out dates.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            checkInDate = dateFormat.parse(checkInStr);
            checkOutDate = dateFormat.parse(checkOutStr);
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format selected.", Toast.LENGTH_SHORT).show();
            return;
        }


        // Basic validation: check-out must be after check-in
        if (checkInDate == null || checkOutDate == null || !checkOutDate.after(checkInDate)) {
            Toast.makeText(this, "Check-out date must be after check-in date.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Perform Booking via DataRepository ---
        Booking newBooking = dataRepository.createBooking(
                userId,
                currentRoom.getRoomId(),
                currentRoom.getName(),
                "Room", // Item Type
                checkInDate,
                checkOutDate
        );

        if (newBooking != null) {
            Toast.makeText(this, "Room booked successfully! Booking ID: " + newBooking.getBookingId(), Toast.LENGTH_LONG).show();
            // Optional: Navigate to "My Bookings" or back to the list
            // Intent intent = new Intent(this, MainActivity.class);
            // intent.putExtra("NAVIGATE_TO", R.id.navigation_my_bookings); // Custom flag for MainActivity
            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            // startActivity(intent);
            finish(); // Close detail view after booking
        } else {
            Toast.makeText(this, "Booking failed. Please try again.", Toast.LENGTH_SHORT).show();
            // More specific error handling could be added based on why DataRepository might fail
        }
    }


    // Handle Toolbar back button press (if Toolbar is used)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close this activity, return to previous one
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}