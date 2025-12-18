package com.codegym.homestay_booking.controller.admin;

import com.codegym.homestay_booking.entity.Booking;
import com.codegym.homestay_booking.entity.Room;
import com.codegym.homestay_booking.dto.RevenueData;
import com.codegym.homestay_booking.dto.StatusCount;
import com.codegym.homestay_booking.dto.MonthlyBooking;
import com.codegym.homestay_booking.dto.RoomBookingCount;
import com.codegym.homestay_booking.repository.BookingRepository;
import com.codegym.homestay_booking.repository.RoomRepository;
import com.codegym.homestay_booking.service.AIInsightService;
import com.codegym.homestay_booking.service.AIInsightPromptBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin", "/admin/dashboard"})
public class DashboardServlet extends HttpServlet {
    
    private BookingRepository bookingRepository = new BookingRepository();
    private RoomRepository roomRepository = new RoomRepository();
    private AIInsightService aiInsightService = new AIInsightService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        // Handle AI insight generation
        if ("generateInsight".equals(action)) {
            generateAIInsight(request, response);
            return;
        }
        
        // Default dashboard view
        showDashboard(request, response);
    }
    
    private void showDashboard(HttpServletRequest request, HttpServletResponse response)
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
    
    private void generateAIInsight(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Gather statistics for AI analysis
            Map<String, Object> stats = new HashMap<>();
            
            // === KPI STATS ===
            int totalBookings = bookingRepository.countAllBookings();
            int pendingBookings = bookingRepository.countByStatus(Booking.BookingStatus.PENDING);
            int confirmedBookings = bookingRepository.countByStatus(Booking.BookingStatus.CONFIRMED);
            int cancelledBookings = bookingRepository.countByStatus(Booking.BookingStatus.CANCELLED);
            int completedBookings = bookingRepository.countByStatus(Booking.BookingStatus.COMPLETED);
            int cancelRequestBookings = bookingRepository.countByStatus(Booking.BookingStatus.CANCELLED_REQUEST);
            
            stats.put("Total Bookings", totalBookings);
            stats.put("Pending Bookings", pendingBookings);
            stats.put("Confirmed Bookings", confirmedBookings);
            stats.put("Cancelled Bookings", cancelledBookings);
            stats.put("Completed Bookings", completedBookings);
            stats.put("Cancel Request Bookings", cancelRequestBookings);
            
            // Calculate rates
            if (totalBookings > 0) {
                stats.put("Pending Rate (%)", String.format("%.1f", (pendingBookings * 100.0 / totalBookings)));
                stats.put("Confirmation Rate (%)", String.format("%.1f", (confirmedBookings * 100.0 / totalBookings)));
                stats.put("Cancellation Rate (%)", String.format("%.1f", (cancelledBookings * 100.0 / totalBookings)));
                stats.put("Completion Rate (%)", String.format("%.1f", (completedBookings * 100.0 / totalBookings)));
            }
            
            // === REVENUE STATS ===
            double totalRevenue = bookingRepository.sumTotalRevenue();
            LocalDate now = LocalDate.now();
            double monthlyRevenue = bookingRepository.sumRevenueByMonth(now.getMonthValue(), now.getYear());
            stats.put("Total Revenue (VND)", String.format("%.0f", totalRevenue));
            stats.put("This Month Revenue (VND)", String.format("%.0f", monthlyRevenue));
            
            // === ROOM STATS ===
            int totalRooms = roomRepository.countAllRooms();
            int availableRooms = roomRepository.countByStatus(Room.RoomStatus.AVAILABLE);
            int unavailableRooms = roomRepository.countByStatus(Room.RoomStatus.UNAVAILABLE);
            double avgStay = bookingRepository.getAverageStayDuration();
            
            stats.put("Total Rooms", totalRooms);
            stats.put("Available Rooms", availableRooms);
            stats.put("Unavailable Rooms", unavailableRooms);
            stats.put("Average Stay Duration (nights)", String.format("%.1f", avgStay));
            
            // === REVENUE TREND (Last 30 days) ===
            List<RevenueData> revenueTrend = bookingRepository.getRevenueTrend(30);
            if (!revenueTrend.isEmpty()) {
                StringBuilder trendSummary = new StringBuilder();
                double maxRevenue = 0, minRevenue = Double.MAX_VALUE;
                String maxDate = "", minDate = "";
                double totalTrend = 0;
                
                for (RevenueData rd : revenueTrend) {
                    totalTrend += rd.getRevenue();
                    if (rd.getRevenue() > maxRevenue) {
                        maxRevenue = rd.getRevenue();
                        maxDate = rd.getDate();
                    }
                    if (rd.getRevenue() < minRevenue) {
                        minRevenue = rd.getRevenue();
                        minDate = rd.getDate();
                    }
                }
                
                stats.put("Revenue Trend Days Analyzed", revenueTrend.size());
                stats.put("Average Daily Revenue (VND)", String.format("%.0f", totalTrend / revenueTrend.size()));
                stats.put("Peak Revenue Day", maxDate + " (" + String.format("%.0f", maxRevenue) + " VND)");
                stats.put("Lowest Revenue Day", minDate + " (" + String.format("%.0f", minRevenue) + " VND)");
            }
            
            // === MONTHLY BOOKING TRENDS ===
            List<MonthlyBooking> monthlyBookings = bookingRepository.getMonthlyBookings();
            if (!monthlyBookings.isEmpty()) {
                String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                StringBuilder monthlyTrend = new StringBuilder();
                int peakMonth = 0, peakCount = 0;
                
                for (MonthlyBooking mb : monthlyBookings) {
                    if (mb.getCount() > peakCount) {
                        peakCount = mb.getCount();
                        peakMonth = mb.getMonth();
                    }
                    monthlyTrend.append(monthNames[mb.getMonth() - 1]).append(": ").append(mb.getCount()).append(" bookings, ");
                }
                
                stats.put("Monthly Booking Distribution", monthlyTrend.toString());
                if (peakMonth > 0) {
                    stats.put("Peak Booking Month", monthNames[peakMonth - 1] + " (" + peakCount + " bookings)");
                }
            }
            
            // === TOP BOOKED ROOMS ===
            List<RoomBookingCount> topRooms = bookingRepository.getTopRooms(5);
            if (!topRooms.isEmpty()) {
                StringBuilder topRoomsSummary = new StringBuilder();
                for (int i = 0; i < topRooms.size(); i++) {
                    RoomBookingCount rbc = topRooms.get(i);
                    topRoomsSummary.append("Room #").append(rbc.getRoomId())
                        .append(": ").append(rbc.getBookingCount()).append(" bookings");
                    if (i < topRooms.size() - 1) topRoomsSummary.append(", ");
                }
                stats.put("Top Performing Rooms", topRoomsSummary.toString());
            }
            
            // Build prompt
            String prompt = AIInsightPromptBuilder.buildDashboardInsight(stats);
            
            // Generate insight
            String insight = aiInsightService.generateInsight(prompt);
            
            // Store in session (use Date object for JSP formatting)
            request.getSession().setAttribute("aiInsight", insight);
            request.getSession().setAttribute("aiInsightTimestamp", new java.util.Date());
            
        } catch (Exception e) {
            e.printStackTrace();
            // Store error message instead of crashing
            request.getSession().setAttribute("aiInsight", 
                "❌ Lỗi khi tạo AI insight: " + e.getMessage() + "\n\nVui lòng kiểm tra server logs.");
            request.getSession().setAttribute("aiInsightTimestamp", new java.util.Date());
        }
        
        // Redirect back to dashboard
        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }
}
