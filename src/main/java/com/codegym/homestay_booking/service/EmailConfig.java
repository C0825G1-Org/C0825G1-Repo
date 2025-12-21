package com.codegym.homestay_booking.service;

/**
 * Email configuration for Gmail SMTP
 * IMPORTANT: Use App Password, not regular Gmail password
 * Create at: https://myaccount.google.com/apppasswords
 */
public class EmailConfig {
    
    // Gmail SMTP settings
    public static final String SMTP_HOST = "smtp.gmail.com";
    public static final int SMTP_PORT = 587;
    public static final boolean SMTP_AUTH = true;
    public static final boolean SMTP_STARTTLS = true;
    
    // Sender email credentials
    // TODO: Replace with your Gmail and App Password
    public static final String SENDER_EMAIL = "triphung15@gmail.com";
    public static final String SENDER_PASSWORD = "libl ugob azgl gysq";
    
    // Homestay info for invoice
    public static final String HOMESTAY_NAME = "Cozy Homestay";
    public static final String HOMESTAY_ADDRESS = "123 Beach Road, Da Nang, Vietnam";
    public static final String HOMESTAY_PHONE = "+84 123 456 789";
    public static final String HOMESTAY_EMAIL = "contact@cozyhomestay.com";
}
