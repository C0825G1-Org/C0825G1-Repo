package com.codegym.homestay_booking.service;

import com.codegym.homestay_booking.entity.BusinessRoom;
import com.codegym.homestay_booking.entity.Room;
import com.codegym.homestay_booking.repository.BaseRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomService implements IService<Room> {

   public final String ROOMS_OPTIONLIST = "room_booking.booking";

   @Override
   public List<Room> getAll() {
      List<Room> rooms = new ArrayList<>();
      try {
         ResultSet rs = BaseRepository.getConnection()
               .prepareStatement("SELECT * FROM room")
               .executeQuery();

         while (rs.next()) {
            Room room = new BusinessRoom(
                  rs.getInt("room_id"),
                  rs.getFloat("room_price"),
                  rs.getInt("sleep_slot")
            );
            rooms.add(room);
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return rooms;
   }

   @Override
   public Room getById(int id) {
      List<Room> roomList = getAll();

      for (Room room : roomList) {
         if (room.getRoomId() == id) {
            return room;
         }
      }
      return null;
   }

   @Override
   public boolean save(Room entity) {
      return false;
   }


   @Override
   public boolean delete(Room entity) {
      return false;
   }

   @Override
   public boolean update(Room entity) {
      List<Room> roomList;
      try {

      } catch (Exception e) {

      }
      return false;
   }
}