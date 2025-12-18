package com.codegym.homestay_booking.controller.admin;

import com.codegym.homestay_booking.entity.Room;
import com.codegym.homestay_booking.service.RoomService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "RoomController", urlPatterns = {"/admin/rooms"})
public class RoomController extends HttpServlet {
    
    private RoomService roomService = new RoomService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        try {
            if ("create".equals(action)) {
                showCreateForm(request, response);
            } else if ("edit".equals(action)) {
                showEditForm(request, response);
            } else {
                showRoomList(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/error.jsp").forward(request, response);
        }
    }
    
    private void showRoomList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Room> rooms = roomService.getAll();
        request.setAttribute("rooms", rooms);
        request.setAttribute("pageTitle", "Room Management");
        request.setAttribute("contentPage", "room_management/room-list.jsp");
        request.getRequestDispatcher("/WEB-INF/views/admin/layout/admin-layout.jsp")
                .forward(request, response);
    }
    
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("pageTitle", "Add New Room");
        request.setAttribute("contentPage", "room_management/room-create.jsp");
        request.getRequestDispatcher("/WEB-INF/views/admin/layout/admin-layout.jsp")
                .forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        int roomId = Integer.parseInt(idStr);
        
        Room room = roomService.getById(roomId);
        if (room == null) {
            request.getSession().setAttribute("errorMessage", "Room not found!");
            response.sendRedirect(request.getContextPath() + "/admin/rooms");
            return;
        }
        
        request.setAttribute("room", room);
        request.setAttribute("pageTitle", "Edit Room #" + roomId);
        request.setAttribute("contentPage", "room_management/room-edit.jsp");
        request.getRequestDispatcher("/WEB-INF/views/admin/layout/admin-layout.jsp")
                .forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        try {
            if ("create".equals(action)) {
                handleCreateRoom(request, response);
            } else if ("update".equals(action)) {
                handleUpdateRoom(request, response);
            } else if ("delete".equals(action)) {
                handleDeleteRoom(request, response);
            } else if ("updateStatus".equals(action)) {
                handleUpdateStatus(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/rooms");
        }
    }
    
    private void handleCreateRoom(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String roomTypeStr = request.getParameter("roomType");
            int sleepSlot = Integer.parseInt(request.getParameter("sleepSlot"));
            float roomPrice = Float.parseFloat(request.getParameter("roomPrice"));
            String imageUrl = request.getParameter("imageUrl");
            String description = request.getParameter("description");
            
            Room room = new Room();
            room.setRoomType(Room.RoomType.valueOf(roomTypeStr));
            room.setSleepSlot(sleepSlot);
            room.setRoomPrice(roomPrice);
            room.setImageUrl(imageUrl);
            room.setDescription(description);
            room.setStatus(Room.RoomStatus.AVAILABLE);
            
            boolean success = roomService.save(room);
            
            if (success) {
                request.getSession().setAttribute("successMessage", "Room created successfully!");
                response.sendRedirect(request.getContextPath() + "/admin/rooms");
            } else {
                request.getSession().setAttribute("errorMessage", 
                    "Failed to create room! Price must be > 0, Description required.");
                response.sendRedirect(request.getContextPath() + "/admin/rooms?action=create");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/rooms?action=create");
        }
    }
    
    private void handleUpdateRoom(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String roomTypeStr = request.getParameter("roomType");
            int sleepSlot = Integer.parseInt(request.getParameter("sleepSlot"));
            float roomPrice = Float.parseFloat(request.getParameter("roomPrice"));
            String statusStr = request.getParameter("status");
            String imageUrl = request.getParameter("imageUrl");
            String description = request.getParameter("description");
            
            Room room = new Room();
            room.setRoomId(roomId);
            room.setRoomType(Room.RoomType.valueOf(roomTypeStr));
            room.setSleepSlot(sleepSlot);
            room.setRoomPrice(roomPrice);
            room.setStatus(Room.RoomStatus.valueOf(statusStr));
            room.setImageUrl(imageUrl);
            room.setDescription(description);
            
            boolean success = roomService.update(room);
            
            if (success) {
                request.getSession().setAttribute("successMessage", "Room updated successfully!");
            } else {
                request.getSession().setAttribute("errorMessage", 
                    "Failed to update! Price must be > 0, Description required.");
            }
            
            response.sendRedirect(request.getContextPath() + "/admin/rooms");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/rooms");
        }
    }
    
    private void handleDeleteRoom(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int roomId = Integer.parseInt(request.getParameter("id"));
            
            boolean success = roomService.delete(roomId);
            
            if (success) {
                request.getSession().setAttribute("successMessage", "Room deleted successfully!");
            } else {
                request.getSession().setAttribute("errorMessage", 
                    "Cannot delete! Room has PENDING/CONFIRMED bookings. Use UNAVAILABLE status instead.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Error: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/rooms");
    }
    
    private void handleUpdateStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int roomId = Integer.parseInt(request.getParameter("id"));
            String statusStr = request.getParameter("status");
            Room.RoomStatus status = Room.RoomStatus.valueOf(statusStr);
            
            boolean success = roomService.updateRoomStatus(roomId, status);
            
            if (success) {
                request.getSession().setAttribute("successMessage", "Status updated to " + status + "!");
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to update status!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Error: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/rooms");
    }
}
