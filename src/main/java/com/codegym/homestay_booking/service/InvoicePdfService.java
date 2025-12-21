package com.codegym.homestay_booking.service;

import com.codegym.homestay_booking.entity.Booking;
import com.codegym.homestay_booking.entity.Room;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * Service for generating PDF invoices
 */
public class InvoicePdfService {
    
    private static InvoicePdfService instance;
    
    private InvoicePdfService() {}
    
    public static synchronized InvoicePdfService getInstance() {
        if (instance == null) {
            instance = new InvoicePdfService();
        }
        return instance;
    }
    
    /**
     * Generate invoice PDF and return the file
     */
    public File generateInvoicePdf(Booking booking, Room room) throws IOException {
        // Create temp file
        File tempFile = File.createTempFile("invoice_" + booking.getBookingId() + "_", ".pdf");
        tempFile.deleteOnExit();
        
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        
        float pageWidth = page.getMediaBox().getWidth();
        float margin = 50;
        float yPosition = page.getMediaBox().getHeight() - margin;
        float lineHeight = 20;
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        
        // ===== HEADER =====
        contentStream.setNonStrokingColor(new Color(102, 126, 234)); // #667eea
        contentStream.addRect(0, yPosition - 30, pageWidth, 80);
        contentStream.fill();
        
        contentStream.setNonStrokingColor(Color.WHITE);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText(EmailConfig.HOMESTAY_NAME);
        contentStream.endText();
        
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, yPosition - 20);
        contentStream.showText(EmailConfig.HOMESTAY_ADDRESS);
        contentStream.endText();
        
        yPosition -= 100;
        
        // ===== INVOICE TITLE =====
        contentStream.setNonStrokingColor(Color.BLACK);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("HOA DON / INVOICE");
        contentStream.endText();
        
        yPosition -= 10;
        
        // Invoice number and date
        contentStream.setFont(PDType1Font.HELVETICA, 11);
        contentStream.beginText();
        contentStream.newLineAtOffset(pageWidth - 200, yPosition + 10);
        contentStream.showText("Invoice #: INVOICE-" + booking.getBookingId());
        contentStream.endText();
        
        contentStream.beginText();
        contentStream.newLineAtOffset(pageWidth - 200, yPosition - 5);
        contentStream.showText("Date: " + LocalDate.now().format(dateFormatter));
        contentStream.endText();
        
        yPosition -= 50;
        
        // ===== GUEST INFO =====
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("GUEST INFORMATION");
        contentStream.endText();
        
        yPosition -= lineHeight;
        
        contentStream.setFont(PDType1Font.HELVETICA, 11);
        drawLabelValue(contentStream, margin, yPosition, "Name:", booking.getGuestName());
        yPosition -= lineHeight;
        drawLabelValue(contentStream, margin, yPosition, "Email:", booking.getGuestEmail());
        
        yPosition -= 40;
        
        // ===== BOOKING DETAILS =====
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("BOOKING DETAILS");
        contentStream.endText();
        
        yPosition -= lineHeight;
        
        contentStream.setFont(PDType1Font.HELVETICA, 11);
        drawLabelValue(contentStream, margin, yPosition, "Booking ID:", "#" + booking.getBookingId());
        yPosition -= lineHeight;
        drawLabelValue(contentStream, margin, yPosition, "Room:", room != null ? room.getRoomType().toString() + " - Room #" + room.getRoomId() : "Room #" + booking.getRoomId());
        yPosition -= lineHeight;
        drawLabelValue(contentStream, margin, yPosition, "Check-in:", booking.getCheckInDate().format(dateFormatter));
        yPosition -= lineHeight;
        drawLabelValue(contentStream, margin, yPosition, "Check-out:", booking.getCheckOutDate().format(dateFormatter));
        yPosition -= lineHeight;
        
        long nights = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        drawLabelValue(contentStream, margin, yPosition, "Number of nights:", String.valueOf(nights));
        yPosition -= lineHeight;
        drawLabelValue(contentStream, margin, yPosition, "Status:", booking.getStatus().toString());
        
        yPosition -= 50;
        
        // ===== PAYMENT SUMMARY =====
        // Draw box
        contentStream.setNonStrokingColor(new Color(240, 240, 240));
        contentStream.addRect(margin, yPosition - 60, pageWidth - 2 * margin, 80);
        contentStream.fill();
        
        contentStream.setNonStrokingColor(Color.BLACK);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin + 10, yPosition);
        contentStream.showText("PAYMENT SUMMARY");
        contentStream.endText();
        
        yPosition -= lineHeight;
        
        float pricePerNight = room != null ? room.getRoomPrice() : (booking.getTotalPrice() / nights);
        
        contentStream.setFont(PDType1Font.HELVETICA, 11);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin + 10, yPosition);
        contentStream.showText("Price per night: " + currencyFormat.format(pricePerNight) + " VND");
        contentStream.endText();
        
        yPosition -= lineHeight;
        
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin + 10, yPosition);
        contentStream.showText("TOTAL: " + currencyFormat.format(booking.getTotalPrice()) + " VND");
        contentStream.endText();
        
        yPosition -= 80;
        
        // ===== FOOTER =====
        contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
        contentStream.setNonStrokingColor(Color.GRAY);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("Cam on quy khach da su dung dich vu cua chung toi!");
        contentStream.endText();
        
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, yPosition - 15);
        contentStream.showText("Thank you for choosing " + EmailConfig.HOMESTAY_NAME + "!");
        contentStream.endText();
        
        // Contact info
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, yPosition - 40);
        contentStream.showText("Contact: " + EmailConfig.HOMESTAY_PHONE + " | " + EmailConfig.HOMESTAY_EMAIL);
        contentStream.endText();
        
        contentStream.close();
        document.save(tempFile);
        document.close();
        
        return tempFile;
    }
    
    /**
     * Draw label: value pair
     */
    private void drawLabelValue(PDPageContentStream cs, float x, float y, String label, String value) throws IOException {
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(label + " " + value);
        cs.endText();
    }
}
