package com.example.techcareservices.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techcareservices.R;
import com.example.techcareservices.adapters.BookingAdapter;
import com.example.techcareservices.models.Booking;
import com.example.techcareservices.models.User;
import com.example.techcareservices.utils.LinearLayoutManagerWrapper;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText editTextFullName, editTextEmail, editTextPhone;
    private Button buttonUpdateProfile;
    private ImageView profileImageView;
    private RecyclerView recyclerViewServiceHistory;
    private BookingAdapter bookingAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("User Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonUpdateProfile = findViewById(R.id.buttonUpdateProfile);
        profileImageView = findViewById(R.id.profileImageView);
        recyclerViewServiceHistory = findViewById(R.id.recyclerViewServiceHistory);

        recyclerViewServiceHistory.setLayoutManager(new LinearLayoutManagerWrapper(this));

        buttonUpdateProfile.setOnClickListener(v -> updateProfile());

        loadUserProfile();
        setupServiceHistory();
    }

    private void loadUserProfile() {
        if (currentUser.getEmail() != null) {
            editTextEmail.setText(currentUser.getEmail());
        }

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    editTextFullName.setText(user.fullName);
                    editTextPhone.setText(user.phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to load user profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupServiceHistory() {
        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference().child("bookings");
        Query bookingsQuery = bookingsRef.orderByChild("userId").equalTo(currentUser.getUid());

        FirebaseRecyclerOptions<Booking> options = new FirebaseRecyclerOptions.Builder<Booking>()
                .setQuery(bookingsQuery, Booking.class)
                .build();

        bookingAdapter = new BookingAdapter(options);
        recyclerViewServiceHistory.setAdapter(bookingAdapter);
    }

    private void updateProfile() {
        String fullName = editTextFullName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            editTextFullName.setError("Full name is required");
            editTextFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            editTextPhone.setError("Phone number is required");
            editTextPhone.requestFocus();
            return;
        }

        userRef.child("fullName").setValue(fullName);
        userRef.child("phone").setValue(phone);

        Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (bookingAdapter != null) {
            bookingAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bookingAdapter != null) {
            bookingAdapter.stopListening();
        }
    }
}