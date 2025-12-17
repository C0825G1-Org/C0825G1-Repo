package com.codegym.homestay_booking.entity;

public abstract class Room {

   private int roomId;
   private float roomPrice;
   private int sleepSlot;
   private RoomType roomType;
   private Status status;

   public int getRoomId() {
         return 0;
   }


   public enum RoomType {
      FAMILY,
      BUSINESS,
      HONEYMOON
   }

   public enum Status {
      AVAILABLE,
      UNAVAILABLE
   }

   public Room() {
   }

   public Room(int roomId, float roomPrice, int sleepSlot,
               RoomType roomType, Status status) {
      this.roomId = roomId;
      this.roomPrice = roomPrice;
      this.sleepSlot = sleepSlot;
      this.roomType = roomType;
      this.status = status;
   }


   public RoomType getRoomType() {
      return roomType;
   }

   public void setRoomType(RoomType roomType) {
      this.roomType = roomType;
   }

   public Status getStatus() {
      return status;
   }

   public void setStatus(Status status) {
      this.status = status;
   }
}
