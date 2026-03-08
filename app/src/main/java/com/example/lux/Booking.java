package com.example.lux;

import java.util.Date;

public class Booking {
    private String bookingId;
    private String userId; // Link to the user who booked
    private String itemId; // Room ID or Service ID
    private String itemName; // Room Name or Service Name
    private String itemType; // "Room" or "Service"
    private Date startDate;
    private Date endDate; // Relevant for rooms, maybe null for some services
    private String status; // "Confirmed", "Cancelled"

    // Constructor, Getters, Setters
    public Booking(String bookingId, String userId, String itemId, String itemName, String itemType, Date startDate, Date endDate, String status) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemType = itemType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }
    public String getBookingId() { return bookingId; }
    public String getUserId() { return userId; }
    public String getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public String getItemType() { return itemType; }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}