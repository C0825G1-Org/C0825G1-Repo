package com.codegym.homestay_booking.dto;

public class RoomBookingCount {
    private int roomId;
    private int bookingCount;
    
    public RoomBookingCount() {}
    
    public RoomBookingCount(int roomId, int bookingCount) {
        this.roomId = roomId;
        this.bookingCount = bookingCount;
    }
    
    public int getRoomId() {
        return roomId;
    }
    
    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
    
    public int getBookingCount() {
        return bookingCount;
    }
    
    public void setBookingCount(int bookingCount) {
        this.bookingCount = bookingCount;
    }
}
