package com.example.techcareservices.models;

public class Booking {
    public String userId, deviceType, serviceType, issueDescription, serviceMethod, pickupDate, status, imageUrl;
    public String technicianName, estimatedCompletionTime; // New fields

    public Booking() {
        // Default constructor required for calls to DataSnapshot.getValue(Booking.class)
    }

    public Booking(String userId, String deviceType, String serviceType, String issueDescription, String serviceMethod, String pickupDate, String status, String imageUrl) {
        this.userId = userId;
        this.deviceType = deviceType;
        this.serviceType = serviceType;
        this.issueDescription = issueDescription;
        this.serviceMethod = serviceMethod;
        this.pickupDate = pickupDate;
        this.status = status;
        this.imageUrl = imageUrl;
        this.technicianName = "Not Assigned"; // Default value
        this.estimatedCompletionTime = "Pending"; // Default value
    }
}
