package com.codegym.homestay_booking.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Payment {
    
    public enum PaymentStatus {
        PENDING,
        SUCCESS,
        FAILED,
        EXPIRED
    }
    
    private int paymentId;
    private String transactionCode;
    private String vnpTxnRef;
    private float amount;
    private PaymentStatus status;
    
    // Booking info
    private String guestName;
    private String guestEmail;
    private int roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    
    // After payment
    private Integer bookingId;
    private String vnpResponseCode;
    private String vnpTransactionNo;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    
    // Constructors
    public Payment() {
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    
    public String getTransactionCode() { return transactionCode; }
    public void setTransactionCode(String transactionCode) { this.transactionCode = transactionCode; }
    
    public String getVnpTxnRef() { return vnpTxnRef; }
    public void setVnpTxnRef(String vnpTxnRef) { this.vnpTxnRef = vnpTxnRef; }
    
    public float getAmount() { return amount; }
    public void setAmount(float amount) { this.amount = amount; }
    
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    
    public String getGuestEmail() { return guestEmail; }
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }
    
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    
    public Integer getBookingId() { return bookingId; }
    public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }
    
    public String getVnpResponseCode() { return vnpResponseCode; }
    public void setVnpResponseCode(String vnpResponseCode) { this.vnpResponseCode = vnpResponseCode; }
    
    public String getVnpTransactionNo() { return vnpTransactionNo; }
    public void setVnpTransactionNo(String vnpTransactionNo) { this.vnpTransactionNo = vnpTransactionNo; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    
    public String getFormattedAmount() {
        return String.format("%,.0f VND", amount);
    }
    
    public boolean isPending() {
        return status == PaymentStatus.PENDING;
    }
    
    public boolean isSuccess() {
        return status == PaymentStatus.SUCCESS;
    }
}
