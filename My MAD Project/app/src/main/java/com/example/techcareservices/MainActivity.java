package com.example.techcareservices;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techcareservices.activities.ContactActivity;
import com.example.techcareservices.activities.FaqActivity;
import com.example.techcareservices.activities.LoginActivity;
import com.example.techcareservices.activities.MyBookingsActivity;
import com.example.techcareservices.activities.ProfileActivity;
import com.example.techcareservices.activities.TipsActivity;
import com.example.techcareservices.adapters.ServiceAdapter;
import com.example.techcareservices.models.Service;
import com.example.techcareservices.utils.LinearLayoutManagerWrapper;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RecyclerView recyclerViewServices;
    private ServiceAdapter serviceAdapter;
    private DatabaseReference servicesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerViewServices = findViewById(R.id.recyclerViewServices);
        recyclerViewServices.setLayoutManager(new LinearLayoutManagerWrapper(this));

        servicesRef = FirebaseDatabase.getInstance().getReference().child("services");

        servicesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    addSampleDataAndSetupAdapter();
                } else {
                    setupAdapter();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addSampleDataAndSetupAdapter() {
        Map<String, Object> sampleServices = new HashMap<>();
        sampleServices.put("service1", new Service("Mobile Screen Replacement", "Cracked or broken screen replacement for all major smartphone brands.", "Smartphone", R.drawable.mobile_repair));
        sampleServices.put("service2", new Service("Laptop Not Charging", "Diagnosing and fixing charging issues, including battery and port replacement.", "Laptop", R.drawable.laptop_repair));
        sampleServices.put("service3", new Service("AC Gas Refill", "Top-up of refrigerant gas to restore cooling performance.", "Air Conditioner", R.drawable.ac_repair));
        sampleServices.put("service4", new Service("TV No Display", "Troubleshooting and repair of televisions with no picture.", "Television", R.drawable.tv_repair));
        sampleServices.put("service5", new Service("Refrigerator Not Cooling", "Repair of cooling system, compressor, and thermostat issues.", "Refrigerator", R.drawable.fridge_repair));
        sampleServices.put("service6", new Service("Washing Machine Not Spinning", "Fixing problems with the drum, motor, or belt.", "Washing Machine", R.drawable.washing_machine_repair));

        servicesRef.updateChildren(sampleServices).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    setupAdapter();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to add sample data.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupAdapter() {
        if (serviceAdapter != null) {
            return;
        }

        FirebaseRecyclerOptions<Service> options = new FirebaseRecyclerOptions.Builder<Service>()
                .setQuery(servicesRef, Service.class)
                .build();
        serviceAdapter = new ServiceAdapter(options);
        recyclerViewServices.setAdapter(serviceAdapter);
        serviceAdapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (serviceAdapter != null) {
            serviceAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (serviceAdapter != null) {
            serviceAdapter.stopListening();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_logout) {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return true;
        } else if (itemId == R.id.action_profile) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            return true;
        } else if (itemId == R.id.action_my_bookings) {
            startActivity(new Intent(MainActivity.this, MyBookingsActivity.class));
            return true;
        } else if (itemId == R.id.action_faq) {
            startActivity(new Intent(MainActivity.this, FaqActivity.class));
            return true;
        } else if (itemId == R.id.action_tips) {
            startActivity(new Intent(MainActivity.this, TipsActivity.class));
            return true;
        } else if (itemId == R.id.action_contact) {
            startActivity(new Intent(MainActivity.this, ContactActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}