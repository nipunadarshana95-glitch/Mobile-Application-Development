package com.example.techcareservices.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.techcareservices.R;
import com.example.techcareservices.models.Booking;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class BookingActivity extends AppCompatActivity {

    private TextInputEditText editTextDeviceType, editTextServiceType, editTextIssueDescription;
    private RadioGroup radioGroupServiceMethod;
    private Button buttonSelectDate, buttonSubmitBooking;
    private TextView textViewSelectedDate, textViewDropOffSlots;

    private FirebaseAuth mAuth;
    private DatabaseReference bookingsRef;

    private String pickupDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        mAuth = FirebaseAuth.getInstance();
        bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");

        editTextDeviceType = findViewById(R.id.editTextDeviceType);
        editTextServiceType = findViewById(R.id.editTextServiceType);
        editTextIssueDescription = findViewById(R.id.editTextIssueDescription);
        radioGroupServiceMethod = findViewById(R.id.radioGroupServiceMethod);
        buttonSelectDate = findViewById(R.id.buttonSelectDate);
        buttonSubmitBooking = findViewById(R.id.buttonSubmitBooking);
        textViewSelectedDate = findViewById(R.id.textViewSelectedDate);
        textViewDropOffSlots = findViewById(R.id.textViewDropOffSlots);

        Intent intent = getIntent();
        if (intent != null) {
            String deviceType = intent.getStringExtra("deviceType");
            String serviceType = intent.getStringExtra("serviceName");
            editTextDeviceType.setText(deviceType);
            editTextServiceType.setText(serviceType);
        }

        radioGroupServiceMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonDropOff) {
                textViewDropOffSlots.setVisibility(View.VISIBLE);
                buttonSelectDate.setVisibility(View.GONE);
                textViewSelectedDate.setVisibility(View.GONE);
            } else {
                textViewDropOffSlots.setVisibility(View.GONE);
                buttonSelectDate.setVisibility(View.VISIBLE);
                textViewSelectedDate.setVisibility(View.VISIBLE);
            }
        });

        buttonSelectDate.setOnClickListener(v -> showDateTimePicker());
        buttonSubmitBooking.setOnClickListener(v -> submitBooking());
    }

    private void showDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        pickupDate = calendar.getTime().toString();
                        textViewSelectedDate.setText(pickupDate);
                    }
                };

                new TimePickerDialog(BookingActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        };

        new DatePickerDialog(BookingActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void submitBooking() {
        String deviceType = editTextDeviceType.getText().toString().trim();
        String serviceType = editTextServiceType.getText().toString().trim();
        String issueDescription = editTextIssueDescription.getText().toString().trim();

        if (TextUtils.isEmpty(deviceType)) {
            editTextDeviceType.setError("Device type is required");
            editTextDeviceType.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(serviceType)) {
            editTextServiceType.setError("Service type is required");
            editTextServiceType.requestFocus();
            return;
        }

        int selectedRadioButtonId = radioGroupServiceMethod.getCheckedRadioButtonId();
        if (selectedRadioButtonId == -1) {
            Toast.makeText(this, "Please select a service method", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        String serviceMethod = selectedRadioButton.getText().toString();
        
        saveBooking(deviceType, serviceType, issueDescription, serviceMethod, pickupDate, "");
    }

    private void saveBooking(String deviceType, String serviceType, String issueDescription, String serviceMethod, String pickupDate, String imageUrl) {
        String userId = mAuth.getCurrentUser().getUid();
        String bookingId = bookingsRef.push().getKey();
        Booking booking = new Booking(userId, deviceType, serviceType, issueDescription, serviceMethod, pickupDate, "Requested", imageUrl);

        if (bookingId != null) {
            bookingsRef.child(bookingId).setValue(booking).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(BookingActivity.this, "Booking submitted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(BookingActivity.this, "Failed to submit booking", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}