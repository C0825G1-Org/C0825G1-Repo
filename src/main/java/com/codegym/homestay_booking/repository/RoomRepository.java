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
}
