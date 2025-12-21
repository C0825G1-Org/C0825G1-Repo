package com.codegym.homestay_booking.service;

import com.codegym.homestay_booking.entity.Booking;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Properties;

/**
 * Service for sending emails via Gmail SMTP
 */
public class EmailService {
    
    private static EmailService instance;
    
    private EmailService() {}
    
    public static synchronized EmailService getInstance() {
        if (instance == null) {
            instance = new EmailService();
        }
        return instance;
    }
    
    /**
     * Send invoice email with PDF attachment
     */
    public boolean sendInvoiceEmail(String toEmail, String guestName, Booking booking, File pdfFile) {
        try {
            // Setup mail properties
            Properties props = new Properties();
            props.put("mail.smtp.host", EmailConfig.SMTP_HOST);
            props.put("mail.smtp.port", String.valueOf(EmailConfig.SMTP_PORT));
            props.put("mail.smtp.auth", String.valueOf(EmailConfig.SMTP_AUTH));
            props.put("mail.smtp.starttls.enable", String.valueOf(EmailConfig.SMTP_STARTTLS));
            
            // Create session with authentication
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        EmailConfig.SENDER_EMAIL, 
                        EmailConfig.SENDER_PASSWORD
                    );
                }
            });
            
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EmailConfig.SENDER_EMAIL, EmailConfig.HOMESTAY_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("[XÁC NHẬN ĐẶT PHÒNG] Hóa đơn booking #" + booking.getBookingId());
            
            // Create multipart message (text + attachment)
            Multipart multipart = new MimeMultipart();
            
            // Part 1: Email body
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(buildEmailBody(guestName, booking), "text/html; charset=UTF-8");
            multipart.addBodyPart(textPart);
            
            // Part 2: PDF attachment
            if (pdfFile != null && pdfFile.exists()) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                DataSource source = new FileDataSource(pdfFile);
                attachmentPart.setDataHandler(new DataHandler(source));
                attachmentPart.setFileName("Invoice_Booking_" + booking.getBookingId() + ".pdf");
                multipart.addBodyPart(attachmentPart);
            }
            
            message.setContent(multipart);
            
            // Send email
            Transport.send(message);
            
            System.out.println("Invoice email sent successfully to: " + toEmail);
            return true;
            
        } catch (Exception e) {
            System.err.println("Failed to send invoice email to: " + toEmail);
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Build HTML email body
     */
    private String buildEmailBody(String guestName, Booking booking) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: Arial, sans-serif; line-height: 1.6;'>");
        sb.append("<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>");
        
        // Header
        sb.append("<div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); ");
        sb.append("color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0;'>");
        sb.append("<h1 style='margin: 0;'>").append(EmailConfig.HOMESTAY_NAME).append("</h1>");
        sb.append("<p style='margin: 10px 0 0 0;'>Xác nhận đặt phòng thành công</p>");
        sb.append("</div>");
        
        // Body
        sb.append("<div style='background: #f9f9f9; padding: 30px; border: 1px solid #ddd;'>");
        
        sb.append("<p>Xin chào <strong>").append(guestName).append("</strong>,</p>");
        sb.append("<p>Cảm ơn bạn đã đặt phòng tại <strong>").append(EmailConfig.HOMESTAY_NAME).append("</strong>.</p>");
        sb.append("<p>Đơn đặt phòng của bạn đã được <span style='color: #28a745; font-weight: bold;'>XÁC NHẬN</span>.</p>");
        
        // Booking details box
        sb.append("<div style='background: white; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #667eea;'>");
        sb.append("<h3 style='margin-top: 0; color: #333;'>📋 Thông tin booking</h3>");
        sb.append("<table style='width: 100%;'>");
        sb.append("<tr><td style='padding: 8px 0; color: #666;'>Mã booking:</td><td style='font-weight: bold;'>#").append(booking.getBookingId()).append("</td></tr>");
        sb.append("<tr><td style='padding: 8px 0; color: #666;'>Check-in:</td><td style='font-weight: bold;'>").append(booking.getCheckInDate().format(dateFormatter)).append("</td></tr>");
        sb.append("<tr><td style='padding: 8px 0; color: #666;'>Check-out:</td><td style='font-weight: bold;'>").append(booking.getCheckOutDate().format(dateFormatter)).append("</td></tr>");
        sb.append("<tr><td style='padding: 8px 0; color: #666;'>Tổng thanh toán:</td><td style='font-weight: bold; color: #e74c3c; font-size: 18px;'>").append(currencyFormat.format(booking.getTotalPrice())).append(" VND</td></tr>");
        sb.append("</table>");
        sb.append("</div>");
        
        sb.append("<p>📎 Hóa đơn chi tiết được đính kèm trong file PDF.</p>");
        sb.append("<p>Chúc bạn có một kỳ nghỉ tuyệt vời!</p>");
        
        sb.append("</div>");
        
        // Footer
        sb.append("<div style='background: #333; color: #ccc; padding: 20px; text-align: center; border-radius: 0 0 10px 10px;'>");
        sb.append("<p style='margin: 0;'>").append(EmailConfig.HOMESTAY_NAME).append("</p>");
        sb.append("<p style='margin: 5px 0; font-size: 12px;'>").append(EmailConfig.HOMESTAY_ADDRESS).append("</p>");
        sb.append("<p style='margin: 5px 0; font-size: 12px;'>📞 ").append(EmailConfig.HOMESTAY_PHONE).append(" | ✉️ ").append(EmailConfig.HOMESTAY_EMAIL).append("</p>");
        sb.append("</div>");
        
        sb.append("</div>");
        sb.append("</body></html>");
        
        return sb.toString();
    }
}
