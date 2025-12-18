package com.codegym.homestay_booking.controller.customer;

import com.codegym.homestay_booking.entity.Booking;
import com.codegym.homestay_booking.entity.Booking.BookingStatus;
import com.codegym.homestay_booking.service.BookingService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "CustomerBookingController", urlPatterns = {"/booking/create", "/my-bookings"})
public class CustomerBookingController extends HttpServlet {
    
    private BookingService bookingService = new BookingService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Handle /my-bookings
        if (request.getRequestURI().contains("/my-bookings")) {
            String action = request.getParameter("action");
            if ("edit".equals(action)) {
                showEditForm(request, response);
            } else if ("changeRoom".equals(action)) {
                showChangeRoomForm(request, response);
            } else {
                showMyBookings(request, response);
            }
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Handle booking creation
        if (request.getRequestURI().contains("/booking/create")) {
            handleCreateBooking(request, response);
        } else if ("cancel".equals(request.getParameter("action"))) {
            handleCancelBooking(request, response);
        } else if ("update".equals(request.getParameter("action"))) {
            handleUpdateBooking(request, response);
        } else if ("changeRoom".equals(request.getParameter("action"))) {
            handleChangeRoom(request, response);
        }
    }
    
    private void handleCreateBooking(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            // Parse form data
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String guestName = request.getParameter("guestName");
            String guestEmail = request.getParameter("guestEmail");
            LocalDate checkIn = LocalDate.parse(request.getParameter("checkInDate"));
            LocalDate checkOut = LocalDate.parse(request.getParameter("checkOutDate"));
            
            // Create booking with PENDING status
            Booking booking = new Booking();
            booking.setRoomId(roomId);
            booking.setGuestName(guestName);
            booking.setGuestEmail(guestEmail);
            booking.setCheckInDate(checkIn);
            booking.setCheckOutDate(checkOut);
            booking.setStatus(BookingStatus.PENDING);
            
            // Service will calculate total price and validate availability
            boolean success = bookingService.createBooking(booking);
            
            if (success) {
                request.getSession().setAttribute("successMessage", 
                    "Booking submitted successfully! We'll confirm soon via email: " + guestEmail);
                response.sendRedirect(request.getContextPath() + "/my-bookings?email=" + guestEmail);
            } else {
                request.getSession().setAttribute("errorMessage", 
                    "Failed to create booking! Room may not be available for selected dates. Please try another room or dates.");
                response.sendRedirect(request.getContextPath() + "/rooms?action=detail&id=" + roomId);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/rooms");
        }
    }
    
    private void showMyBookings(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        List<Booking> bookings = null;
        
        if (email != null && !email.trim().isEmpty()) {
            bookings = bookingService.getByGuestEmail(email);
        }
        
        request.setAttribute("email", email);
        request.setAttribute("bookings", bookings);
        request.getRequestDispatcher("/WEB-INF/views/customer/my-bookings.jsp")
                .forward(request, response);
    }
    
    private void handleCancelBooking(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int bookingId = Integer.parseInt(request.getParameter("id"));
            String email = request.getParameter("email");
            
            // Customer can only REQUEST cancellation, not directly cancel
            Booking booking = bookingService.getById(bookingId);
            
            if (booking == null) {
                request.getSession().setAttribute("errorMessage", "Booking not found!");
                response.sendRedirect(request.getContextPath() + "/my-bookings?email=" + email);
                return;
            }
            
            // Only PENDING or CONFIRMED can request cancellation
            if (booking.getStatus() != Booking.BookingStatus.PENDING && 
                booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
                request.getSession().setAttribute("errorMessage", 
                    "Cannot request cancellation for this booking status!");
                response.sendRedirect(request.getContextPath() + "/my-bookings?email=" + email);
                return;
            }
            
            boolean success = bookingService.requestCancellation(bookingId);
            
            if (success) {
                request.getSession().setAttribute("successMessage", 
                    "Cancellation request submitted! Admin will review your request.");
            } else {
                request.getSession().setAttribute("errorMessage", 
                    "Failed to submit cancellation request. Please try again.");
            }
            
            response.sendRedirect(request.getContextPath() + "/my-bookings?email=" + email);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/my-bookings");
        }
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int bookingId = Integer.parseInt(request.getParameter("id"));
            String email = request.getParameter("email");
            
            Booking booking = bookingService.getById(bookingId);
            
            if (booking == null || !booking.getGuestEmail().equals(email)) {
                request.getSession().setAttribute("errorMessage", "Booking not found or access denied!");
                response.sendRedirect(request.getContextPath() + "/my-bookings?email=" + email);
                return;
            }
            
            if (!booking.canBeEdited()) {
                request.getSession().setAttribute("errorMessage", 
                    "This booking cannot be edited!");
                response.sendRedirect(request.getContextPath() + "/my-bookings?email=" + email);
                return;
            }
            
            request.setAttribute("booking", booking);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/customer/booking-edit.jsp")
                    .forward(request, response);
                    
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/my-bookings");
        }
    }
    
    private void handleUpdateBooking(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int bookingId = Integer.parseInt(request.getParameter("id"));
            String email = request.getParameter("email");
            LocalDate checkIn = LocalDate.parse(request.getParameter("checkInDate"));
            LocalDate checkOut = LocalDate.parse(request.getParameter("checkOutDate"));
            
            boolean success = bookingService.updateBookingByCustomer(
                bookingId, email, checkIn, checkOut
            );
            
            if (success) {
                request.getSession().setAttribute("successMessage", 
                    "Booking updated successfully!");
            } else {
                request.getSession().setAttribute("errorMessage", 
                    "Failed to update booking! Please check dates and room availability.");
            }
            
            response.sendRedirect(request.getContextPath() + "/my-bookings?email=" + email);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/my-bookings");
        }
    }
    
    private void showChangeRoomForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int bookingId = Integer.parseInt(request.getParameter("id"));
            String email = request.getParameter("email");
            
            Booking booking = bookingService.getById(bookingId);
            
            if (booking == null || !booking.getGuestEmail().equals(email)) {
                request.getSession().setAttribute("errorMessage", "Booking not found or access denied!");
                response.sendRedirect(request.getContextPath() + "/my-bookings?email=" + email);
                return;
            }
            
            if (!booking.canChangeRoom()) {
                request.getSession().setAttribute("errorMessage", 
                    "Cannot change room for this booking!");
                response.sendRedirect(request.getContextPath() + "/my-bookings?email=" + email);
                return;
            }
            
            // Get available rooms for the booking dates
            com.codegym.homestay_booking.repository.RoomRepository roomRepo = 
                new com.codegym.homestay_booking.repository.RoomRepository();
            List<com.codegym.homestay_booking.entity.Room> allRooms = roomRepo.getAvailableRooms();
            
            // Filter rooms that are available for booking dates (excluding current booking)
            List<com.codegym.homestay_booking.entity.Room> availableRooms = new java.util.ArrayList<>();
            for (com.codegym.homestay_booking.entity.Room room : allRooms) {
                if (room.getRoomId() != booking.getRoomId()) {
                    if (bookingService.isRoomAvailable(
                        room.getRoomId(), 
                        booking.getCheckInDate(), 
                        booking.getCheckOutDate(),
                        bookingId)) {
                        availableRooms.add(room);
                    }
                }
            }
            
            request.setAttribute("booking", booking);
            request.setAttribute("availableRooms", availableRooms);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/customer/room-change.jsp")
                    .forward(request, response);
                    
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/my-bookings");
        }
    }
    
    private void handleChangeRoom(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int bookingId = Integer.parseInt(request.getParameter("id"));
            int newRoomId = Integer.parseInt(request.getParameter("newRoomId"));
            String email = request.getParameter("email");
            
            boolean success = bookingService.changeRoom(bookingId, newRoomId, email);
            
            if (success) {
                request.getSession().setAttribute("successMessage", 
                    "Room changed successfully! Price has been recalculated.");
            } else {
                request.getSession().setAttribute("errorMessage", 
                    "Failed to change room! Room may not be available.");
            }
            
            response.sendRedirect(request.getContextPath() + "/my-bookings?email=" + email);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/my-bookings");
        }
    }
}
