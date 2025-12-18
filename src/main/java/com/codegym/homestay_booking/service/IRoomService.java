package com.codegym.homestay_booking.service;

import com.codegym.homestay_booking.entity.Room;

/**
 * Room service interface extending base service with room-specific operations
 */
public interface IRoomService extends IService<Room> {
    
    /**
     * Get only available rooms (status = AVAILABLE) - for customers
     */
    java.util.List<Room> getAvailableRooms();
    
    /**
     * Update room status (AVAILABLE or UNAVAILABLE)
     * UNAVAILABLE = soft delete/hidden from customers
     */
    boolean updateRoomStatus(int roomId, Room.RoomStatus status);
    
    /**
     * Check if room has active bookings (PENDING or CONFIRMED)
     * Used for delete validation
     */
    boolean hasActiveBookings(int roomId);
}
