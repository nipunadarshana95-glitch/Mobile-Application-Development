package com.example.techcareservices.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.techcareservices.R;
import com.example.techcareservices.activities.BookingActivity;
import com.example.techcareservices.models.Service;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ServiceAdapter extends FirebaseRecyclerAdapter<Service, ServiceAdapter.ServiceViewHolder> {

    private Context context;
    private RecyclerView recyclerView;

    public ServiceAdapter(@NonNull FirebaseRecyclerOptions<Service> options) {
        super(options);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDataChanged() {
        if (recyclerView != null && recyclerView.isComputingLayout()) {
            recyclerView.post(this::notifyDataSetChanged);
        } else {
            super.onDataChanged();
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull ServiceViewHolder holder, int position, @NonNull final Service model) {
        holder.textViewServiceName.setText(model.serviceName);
        holder.textViewServiceDescription.setText(model.description);
        holder.textViewDeviceType.setText(model.deviceType);

        // Load image directly from drawable resource
        if (model.imageResId != 0) {
            Glide.with(holder.imageViewService.getContext())
                    .load(model.imageResId)
                    .placeholder(R.drawable.app_background)
                    .error(R.drawable.app_background)
                    .into(holder.imageViewService);
        }

        holder.buttonBookService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BookingActivity.class);
                intent.putExtra("deviceType", model.deviceType);
                intent.putExtra("serviceName", model.serviceName);
                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.service_row, parent, false);

        return new ServiceViewHolder(view);
    }

    class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView textViewServiceName, textViewServiceDescription, textViewDeviceType;
        Button buttonBookService;
        ImageView imageViewService;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewServiceName = itemView.findViewById(R.id.textViewServiceName);
            textViewServiceDescription = itemView.findViewById(R.id.textViewServiceDescription);
            textViewDeviceType = itemView.findViewById(R.id.textViewDeviceType);
            buttonBookService = itemView.findViewById(R.id.buttonBookService);
            imageViewService = itemView.findViewById(R.id.imageViewService);
        }
    }
}