package com.example.lux;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
// import com.bumptech.glide.Glide;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private static final String TAG = "ServiceAdapter";

    private List<Service> serviceList;
    private Context context;

    public ServiceAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.serviceName.setText(service.getName());
        holder.serviceDescriptionShort.setText(service.getDescription());

        // ... (price handling remains the same) ...
        if (service.getPrice() > 0) {
            holder.servicePrice.setText(String.format(Locale.getDefault(), "$%.2f", service.getPrice()));
            holder.servicePrice.setVisibility(View.VISIBLE);
        } else {
            holder.servicePrice.setVisibility(View.INVISIBLE);
        }

        // --- Load image using Glide ---
        Glide.with(context)
                .load(service.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .centerCrop()
                .into(holder.serviceImage);
        // ----------------------------

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ServiceBookingActivity.class);
            intent.putExtra("SERVICE_ID", service.getServiceId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        int count = (serviceList != null ? serviceList.size() : 0);
        Log.d(TAG, "getItemCount() returning: " + count); // Log count returned
        return count;
    }

    // Method to update data if needed
    public void updateData(List<Service> newServiceList) {
        this.serviceList = newServiceList;
        notifyDataSetChanged();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView serviceImage;
        TextView serviceName, servicePrice, serviceDescriptionShort;

        ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceImage = itemView.findViewById(R.id.imageViewService);
            serviceName = itemView.findViewById(R.id.textViewServiceName);
            servicePrice = itemView.findViewById(R.id.textViewServicePrice);
            serviceDescriptionShort = itemView.findViewById(R.id.textViewServiceDescriptionShort);
        }
    }
}