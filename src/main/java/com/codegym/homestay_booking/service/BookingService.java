package com.codegym.homestay_booking.service;

import com.codegym.homestay_booking.entity.Booking;
import com.codegym.homestay_booking.repository.BookingRepository;

import java.time.LocalDate;
import java.util.List;

public class BookingService implements IBookingService{
    private BookingRepository bookingRepository = new BookingRepository();
    @Override
    public List<Booking> getByStatus(Booking.BookingStatus status) {
        return List.of();
    }

    @Override
    public List<Booking> getByGuestEmail(String email) {
        return List.of();
    }

    @Override
    public boolean isRoomAvailable(int roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        return false;
    }

    @Override
    public boolean isRoomAvailable(int roomId, LocalDate checkInDate, LocalDate checkOutDate, int bookingId) {
        return false;
    }

    @Override
    public boolean createBooking(Booking booking) {
        return false;
    }

    @Override
    public boolean approveBooking(int bookingId) {
        return false;
    }

    @Override
    public boolean cancelBooking(int bookingId) {
        return false;
    }

    @Override
    public boolean completeBooking(int bookingId) {
        return false;
    }

    @Override
    public List<Booking> getAll() {
        return List.of();
    }

    @Override
    public Booking getById(int id) {
        return null;
    }

    @Override
    public boolean save(Booking entity) {
        return false;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }

    @Override
    public boolean update(Booking entity) {
        return false;
    }
}
