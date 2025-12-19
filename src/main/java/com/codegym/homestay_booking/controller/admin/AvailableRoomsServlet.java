package com.codegym.homestay_booking.controller.admin;

import com.codegym.homestay_booking.entity.Room;
import com.codegym.homestay_booking.repository.RoomRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

/**
 * API endpoint to get available rooms for a given date range.
 * Used by AJAX in admin booking creation form.
 */
@WebServlet(name = "AvailableRoomsServlet", urlPatterns = {"/admin/api/available-rooms"})
public class AvailableRoomsServlet extends HttpServlet {
    
    private RoomRepository roomRepository = new RoomRepository();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String checkInStr = request.getParameter("checkIn");
        String checkOutStr = request.getParameter("checkOut");
        
        PrintWriter out = response.getWriter();
        
        // Validate parameters
        if (checkInStr == null || checkInStr.isEmpty() || 
            checkOutStr == null || checkOutStr.isEmpty()) {
            response.setStatus(400);
            out.print("{\"error\": \"Missing checkIn or checkOut parameter\"}");
            return;
        }
        
        try {
            LocalDate checkIn = LocalDate.parse(checkInStr);
            LocalDate checkOut = LocalDate.parse(checkOutStr);
            
            // Validate dates
            if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
                response.setStatus(400);
                out.print("{\"error\": \"Check-out must be after check-in\"}");
                return;
            }
            
            // Get available rooms
            List<Room> availableRooms = roomRepository.findAvailableRooms(checkIn, checkOut);
            
            // Build JSON response manually (avoiding external JSON library dependency issues)
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < availableRooms.size(); i++) {
                Room room = availableRooms.get(i);
                if (i > 0) json.append(",");
                json.append("{");
                json.append("\"roomId\":").append(room.getRoomId()).append(",");
                json.append("\"roomType\":\"").append(room.getRoomType()).append("\",");
                json.append("\"roomPrice\":").append(room.getRoomPrice()).append(",");
                json.append("\"sleepSlot\":").append(room.getSleepSlot()).append(",");
                json.append("\"formattedPrice\":\"").append(room.getFormattedPrice()).append("\"");
                json.append("}");
            }
            json.append("]");
            
            out.print(json.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
