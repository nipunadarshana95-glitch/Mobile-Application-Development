package com.example.techcareservices.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techcareservices.R;
import com.example.techcareservices.adapters.BookingAdapter;
import com.example.techcareservices.models.Booking;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MyBookingsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBookings;
    private BookingAdapter bookingAdapter;
    private DatabaseReference bookingsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();

        recyclerViewBookings = findViewById(R.id.recyclerViewBookings);
        recyclerViewBookings.setLayoutManager(new LinearLayoutManager(this));

        bookingsRef = FirebaseDatabase.getInstance().getReference().child("bookings");
        Query bookingsQuery = bookingsRef.orderByChild("userId").equalTo(currentUserId);

        FirebaseRecyclerOptions<Booking> options = new FirebaseRecyclerOptions.Builder<Booking>()
                .setQuery(bookingsQuery, Booking.class)
                .build();

        bookingAdapter = new BookingAdapter(options);
        recyclerViewBookings.setAdapter(bookingAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bookingAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        bookingAdapter.stopListening();
    }
}