package com.codegym.homestay_booking.controller.customer;

import com.codegym.homestay_booking.entity.Room;
import com.codegym.homestay_booking.service.RoomService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "CustomerRoomController", urlPatterns = {"/rooms", "/"})
public class CustomerRoomController extends HttpServlet {
    
    private RoomService roomService = new RoomService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        try {
            if ("detail".equals(action)) {
                showRoomDetail(request, response);
            } else {
                showRoomList(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
    
    private void showRoomList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get filter parameters
        String roomType = request.getParameter("type");
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");
        
        List<Room> rooms;
        
        // Apply filters if provided
        if (roomType != null || minPriceStr != null || maxPriceStr != null) {
            Float minPrice = (minPriceStr != null && !minPriceStr.isEmpty()) ? Float.parseFloat(minPriceStr) : null;
            Float maxPrice = (maxPriceStr != null && !maxPriceStr.isEmpty()) ? Float.parseFloat(maxPriceStr) : null;
            
            rooms = roomService.filterRooms(roomType, minPrice, maxPrice);
        } else {
            // No filters - show all available rooms
            rooms = roomService.getAvailableRooms();
        }
        
        request.setAttribute("rooms", rooms);
        request.setAttribute("selectedType", roomType);
        request.setAttribute("minPrice", minPriceStr);
        request.setAttribute("maxPrice", maxPriceStr);
        
        request.getRequestDispatcher("/WEB-INF/views/customer/room-list.jsp")
                .forward(request, response);
    }
    
    private void showRoomDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        int roomId = Integer.parseInt(idStr);
        
        Room room = roomService.getById(roomId);
        
        if (room == null) {
            request.setAttribute("errorMessage", "Room not found!");
            request.getRequestDispatcher("/WEB-INF/views/customer/room-list.jsp")
                    .forward(request, response);
            return;
        }
        
        request.setAttribute("room", room);
        request.getRequestDispatcher("/WEB-INF/views/customer/room-detail.jsp")
                .forward(request, response);
    }
}
