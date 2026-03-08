package com.example.lux;

public class Room {
    private String roomId;
    private String name; // e.g., "Ocean View Suite"
    private String description;
    private double pricePerNight;
    private String imageUrl; // Placeholder for image loading
    private String type; // "Suite", "Deluxe"
    private boolean isAvailable; // Simplified availability

    // Constructor, Getters
    public Room(String roomId, String name, String description, double pricePerNight, String imageUrl, String type, boolean isAvailable) {
        this.roomId = roomId;
        this.name = name;
        this.description = description;
        this.pricePerNight = pricePerNight;
        this.imageUrl = imageUrl;
        this.type = type;
        this.isAvailable = isAvailable;
    }
    public String getRoomId() { return roomId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPricePerNight() { return pricePerNight; }
    public String getImageUrl() { return imageUrl; }
    public String getType() { return type; }
    public boolean isAvailable() { return isAvailable; }
}