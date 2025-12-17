package com.codegym.homestay_booking.service;

import com.codegym.homestay_booking.entity.Booking;

import java.time.LocalDate;
import java.util.List;

public interface IBookingService extends IService<Booking>{
    List<Booking> getByStatus(Booking.BookingStatus status);
    List<Booking> getByGuestEmail(String email);
    boolean isRoomAvailable(int roomId, LocalDate checkInDate, LocalDate checkOutDate);
    boolean isRoomAvailable(int roomId, LocalDate checkInDate, LocalDate checkOutDate, int bookingId);
    boolean createBooking(Booking booking);
    boolean approveBooking(int bookingId);
    boolean cancelBooking(int bookingId);
    boolean completeBooking(int bookingId);
}
