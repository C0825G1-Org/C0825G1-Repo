package com.codegym.homestay_booking.service;

import com.codegym.homestay_booking.entity.Room;
import com.codegym.homestay_booking.repository.RoomRepository;
import java.util.List;

public class RoomService implements IRoomService {
    
    private RoomRepository roomRepository = new RoomRepository();
    
    @Override
    public List<Room> getAll() {
        return roomRepository.getAll();
    }
    
    @Override
    public List<Room> getAvailableRooms() {
        return roomRepository.getAvailableRooms();
    }
    
    @Override
    public Room getById(int roomId) {
        return roomRepository.getById(roomId);
    }
    
    @Override
    public boolean save(Room room) {
        // Validation: price > 0
        if (room.getRoomPrice() <= 0) {
            return false;
        }
        
        // Validation: description not empty (basic check)
        if (room.getDescription() == null || room.getDescription().trim().isEmpty()) {
            return false;
        }
        
        // Set default status if not set
        if (room.getStatus() == null) {
            room.setStatus(Room.RoomStatus.AVAILABLE);
        }
        
        return roomRepository.save(room);
    }
    
    @Override
    public boolean update(Room room) {
        // Basic validation
        if (room.getRoomPrice() <= 0) {
            return false;
        }
        
        if (room.getDescription() == null || room.getDescription().trim().isEmpty()) {
            return false;
        }
        
        // Note: Price change only affects new bookings (snapshot logic)
        // Existing bookings already have total_price stored
        return roomRepository.update(room);
    }
    
    @Override
    public boolean delete(int roomId) {
        // Rule: Can't delete if has PENDING/CONFIRMED bookings
        if (hasActiveBookings(roomId)) {
            return false;
        }
        
        return roomRepository.delete(roomId);
    }
    
    @Override
    public boolean updateRoomStatus(int roomId, Room.RoomStatus status) {
        return roomRepository.updateStatus(roomId, status);
    }
    
    @Override
    public boolean hasActiveBookings(int roomId) {
        return roomRepository.hasActiveBookings(roomId);
    }
    
    /**
     * Filter available rooms by type and price range
     * For customer room browsing
     */
    public List<Room> filterRooms(String roomType, Float minPrice, Float maxPrice) {
        return roomRepository.filterRooms(roomType, minPrice, maxPrice);
    }
}