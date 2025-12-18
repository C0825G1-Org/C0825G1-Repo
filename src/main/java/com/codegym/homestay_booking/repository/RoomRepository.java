package com.codegym.homestay_booking.repository;

import com.codegym.homestay_booking.entity.Room;
import com.codegym.homestay_booking.entity.Room.RoomStatus;
import com.codegym.homestay_booking.entity.Room.RoomType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomRepository {
    
    private static final String SELECT_ALL = "SELECT * FROM room ORDER BY room_id";
    private static final String SELECT_BY_ID = "SELECT * FROM room WHERE room_id = ?";
    private static final String SELECT_AVAILABLE = "SELECT * FROM room WHERE status = 'AVAILABLE' ORDER BY room_id";
    
    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        
        String roomTypeStr = rs.getString("room_type");
        room.setRoomType(RoomType.valueOf(roomTypeStr.replace(" ", "_")));
        
        room.setSleepSlot(rs.getInt("sleep_slot"));
        room.setRoomPrice(rs.getFloat("room_price"));
        
        String statusStr = rs.getString("status");
        room.setStatus(RoomStatus.valueOf(statusStr));
        
        room.setImageUrl(rs.getString("image_url"));
        room.setDescription(rs.getString("description"));
        
        return room;
    }
    
    public List<Room> getAll() {
        List<Room> rooms = new ArrayList<>();
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(SELECT_ALL);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }
    
    public Room getById(int id) {
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(SELECT_BY_ID);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToRoom(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Room> getAvailableRooms() {
        List<Room> rooms = new ArrayList<>();
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(SELECT_AVAILABLE);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }
    
    public boolean save(Room room) {
        String sql = "INSERT INTO room (room_type, sleep_slot, room_price, status, image_url, description) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(sql);
            ps.setString(1, room.getRoomType().toString());
            ps.setInt(2, room.getSleepSlot());
            ps.setFloat(3, room.getRoomPrice());
            ps.setString(4, room.getStatus().toString());
            ps.setString(5, room.getImageUrl());
            ps.setString(6, room.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean update(Room room) {
        String sql = "UPDATE room SET room_type = ?, sleep_slot = ?, room_price = ?, status = ?, image_url = ?, description = ? WHERE room_id = ?";
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(sql);
            ps.setString(1, room.getRoomType().toString());
            ps.setInt(2, room.getSleepSlot());
            ps.setFloat(3, room.getRoomPrice());
            ps.setString(4, room.getStatus().toString());
            ps.setString(5, room.getImageUrl());
            ps.setString(6, room.getDescription());
            ps.setInt(7, room.getRoomId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean delete(int roomId) {
        String sql = "DELETE FROM room WHERE room_id = ?";
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(sql);
            ps.setInt(1, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateStatus(int roomId, RoomStatus status) {
        String sql = "UPDATE room SET status = ? WHERE room_id = ?";
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(sql);
            ps.setString(1, status.toString());
            ps.setInt(2, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Check if room has active bookings (PENDING or CONFIRMED status)
     * Used for delete validation
     */
    public boolean hasActiveBookings(int roomId) {
        String sql = "SELECT COUNT(*) FROM booking WHERE room_id = ? AND status IN ('PENDING', 'CONFIRMED')";
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(sql);
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Filter available rooms by type and price range
     * All filters are optional
     */
    public List<Room> filterRooms(String roomType, Float minPrice, Float maxPrice) {
        List<Room> rooms = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM room WHERE status = 'AVAILABLE'");
        List<Object> params = new ArrayList<>();
        
        if (roomType != null && !roomType.isEmpty()) {
            sql.append(" AND room_type = ?");
            params.add(roomType);
        }
        
        if (minPrice != null) {
            sql.append(" AND room_price >= ?");
            params.add(minPrice);
        }
        
        if (maxPrice != null) {
            sql.append(" AND room_price <= ?");
            params.add(maxPrice);
        }
        
        sql.append(" ORDER BY room_id");
        
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(sql.toString());
            
            // Set parameters dynamically
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else if (param instanceof Float) {
                    ps.setFloat(i + 1, (Float) param);
                }
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return rooms;
    }
}
