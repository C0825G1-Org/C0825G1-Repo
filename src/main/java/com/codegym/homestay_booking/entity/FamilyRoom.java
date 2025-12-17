package com.codegym.homestay_booking.entity;

public class FamilyRoom  extends Room{
   public FamilyRoom() {
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

   public FamilyRoom(int roomId, float roomPrice, int sleepSlot, RoomType roomType, Status status) {
      super(roomId, roomPrice, sleepSlot, roomType, status);
   }
}
