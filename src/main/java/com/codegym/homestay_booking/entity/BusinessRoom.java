package com.codegym.homestay_booking.entity;

public class BusinessRoom  extends Room{
   public BusinessRoom() {
   }

   public BusinessRoom(int roomId, float roomPrice, int sleepSlot) {

   }

   @Override
   public RoomType getRoomType() {
      return super.getRoomType();
   }

   @Override
   public void setRoomType(RoomType roomType) {
      super.setRoomType(roomType);
   }

   @Override
   public Status getStatus() {
      return super.getStatus();
   }

   @Override
   public void setStatus(Status status) {
      super.setStatus(status);
   }

   public BusinessRoom(int roomId, float roomPrice, int sleepSlot, RoomType roomType, Status status) {
      super(roomId, roomPrice, sleepSlot, roomType, status);
   }
}
