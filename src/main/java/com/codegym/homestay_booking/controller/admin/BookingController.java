package com.codegym.homestay_booking.controller.admin;

import com.codegym.homestay_booking.entity.Booking;
import com.codegym.homestay_booking.entity.Room;
import com.codegym.homestay_booking.repository.BookingRepository;
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
            } else if ("edit".equals(action)) {
                showEditForm(request, response);
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
        
        // Pagination settings
        int pageSize = 10;
        int currentPage = 1;
        
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }
        
        // Filter and search parameters
        String statusFilter = request.getParameter("status");
        String searchKeyword = request.getParameter("search");
        
        // Clean up parameters
        if (statusFilter != null && statusFilter.isEmpty()) statusFilter = null;
        if (searchKeyword != null && searchKeyword.trim().isEmpty()) searchKeyword = null;
        else if (searchKeyword != null) searchKeyword = searchKeyword.trim();
        
        // Get bookings based on filter/search combination
        BookingRepository bookingRepository = new BookingRepository();
        List<Booking> bookings;
        int totalBookings;
        
        if (statusFilter != null && searchKeyword != null) {
            // Both status filter and search
            Booking.BookingStatus status = Booking.BookingStatus.valueOf(statusFilter);
            bookings = bookingRepository.searchWithStatusPaginated(status, searchKeyword, currentPage, pageSize);
            totalBookings = bookingRepository.countSearchWithStatus(status, searchKeyword);
        } else if (statusFilter != null) {
            // Only status filter
            Booking.BookingStatus status = Booking.BookingStatus.valueOf(statusFilter);
            bookings = bookingRepository.filterByStatusPaginated(status, currentPage, pageSize);
            totalBookings = bookingRepository.countByStatusFilter(status);
        } else if (searchKeyword != null) {
            // Only search
            bookings = bookingRepository.searchPaginated(searchKeyword, currentPage, pageSize);
            totalBookings = bookingRepository.countSearch(searchKeyword);
        } else {
            // No filter, no search - get all paginated
            bookings = bookingRepository.getPaginated(currentPage, pageSize);
            totalBookings = bookingRepository.getTotalCount();
        }
        
        int totalPages = (int) Math.ceil((double) totalBookings / pageSize);
        if (totalPages == 0) totalPages = 1;
        
        // Ensure currentPage doesn't exceed totalPages
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
            // Re-fetch with corrected page
            if (statusFilter != null && searchKeyword != null) {
                Booking.BookingStatus status = Booking.BookingStatus.valueOf(statusFilter);
                bookings = bookingRepository.searchWithStatusPaginated(status, searchKeyword, currentPage, pageSize);
            } else if (statusFilter != null) {
                Booking.BookingStatus status = Booking.BookingStatus.valueOf(statusFilter);
                bookings = bookingRepository.filterByStatusPaginated(status, currentPage, pageSize);
            } else if (searchKeyword != null) {
                bookings = bookingRepository.searchPaginated(searchKeyword, currentPage, pageSize);
            } else {
                bookings = bookingRepository.getPaginated(currentPage, pageSize);
            }
        }
        
        request.setAttribute("bookings", bookings);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalBookings", totalBookings);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("statusFilter", statusFilter);
        request.setAttribute("searchKeyword", searchKeyword);
        
        // System-wide statistics (not affected by filters/pagination)
        request.setAttribute("allPendingCount", bookingRepository.countByStatus(Booking.BookingStatus.PENDING));
        request.setAttribute("allConfirmedCount", bookingRepository.countByStatus(Booking.BookingStatus.CONFIRMED));
        request.setAttribute("allCancelledCount", bookingRepository.countByStatus(Booking.BookingStatus.CANCELLED));
        request.setAttribute("allCompletedCount", bookingRepository.countByStatus(Booking.BookingStatus.COMPLETED));
        request.setAttribute("allCancelRequestCount", bookingRepository.countByStatus(Booking.BookingStatus.CANCELLED_REQUEST));
        request.setAttribute("allBookingsCount", bookingRepository.getTotalCount());
        
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

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        int bookingId = Integer.parseInt(idStr);

        Booking booking = bookingService.getById(bookingId);
        if (booking == null) {
            request.getSession().setAttribute("errorMessage", "Booking not found!");
            response.sendRedirect(request.getContextPath() + "/admin/bookings");
            return;
        }

        // Check if booking can be edited
        if (!booking.canBeEdited()) {
            request.getSession().setAttribute("errorMessage",
                    "Cannot edit this booking. Booking must be PENDING or CONFIRMED (before check-in date).");
            response.sendRedirect(request.getContextPath() + "/admin/bookings?action=detail&id=" + bookingId);
            return;
        }

        // Get available rooms for dropdown
        List<Room> rooms = roomRepository.getAvailableRooms();

        request.setAttribute("booking", booking);
        request.setAttribute("rooms", rooms);
        request.setAttribute("pageTitle", "Edit Booking #" + bookingId);
        request.setAttribute("contentPage", "booking_management/booking-edit.jsp");
        request.getRequestDispatcher("/WEB-INF/views/admin/layout/admin-layout.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if ("create".equals(action)) {
                handleCreateBooking(request, response);
            } else if ("update".equals(action)) {
                handleUpdateBooking(request, response);
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
                response.sendRedirect(request.getContextPath() + "/admin/bookings");
            } else {
                request.getSession().setAttribute("errorMessage",
                        "Failed to create booking! The selected room is not available for the chosen dates. Please select different dates or another room.");
                response.sendRedirect(request.getContextPath() + "/admin/bookings?action=create");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage",
                    "Error creating booking: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/bookings?action=create");
        }
    }

    private void handleUpdateBooking(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            // Parse form data
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
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

            // Get existing booking to preserve status
            Booking existingBooking = bookingService.getById(bookingId);
            if (existingBooking == null) {
                request.getSession().setAttribute("errorMessage", "Booking not found!");
                response.sendRedirect(request.getContextPath() + "/admin/bookings");
                return;
            }

            // Create updated booking object
            Booking updatedBooking = new Booking(
                    guestName, guestEmail, roomId,
                    checkIn, checkOut, totalPrice,
                    existingBooking.getStatus()  // Preserve existing status
            );
            updatedBooking.setBookingId(bookingId);

            // Update booking via service
            boolean success = bookingService.updateBooking(updatedBooking);

            if (success) {
                request.getSession().setAttribute("successMessage",
                        "Booking #" + bookingId + " updated successfully!");
                response.sendRedirect(request.getContextPath() + "/admin/bookings?action=detail&id=" + bookingId);
            } else {
                request.getSession().setAttribute("errorMessage",
                        "Failed to update booking! Possible reasons: Room not available for selected dates, Check-in date is in the past, or Booking cannot be edited (check status/check-in date).");
                response.sendRedirect(request.getContextPath() + "/admin/bookings?action=edit&id=" + bookingId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            String bookingId = request.getParameter("bookingId");
            request.getSession().setAttribute("errorMessage",
                    "Error updating booking: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/bookings?action=edit&id=" + bookingId);
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
                case "approveCancelRequest":
                    success = bookingService.approveCancelRequest(bookingId);
                    successMessage = "Cancel request approved! Booking #" + bookingId + " cancelled.";
                    errorMessage = "Failed to approve. Must be CANCELLED_REQUEST status.";
                    break;

                case "rejectCancelRequest":
                    success = bookingService.rejectCancelRequest(bookingId);
                    successMessage = "Cancel request rejected! Booking #" + bookingId + " returned to PENDING.";
                    errorMessage = "Failed to reject. Must be CANCELLED_REQUEST status.";
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
