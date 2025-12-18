package com.codegym.homestay_booking.dto;

public class MonthlyBooking {
    private int month;
    private int count;
    
    public MonthlyBooking() {}
    
    public MonthlyBooking(int month, int count) {
        this.month = month;
        this.count = count;
    }
    
    public int getMonth() {
        return month;
    }
    
    public void setMonth(int month) {
        this.month = month;
    }
    
    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
}
