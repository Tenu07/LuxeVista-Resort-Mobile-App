package com.example.lux;

import android.app.DatePickerDialog;
// import android.app.TimePickerDialog; // If time selection is needed
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
// import androidx.appcompat.widget.Toolbar; // Optional Toolbar


// import com.bumptech.glide.Glide;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ServiceBookingActivity extends AppCompatActivity {

    private ImageView imageViewServiceDetail;
    private TextView textViewServiceNameDetail, textViewServicePriceDetail, textViewServiceDescriptionDetail;
    private TextView textViewServiceDate; // Add textViewServiceTime if needed
    private Button buttonBookService;

    private DataRepository dataRepository;
    private PrefsManager prefsManager;
    private Service currentService;
    private String serviceId;

    private Calendar selectedDateTime = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    // private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault()); // If using time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_booking);

        // Optional Toolbar setup
        // Toolbar toolbar = findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setTitle("Service Details");


        dataRepository = DataRepository.getInstance(getApplicationContext());
        prefsManager = new PrefsManager(this);

        imageViewServiceDetail = findViewById(R.id.imageViewServiceDetail);
        textViewServiceNameDetail = findViewById(R.id.textViewServiceNameDetail);
        textViewServicePriceDetail = findViewById(R.id.textViewServicePriceDetail);
        textViewServiceDescriptionDetail = findViewById(R.id.textViewServiceDescriptionDetail);
        textViewServiceDate = findViewById(R.id.textViewServiceDate);
        // textViewServiceTime = findViewById(R.id.textViewServiceTime); // If using time
        buttonBookService = findViewById(R.id.buttonBookService);

        serviceId = getIntent().getStringExtra("SERVICE_ID");
        if (serviceId == null) {
            Toast.makeText(this, "Error: Service details not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadServiceDetails();
        setupDateTimePicker();

        buttonBookService.setOnClickListener(v -> attemptReservation());
    }

    private void loadServiceDetails() {
        currentService = dataRepository.getServiceById(serviceId);
        if (currentService != null) {
            textViewServiceNameDetail.setText(currentService.getName());
            // ... (price handling remains the same)
            if (currentService.getPrice() > 0) {
                textViewServicePriceDetail.setText(String.format(Locale.getDefault(), "$%.2f", currentService.getPrice()));
                textViewServicePriceDetail.setVisibility(TextView.VISIBLE);
            } else {
                textViewServicePriceDetail.setVisibility(TextView.INVISIBLE);
            }
            textViewServiceDescriptionDetail.setText(currentService.getDescription());

            // --- Load image using Glide ---
            Glide.with(this)
                    .load(currentService.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .centerCrop()
                    .into(imageViewServiceDetail);
            // ----------------------------

        } else {
            Toast.makeText(this, "Error loading service details.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupDateTimePicker() {
        long todayMillis = System.currentTimeMillis() - 1000;

        DatePickerDialog.OnDateSetListener dateListener = (view, year, month, dayOfMonth) -> {
            selectedDateTime.set(Calendar.YEAR, year);
            selectedDateTime.set(Calendar.MONTH, month);
            selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
            // Optional: Show time picker immediately after date is set
            // showTimePicker();
        };

        textViewServiceDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(ServiceBookingActivity.this, dateListener,
                    selectedDateTime.get(Calendar.YEAR), selectedDateTime.get(Calendar.MONTH),
                    selectedDateTime.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMinDate(todayMillis);
            dialog.show();
        });

        // --- Optional Time Picker ---
        /*
         TimePickerDialog.OnTimeSetListener timeListener = (view, hourOfDay, minute) -> {
            selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedDateTime.set(Calendar.MINUTE, minute);
            updateTimeLabel();
         };

         textViewServiceTime.setOnClickListener(v -> {
            new TimePickerDialog(ServiceBookingActivity.this, timeListener,
                    selectedDateTime.get(Calendar.HOUR_OF_DAY), selectedDateTime.get(Calendar.MINUTE),
                    true).show(); // true for 24 hour view
         });
         */
    }

    private void updateDateLabel() {
        textViewServiceDate.setText(dateFormat.format(selectedDateTime.getTime()));
    }

    /* // If using time
    private void updateTimeLabel() {
        textViewServiceTime.setText(timeFormat.format(selectedDateTime.getTime()));
    }
    */


    private void attemptReservation() {
        String userId = prefsManager.getUserId();
        if (userId == null) {
            Toast.makeText(this, "Please log in to reserve a service.", Toast.LENGTH_LONG).show();
            return;
        }

        if (currentService == null) {
            Toast.makeText(this, "Cannot reserve, service details unavailable.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate selected date (and time if applicable)
        String dateStr = textViewServiceDate.getText().toString();
        if (dateStr.equals(getString(R.string.select_date_prompt))) {
            Toast.makeText(this, "Please select a date for the service.", Toast.LENGTH_SHORT).show();
            return;
        }
         /* // If using time
         String timeStr = textViewServiceTime.getText().toString();
         if (timeStr.equals(getString(R.string.select_time_prompt))) { // Define this string
              Toast.makeText(this, "Please select a time for the service.", Toast.LENGTH_SHORT).show();
              return;
         }
         */

        Date serviceDate = selectedDateTime.getTime(); // Contains both date and time if time picker was used

        // --- Perform Reservation via DataRepository ---
        Booking newBooking = dataRepository.createBooking(
                userId,
                currentService.getServiceId(),
                currentService.getName(),
                "Service", // Item Type
                serviceDate,
                null // End date is often null for single-instance services
        );

        if (newBooking != null) {
            Toast.makeText(this, "Service reserved successfully! Booking ID: " + newBooking.getBookingId(), Toast.LENGTH_LONG).show();
            finish(); // Close detail view
        } else {
            Toast.makeText(this, "Reservation failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}