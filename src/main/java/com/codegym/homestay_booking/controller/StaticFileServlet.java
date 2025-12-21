package com.codegym.homestay_booking.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * Servlet to serve static files from /uploads/* directory
 * Serves from external persistent directory to survive redeployments
 */
@WebServlet(name = "StaticFileServlet", urlPatterns = {"/uploads/*"})
public class StaticFileServlet extends HttpServlet {
    
    // Source webapp folder for persistent uploads
    private static final String SOURCE_UPLOAD_BASE = "D:" + File.separator + "md4_case_study" + 
        File.separator + "homestay_booking" + File.separator + "src" + File.separator + 
        "main" + File.separator + "webapp" + File.separator + "uploads";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get the file path from URL (e.g., /rooms/123_image.jpg)
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Try source folder first (persistent storage)
        String sourcePath = SOURCE_UPLOAD_BASE + pathInfo.replace("/", File.separator);
        File file = new File(sourcePath);
        
        // Fallback to webapp directory
        if (!file.exists() || !file.isFile()) {
            String webappPath = getServletContext().getRealPath("/uploads" + pathInfo);
            file = new File(webappPath);
        }
        
        // Check if file exists
        if (!file.exists() || !file.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Get MIME type
        String mimeType = getServletContext().getMimeType(file.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        
        // Only serve image files for security
        if (!mimeType.startsWith("image/")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only image files allowed");
            return;
        }
        
        // Set response headers
        response.setContentType(mimeType);
        response.setContentLengthLong(file.length());
        
        // Stream file to response
        try (OutputStream out = response.getOutputStream()) {
            Files.copy(file.toPath(), out);
        }
    }
}

