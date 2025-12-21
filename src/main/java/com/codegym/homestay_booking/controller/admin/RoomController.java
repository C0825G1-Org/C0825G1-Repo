package com.codegym.homestay_booking.controller.admin;

import com.codegym.homestay_booking.entity.Room;
import com.codegym.homestay_booking.service.RoomService;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@WebServlet(name = "RoomController", urlPatterns = {"/admin/rooms"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1MB
    maxFileSize = 5 * 1024 * 1024,         // 5MB
    maxRequestSize = 10 * 1024 * 1024      // 10MB
)
public class RoomController extends HttpServlet {
    
    private RoomService roomService = new RoomService();
    private static final String UPLOAD_DIR = "uploads/rooms";
    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/png", "image/webp", "image/gif"};
    
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
            throws IOException, ServletException {
        try {
            String roomTypeStr = request.getParameter("roomType");
            int sleepSlot = Integer.parseInt(request.getParameter("sleepSlot"));
            float roomPrice = Float.parseFloat(request.getParameter("roomPrice"));
            String description = request.getParameter("description");
            
            // Handle file upload
            String imageUrl = handleImageUpload(request, null);
            
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
            throws IOException, ServletException {
        try {
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String roomTypeStr = request.getParameter("roomType");
            int sleepSlot = Integer.parseInt(request.getParameter("sleepSlot"));
            float roomPrice = Float.parseFloat(request.getParameter("roomPrice"));
            String statusStr = request.getParameter("status");
            String existingImageUrl = request.getParameter("existingImageUrl");
            String description = request.getParameter("description");
            
            // Handle file upload - keep existing image if no new file
            String imageUrl = handleImageUpload(request, existingImageUrl);
            
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
    
    /**
     * Handle image file upload
     * @param request HTTP request with multipart data
     * @param existingImageUrl existing image URL (for edit mode, keep if no new upload)
     * @return relative path to saved image or existing URL
     */
    private String handleImageUpload(HttpServletRequest request, String existingImageUrl)
            throws IOException, ServletException {
        
        Part filePart = request.getPart("image");
        
        // Check if a file was uploaded
        if (filePart == null || filePart.getSize() == 0) {
            // No file uploaded - return existing or null
            return existingImageUrl;
        }
        
        // Validate content type
        String contentType = filePart.getContentType();
        boolean validType = false;
        for (String allowed : ALLOWED_TYPES) {
            if (allowed.equals(contentType)) {
                validType = true;
                break;
            }
        }
        
        if (!validType) {
            throw new ServletException("Invalid file type. Only JPEG, PNG, WebP and GIF images are allowed.");
        }
        
        // Get original filename and extension
        String submittedFileName = filePart.getSubmittedFileName();
        if (submittedFileName == null || submittedFileName.isEmpty()) {
            return existingImageUrl;
        }
        
        // Security: prevent path traversal and dangerous extensions
        String fileName = submittedFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        String extension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = fileName.substring(dotIndex).toLowerCase();
            fileName = fileName.substring(0, dotIndex);
        }
        
        // Block dangerous extensions
        if (extension.equals(".jsp") || extension.equals(".exe") || 
            extension.equals(".js") || extension.equals(".php")) {
            throw new ServletException("File type not allowed for security reasons.");
        }
        
        // Generate unique filename: timestamp_originalname.ext
        String uniqueFileName = System.currentTimeMillis() + "_" + fileName + extension;
        
        // Use source webapp folder for uploads (persists in source code)
        String sourceUploadPath = "D:" + File.separator + "md4_case_study" + File.separator + 
            "homestay_booking" + File.separator + "src" + File.separator + "main" + 
            File.separator + "webapp" + File.separator + "uploads" + File.separator + "rooms";
        
        // Also save to deployed webapp for immediate serving
        String webappPath = getServletContext().getRealPath("");
        String webappUploadPath = webappPath + File.separator + "uploads" + File.separator + "rooms";
        
        // Create directories if not exist
        File sourceDir = new File(sourceUploadPath);
        if (!sourceDir.exists()) {
            sourceDir.mkdirs();
        }
        
        File webappDir = new File(webappUploadPath);
        if (!webappDir.exists()) {
            webappDir.mkdirs();
        }
        
        // Save file to BOTH locations
        // 1. Source folder (persistent) - survives redeploy
        Path sourceFilePath = Paths.get(sourceUploadPath, uniqueFileName);
        try (InputStream input = filePart.getInputStream()) {
            Files.copy(input, sourceFilePath, StandardCopyOption.REPLACE_EXISTING);
        }
        
        // 2. Webapp (for immediate serving)
        Path webappFilePath = Paths.get(webappUploadPath, uniqueFileName);
        Files.copy(sourceFilePath, webappFilePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Return relative path for DB storage
        return "/uploads/rooms/" + uniqueFileName;
    }
}
