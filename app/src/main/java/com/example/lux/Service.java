package com.example.lux;

public class Service {
    private String serviceId;
    private String name; // "Spa Treatment", "Fine Dining Reservation"
    private String description;
    private double price; // Or could be variable
    private String imageUrl;

    // Constructor, Getters
    public Service(String serviceId, String name, String description, double price, String imageUrl) {
        this.serviceId = serviceId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }
    public String getServiceId() { return serviceId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
}