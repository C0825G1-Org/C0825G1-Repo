package com.codegym.homestay_booking.controller.admin;

import com.codegym.homestay_booking.entity.Booking;
import com.codegym.homestay_booking.entity.Room;
import com.codegym.homestay_booking.repository.RoomRepository;
import com.codegym.homestay_booking.service.BookingService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/bookings")
public class BookingController extends HttpServlet {
    
    private BookingService bookingService = new BookingService();
    private RoomRepository roomRepository = new RoomRepository();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        try {
            if ("create".equals(action)) {
                showCreateForm(request, response);
            } else if ("detail".equals(action)) {
                showBookingDetail(request, response);
            } else if ("invoice".equals(action)) {
                showInvoice(request, response);
            } else {
                showBookingList(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/error.jsp").forward(request, response);
        }
    }
    
    private void showBookingList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<Booking> bookings = bookingService.getAll();
        request.setAttribute("bookings", bookings);
        request.setAttribute("pageTitle", "Booking Management");
        request.setAttribute("contentPage", "booking_management/booking-list.jsp");
        request.getRequestDispatcher("/WEB-INF/views/admin/layout/admin-layout.jsp")
                .forward(request, response);
    }
    
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<Room> rooms = roomRepository.getAvailableRooms();
        request.setAttribute("rooms", rooms);
        request.setAttribute("pageTitle", "Create New Booking");
        request.setAttribute("contentPage", "booking_management/booking-create.jsp");
        request.getRequestDispatcher("/WEB-INF/views/admin/layout/admin-layout.jsp")
                .forward(request, response);
    }
    
    private void showBookingDetail(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        int bookingId = Integer.parseInt(idStr);
        
        Booking booking = bookingService.getById(bookingId);
        if (booking == null) {
            request.getSession().setAttribute("errorMessage", "Booking not found!");
            response.sendRedirect(request.getContextPath() + "/admin/bookings");
            return;
        }
        
        Room room = roomRepository.getById(booking.getRoomId());
        
        request.setAttribute("booking", booking);
        request.setAttribute("room", room);
        request.setAttribute("pageTitle", "Booking Details #" + bookingId);
        request.setAttribute("contentPage", "booking_management/booking-detail.jsp");
        request.getRequestDispatcher("/WEB-INF/views/admin/layout/admin-layout.jsp")
                .forward(request, response);
    }
    
    private void showInvoice(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        int bookingId = Integer.parseInt(idStr);
        
        Booking booking = bookingService.getById(bookingId);
        if (booking == null) {
            request.getSession().setAttribute("errorMessage", "Booking not found!");
            response.sendRedirect(request.getContextPath() + "/admin/bookings");
            return;
        }
        
        Room room = roomRepository.getById(booking.getRoomId());
        
        // Generate invoice data
        String invoiceNumber = "INVOICE-" + bookingId;
        String issueDate = java.time.LocalDate.now().toString();
        
        request.setAttribute("booking", booking);
        request.setAttribute("room", room);
        request.setAttribute("invoiceNumber", invoiceNumber);
        request.setAttribute("issueDate", issueDate);
        
        // Direct to invoice JSP (no layout - for printing)
        request.getRequestDispatcher("/WEB-INF/views/admin/booking_management/booking-invoice.jsp")
                .forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        try {
            if ("create".equals(action)) {
                handleCreateBooking(request, response);
            } else {
                handleBookingActions(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/bookings");
        }
    }
    
    private void handleCreateBooking(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            // Parse form data
            String guestName = request.getParameter("guestName");
            String guestEmail = request.getParameter("guestEmail");
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String checkInStr = request.getParameter("checkInDate");
            String checkOutStr = request.getParameter("checkOutDate");
            
            // Parse dates
            java.time.LocalDate checkIn = java.time.LocalDate.parse(checkInStr);
            java.time.LocalDate checkOut = java.time.LocalDate.parse(checkOutStr);
            
            // Calculate total price
            Room room = roomRepository.getById(roomId);
            int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
            float totalPrice = room.getRoomPrice() * nights;
            
            // Create booking object
            Booking booking = new Booking(guestName, guestEmail, roomId, 
                                         checkIn, checkOut, totalPrice, 
                                         Booking.BookingStatus.PENDING);
            
            // Save booking
            boolean success = bookingService.createBooking(booking);
            
            if (success) {
                request.getSession().setAttribute("successMessage", 
                    "Booking created successfully for " + guestName + "!");
            } else {
                request.getSession().setAttribute("errorMessage", 
                    "Failed to create booking. Room may not be available for selected dates.");
            }
            
            response.sendRedirect(request.getContextPath() + "/admin/bookings");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", 
                "Error creating booking: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/bookings?action=create");
        }
    }
    
    private void handleBookingActions(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String bookingIdStr = request.getParameter("id");
        String action = request.getParameter("action");
        
        try {
            int bookingId = Integer.parseInt(bookingIdStr);
            boolean success = false;
            String successMessage = "";
            String errorMessage = "";
            
            switch (action) {
                case "approve":
                    success = bookingService.approveBooking(bookingId);
                    successMessage = "Booking #" + bookingId + " approved successfully!";
                    errorMessage = "Failed to approve booking. It may not be PENDING or room is no longer available.";
                    break;
                    
                case "cancel":
                    success = bookingService.cancelBooking(bookingId);
                    successMessage = "Booking #" + bookingId + " cancelled successfully!";
                    errorMessage = "Failed to cancel booking. It may already be completed or cancelled.";
                    break;
                    
                case "complete":
                    success = bookingService.completeBooking(bookingId);
                    successMessage = "Booking #" + bookingId + " marked as completed!";
                    errorMessage = "Failed to complete booking. It must be CONFIRMED first.";
                    break;
                    
                default:
                    errorMessage = "Unknown action: " + action;
            }

            if (success) {
                request.getSession().setAttribute("successMessage", successMessage);
            } else {
                request.getSession().setAttribute("errorMessage", errorMessage);
            }

            response.sendRedirect(request.getContextPath() + "/admin/bookings");
            
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid booking ID");
            response.sendRedirect(request.getContextPath() + "/admin/bookings");
        }
    }
}
