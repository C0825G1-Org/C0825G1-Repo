package com.codegym.homestay_booking.dto;

public class RevenueData {
    private String date;
    private double revenue;
    
    public RevenueData() {}
    
    public RevenueData(String date, double revenue) {
        this.date = date;
        this.revenue = revenue;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public double getRevenue() {
        return revenue;
    }
    
    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }
}
