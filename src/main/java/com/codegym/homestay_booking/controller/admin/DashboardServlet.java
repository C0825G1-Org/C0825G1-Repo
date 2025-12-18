package com.codegym.homestay_booking.controller.admin;

import com.codegym.homestay_booking.entity.Booking;
import com.codegym.homestay_booking.entity.Room;
import com.codegym.homestay_booking.dto.RevenueData;
import com.codegym.homestay_booking.dto.StatusCount;
import com.codegym.homestay_booking.dto.MonthlyBooking;
import com.codegym.homestay_booking.dto.RoomBookingCount;
import com.codegym.homestay_booking.repository.BookingRepository;
import com.codegym.homestay_booking.repository.RoomRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin", "/admin/dashboard"})
public class DashboardServlet extends HttpServlet {
    
    private BookingRepository bookingRepository = new BookingRepository();
    private RoomRepository roomRepository = new RoomRepository();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Booking Statistics
        int totalBookings = bookingRepository.countAllBookings();
        int pendingBookings = bookingRepository.countByStatus(Booking.BookingStatus.PENDING);
        int confirmedBookings = bookingRepository.countByStatus(Booking.BookingStatus.CONFIRMED);
        int cancelledBookings = bookingRepository.countByStatus(Booking.BookingStatus.CANCELLED);
        int completedBookings = bookingRepository.countByStatus(Booking.BookingStatus.COMPLETED);
        
        // Revenue Statistics
        double totalRevenue = bookingRepository.sumTotalRevenue();
        LocalDate now = LocalDate.now();
        double monthlyRevenue = bookingRepository.sumRevenueByMonth(now.getMonthValue(), now.getYear());
        
        // Room Statistics
        int totalRooms = roomRepository.countAllRooms();
        int availableRooms = roomRepository.countByStatus(Room.RoomStatus.AVAILABLE);
        int unavailableRooms = roomRepository.countByStatus(Room.RoomStatus.UNAVAILABLE);
        
        // Recent Bookings
        List<Booking> recentBookings = bookingRepository.getRecentBookings(10);
        
        // Revenue Trend (last 30 days)
        List<RevenueData> revenueTrend = bookingRepository.getRevenueTrend(30);
        
        // Analytics Data
        List<StatusCount> statusDistribution = bookingRepository.getStatusDistribution();
        List<MonthlyBooking> monthlyBookings = bookingRepository.getMonthlyBookings();
        List<RoomBookingCount> topRooms = bookingRepository.getTopRooms(5);
        double avgStayDuration = bookingRepository.getAverageStayDuration();
        
        // Set attributes
        request.setAttribute("totalBookings", totalBookings);
        request.setAttribute("pendingBookings", pendingBookings);
        request.setAttribute("confirmedBookings", confirmedBookings);
        request.setAttribute("cancelledBookings", cancelledBookings);
        request.setAttribute("completedBookings", completedBookings);
        
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("monthlyRevenue", monthlyRevenue);
        
        request.setAttribute("totalRooms", totalRooms);
        request.setAttribute("availableRooms", availableRooms);
        request.setAttribute("unavailableRooms", unavailableRooms);
        
        request.setAttribute("recentBookings", recentBookings);
        request.setAttribute("revenueTrend", revenueTrend);
        request.setAttribute("statusDistribution", statusDistribution);
        request.setAttribute("monthlyBookings", monthlyBookings);
        request.setAttribute("topRooms", topRooms);
        request.setAttribute("avgStayDuration", String.format("%.1f", avgStayDuration));
        
        request.setAttribute("pageTitle", "Dashboard");
        request.setAttribute("contentPage", "dashboard.jsp");
        request.getRequestDispatcher("/WEB-INF/views/admin/layout/admin-layout.jsp")
                .forward(request, response);
    }
}
