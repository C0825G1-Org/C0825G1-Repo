package com.codegym.homestay_booking.entity;

public class Room {
    private int roomId;
    private RoomType roomType;
    private int sleepSlot;
    private float roomPrice;
    private RoomStatus status;
    private String imageUrl;
    private String description;

    public enum RoomType {
        Family,
        Business,
        Honey_Moon
    }

    public enum RoomStatus {
        AVAILABLE,
        UNAVAILABLE
    }

    // Constructors
    public Room() {
    }

    public Room(int roomId, RoomType roomType, int sleepSlot, float roomPrice, 
                RoomStatus status, String imageUrl, String description) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.sleepSlot = sleepSlot;
        this.roomPrice = roomPrice;
        this.status = status;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    // Getters and Setters
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public int getSleepSlot() {
        return sleepSlot;
    }

    public void setSleepSlot(int sleepSlot) {
        this.sleepSlot = sleepSlot;
    }

    public float getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(float roomPrice) {
        this.roomPrice = roomPrice;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormattedPrice() {
        return String.format("%,.0f VND", roomPrice);
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomId=" + roomId +
                ", roomType=" + roomType +
                ", sleepSlot=" + sleepSlot +
                ", roomPrice=" + roomPrice +
                ", status=" + status +
                '}';
    }
}
