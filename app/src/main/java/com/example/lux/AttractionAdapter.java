package com.example.lux;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Import Glide
import com.example.lux.Attraction;

import java.util.List;

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.AttractionViewHolder> {

    private static final String TAG = "AttractionAdapter";

    private List<Attraction> attractionList;
    private Context context;

    public AttractionAdapter(Context context, List<Attraction> attractionList) {
        this.context = context;
        this.attractionList = attractionList;
    }

    @NonNull
    @Override
    public AttractionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_attraction, parent, false);
        return new AttractionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttractionViewHolder holder, int position) {
        Attraction attraction = attractionList.get(position);
        holder.attractionName.setText(attraction.getName());
        holder.attractionDescription.setText(attraction.getDescription());

        // --- Load image using Glide ---
        Glide.with(context)
                .load(attraction.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .centerCrop()
                .into(holder.attractionImage);
        // ----------------------------

        // Optional: Add click listener if needed
    }

    @Override
    public int getItemCount() {
        int count = (attractionList != null ? attractionList.size() : 0);
        Log.d(TAG, "getItemCount() returning: " + count); // Log count returned
        return count;
    }

    public void updateData(List<Attraction> newAttractionList) {
        this.attractionList = newAttractionList;
        notifyDataSetChanged();
    }

    static class AttractionViewHolder extends RecyclerView.ViewHolder {
        ImageView attractionImage;
        TextView attractionName, attractionDescription;

        AttractionViewHolder(@NonNull View itemView) {
            super(itemView);
            attractionImage = itemView.findViewById(R.id.imageViewAttraction);
            attractionName = itemView.findViewById(R.id.textViewAttractionName);
            attractionDescription = itemView.findViewById(R.id.textViewAttractionDescription);
        }
    }
}