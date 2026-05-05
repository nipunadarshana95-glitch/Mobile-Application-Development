package com.example.techcareservices.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techcareservices.R;
import com.example.techcareservices.models.Booking;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class BookingAdapter extends FirebaseRecyclerAdapter<Booking, BookingAdapter.BookingViewHolder> {

    private RecyclerView recyclerView;

    public BookingAdapter(@NonNull FirebaseRecyclerOptions<Booking> options) {
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
    protected void onBindViewHolder(@NonNull BookingViewHolder holder, int position, @NonNull Booking model) {
        holder.textViewServiceType.setText(model.serviceType);
        holder.textViewDeviceType.setText(model.deviceType);
        holder.textViewStatus.setText("Status: " + model.status);
        holder.textViewTechnician.setText("Technician: " + model.technicianName);
        holder.textViewPickupDate.setText("Pickup Date: " + model.pickupDate);
        holder.textViewCompletionTime.setText("Est. Completion: " + model.estimatedCompletionTime);

        // Show/Hide cancel button based on status
        if ("Requested".equals(model.status)) {
            holder.buttonCancelBooking.setVisibility(View.VISIBLE);
            holder.buttonCancelBooking.setOnClickListener(v -> {
                // Get the key of the item to be removed
                String bookingKey = getRef(position).getKey();
                if (bookingKey != null) {
                    FirebaseDatabase.getInstance().getReference("bookings").child(bookingKey).removeValue()
                            .addOnSuccessListener(aVoid -> Toast.makeText(holder.itemView.getContext(), "Booking Canceled", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(holder.itemView.getContext(), "Failed to cancel booking", Toast.LENGTH_SHORT).show());
                }
            });
        } else {
            holder.buttonCancelBooking.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_row, parent, false);

        return new BookingViewHolder(view);
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView textViewServiceType, textViewDeviceType, textViewStatus, textViewTechnician, textViewPickupDate, textViewCompletionTime;
        Button buttonCancelBooking;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewServiceType = itemView.findViewById(R.id.textViewServiceType);
            textViewDeviceType = itemView.findViewById(R.id.textViewDeviceType);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewTechnician = itemView.findViewById(R.id.textViewTechnician);
            textViewPickupDate = itemView.findViewById(R.id.textViewPickupDate);
            textViewCompletionTime = itemView.findViewById(R.id.textViewCompletionTime);
            buttonCancelBooking = itemView.findViewById(R.id.buttonCancelBooking);
        }
    }
}