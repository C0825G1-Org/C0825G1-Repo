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

    public String getFormattedPrice() {
        return String.format("%,.0f VND", totalPrice);
    }
    public enum BookingStatus {
        PENDING,
        CONFIRMED,
        CANCELLED,
        COMPLETED,
        CANCELLED_REQUEST
    }
}
