package com.codegym.homestay_booking.entity;

import java.time.LocalDate;

public class Booking {
    private int bookingId;
    private String guestName;
    private String guestEmail;
    private int roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private float totalPrice;
    private BookingStatus status;

    public Booking() {
    }

    public Booking(int bookingId, String guestName, String guestEmail, int roomId, LocalDate checkInDate, LocalDate checkOutDate, float totalPrice, BookingStatus status) {
        this.bookingId = bookingId;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public Booking(String guestName, String guestEmail, int roomId, LocalDate checkInDate, LocalDate checkOutDate, float totalPrice, BookingStatus status) {
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public int getNumberOfNights() {
        if (checkInDate == null || checkOutDate == null) {
            return 0;
        }
        return (int) checkOutDate.toEpochDay() - (int) checkInDate.toEpochDay();
    }

    public boolean canBeApproved() {
        return status == BookingStatus.PENDING;
    }

    public boolean canBeCancelled() {
        return status == BookingStatus.PENDING || status == BookingStatus.CONFIRMED;
    }
    
    /**
     * Check if booking can be edited based on status and check-in date
     * Rules:
     * - COMPLETED/CANCELLED/CANCELLED_REQUEST: Cannot edit
     * - PENDING: Can edit
     * - CONFIRMED: Can edit only if check-in date is in the future (> today)
     */
    public boolean canBeEdited() {
        if (status == BookingStatus.COMPLETED || 
            status == BookingStatus.CANCELLED ||
            status == BookingStatus.CANCELLED_REQUEST) {
            return false;
        }

        if (status == BookingStatus.PENDING) {
            return true;
        }

        if (status == BookingStatus.CONFIRMED) {
            // Can edit if check-in is more than 1 day away
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            return checkInDate != null && checkInDate.isAfter(tomorrow);
        }
        
        return false;
    }
    
    /**
     * Check if check-in date can be edited
     * PENDING: Yes
     * CONFIRMED: Yes (if check-in is in the future)
     */
    public boolean canEditCheckIn() {
        if (!canBeEdited()) {
            return false;
        }
        // Both PENDING and CONFIRMED can edit check-in if booking is editable
        return true;
    }
    
    /**
     * Check if check-out date can be edited
     * PENDING: Yes
     * CONFIRMED: Yes (if editable at all)
     */
    public boolean canEditCheckOut() {
        return canBeEdited();
    }
    
    /**
     * Check if room can be changed
     * Rules:
     * - PENDING or CONFIRMED only
     * - Check-in date must be in future
     * - Cannot change if CANCELLED/COMPLETED/CANCELLED_REQUEST
     */
    public boolean canChangeRoom() {
        if (status == BookingStatus.CANCELLED || 
            status == BookingStatus.COMPLETED ||
            status == BookingStatus.CANCELLED_REQUEST) {
            return false;
        }
        
        if (status != BookingStatus.PENDING && status != BookingStatus.CONFIRMED) {
            return false;
        }
        
        // Check-in must be in future
        LocalDate today = LocalDate.now();
        return checkInDate != null && checkInDate.isAfter(today);
    }
    
    /**
     * Check if check-in date has passed (is today or in the past)
     */
    public boolean isCheckInPassed() {
        if (checkInDate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return !checkInDate.isAfter(today); // checkIn <= today
    }

    public String getFormattedPrice() {
        return String.format("%,.0f VND", totalPrice);
    }
    
    public String getFormattedTotalPrice() {
        return getFormattedPrice();
    }
    
    public enum BookingStatus {
        PENDING,
        CONFIRMED,
        CANCELLED,
        COMPLETED,
        CANCELLED_REQUEST
    }
}
