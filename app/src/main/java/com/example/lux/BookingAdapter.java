package com.example.lux;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;
    private Context context;
    private DataRepository dataRepository;
    private PrefsManager prefsManager; // Needed to get current user ID for cancellation


    // Listener interface for cancellation callback
    public interface OnBookingCancelledListener {
        void onBookingCancelled(int position);
    }
    private OnBookingCancelledListener cancellationListener;


    public BookingAdapter(Context context, List<Booking> bookingList, OnBookingCancelledListener listener) {
        this.context = context;
        this.bookingList = bookingList;
        // --- FIX THIS LINE ---
        // this.dataRepository = DataRepository.getInstance(); // OLD WAY (Causes error)
        this.dataRepository = DataRepository.getInstance(context); // NEW WAY - Pass the context received by the constructor
        // --------------------
        this.prefsManager = new PrefsManager(context); // This line was likely already correct
        this.cancellationListener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.itemName.setText(String.format("%s (%s)", booking.getItemName(), booking.getItemType()));
        holder.bookingStatus.setText(String.format("Status: %s", booking.getStatus()));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateStr = "Date: " + sdf.format(booking.getStartDate());
        if (booking.getEndDate() != null && "Room".equals(booking.getItemType())) {
            dateStr += " to " + sdf.format(booking.getEndDate());
        }
        holder.bookingDates.setText(dateStr);

        holder.cancelButton.setOnClickListener(v -> {
            String currentUserId = prefsManager.getUserId();
            if (currentUserId == null) {
                Toast.makeText(context, "Error: User not logged in.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Confirm cancellation (optional but recommended)
            // AlertDialog.Builder builder = new AlertDialog.Builder(context);
            // builder.setTitle("Confirm Cancellation");
            // builder.setMessage("Are you sure you want to cancel this booking?");
            // builder.setPositiveButton("Yes", (dialog, which) -> {
            //      performCancellation(booking.getBookingId(), currentUserId, holder.getAdapterPosition());
            // });
            // builder.setNegativeButton("No", null);
            // builder.show();

            // Direct cancellation (without confirmation dialog for simplicity)
            performCancellation(booking.getBookingId(), currentUserId, holder.getAdapterPosition());

        });
    }

    private void performCancellation(String bookingId, String userId, int adapterPosition) {
        boolean cancelled = dataRepository.cancelBooking(bookingId, userId);
        if (cancelled) {
            Toast.makeText(context, "Booking Cancelled", Toast.LENGTH_SHORT).show();
            // Notify the fragment to remove the item from its list and update UI
            if (cancellationListener != null && adapterPosition != RecyclerView.NO_POSITION) {
                cancellationListener.onBookingCancelled(adapterPosition);
            }

        } else {
            Toast.makeText(context, "Failed to cancel booking", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    // Method to update data
    public void updateData(List<Booking> newBookingList) {
        this.bookingList = newBookingList;
        notifyDataSetChanged();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, bookingDates, bookingStatus;
        Button cancelButton;

        BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.textViewBookingItemName);
            bookingDates = itemView.findViewById(R.id.textViewBookingDates);
            bookingStatus = itemView.findViewById(R.id.textViewBookingStatus);
            cancelButton = itemView.findViewById(R.id.buttonCancelBooking);
        }
    }
}