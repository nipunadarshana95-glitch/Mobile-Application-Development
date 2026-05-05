package com.example.techcareservices.models;

public class Service {
    public String serviceName;
    public String description;
    public String deviceType;
    public int imageResId; // Changed from String to int

    public Service() {
        // Default constructor
    }

    public Service(String serviceName, String description, String deviceType, int imageResId) {
        this.serviceName = serviceName;
        this.description = description;
        this.deviceType = deviceType;
        this.imageResId = imageResId;
    }
}
