package com.example.lux;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Import Glide

import java.util.List;
import java.util.Locale;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private List<Room> roomList;
    private Context context;

    public RoomAdapter(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.roomName.setText(room.getName());
        holder.roomPrice.setText(String.format(Locale.getDefault(), "$%.2f / night", room.getPricePerNight()));

        // ... (availability text setting remains the same) ...
        if (room.isAvailable()) {
            holder.roomAvailability.setText("Available");
            holder.roomAvailability.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark)); // Example color
        } else {
            holder.roomAvailability.setText("Not Available");
            holder.roomAvailability.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark)); // Example color
        }

        // --- Load image using Glide ---
        Glide.with(context)
                .load(room.getImageUrl()) // Load the URL from the Room object
                .placeholder(R.drawable.placeholder_image) // Show placeholder while loading
                .error(R.drawable.placeholder_image) // Show placeholder if URL fails to load
                .centerCrop() // Scale type
                .into(holder.roomImage); // Target ImageView
        // ----------------------------

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RoomDetailActivity.class);
            intent.putExtra("ROOM_ID", room.getRoomId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public void updateData(List<Room> newRoomList) {
        this.roomList = newRoomList;
        notifyDataSetChanged();
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageView roomImage;
        TextView roomName, roomPrice, roomAvailability;

        RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomImage = itemView.findViewById(R.id.imageViewRoom);
            roomName = itemView.findViewById(R.id.textViewRoomName);
            roomPrice = itemView.findViewById(R.id.textViewRoomPrice);
            roomAvailability = itemView.findViewById(R.id.textViewRoomAvailability);
        }
    }
}