package com.codegym.homestay_booking.service;

import com.codegym.homestay_booking.entity.Booking;
import com.codegym.homestay_booking.repository.BaseRepository;
import com.codegym.homestay_booking.repository.BookingRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class BookingService implements IBookingService{
    private BookingRepository bookingRepository = new BookingRepository();
    @Override
    public List<Booking> getByStatus(Booking.BookingStatus status) {
        return bookingRepository.getByStatus(status);
    }

    @Override
    public List<Booking> getByGuestEmail(String email) {
        return bookingRepository.getByEmail(email);
    }

    @Override
    public boolean isRoomAvailable(int roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate == null || checkOutDate == null) return false;
        return bookingRepository.isRoomAvailable(roomId, checkInDate, checkOutDate);
    }

    @Override
    public boolean isRoomAvailable(int roomId, LocalDate checkInDate, LocalDate checkOutDate, int bookingId) {
        if (checkInDate == null || checkOutDate == null) return false;
        if (!checkInDate.isBefore(checkOutDate)) return false;
        return bookingRepository.isRoomAvailable(roomId, checkInDate, checkOutDate, bookingId);
    }

    @Override
    public boolean createBooking(Booking booking) {
        Connection connection = null;
        try {
            connection = BaseRepository.getConnection();
            connection.setAutoCommit(false);
            boolean isRoomAvailable = isRoomAvailable(booking.getRoomId(), booking.getCheckInDate(), booking.getCheckOutDate());
            if (!isRoomAvailable) {
                connection.rollback();
                return false;
            }
            booking.setStatus(Booking.BookingStatus.PENDING);
            boolean inserted = bookingRepository.insert(booking);
            if (!inserted) {
                connection.rollback();
                return false;
            }
            connection.commit();
            return true;
        } catch (Exception e) {
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                e.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (connection != null) connection.setAutoCommit(true);
            } catch (SQLException e) {}
        }
    }


    @Override
    public boolean approveBooking(int bookingId) {
        Connection connection = null;
        try {
            connection = BaseRepository.getConnection();
            connection.setAutoCommit(false);
            Booking booking = bookingRepository.getById(bookingId);
            if (booking == null) {
                connection.rollback();
                return false;
            }
            if (booking.getStatus() != Booking.BookingStatus.PENDING) {
                connection.rollback();
                return false;
            }
            boolean available = bookingRepository.isRoomAvailable(
                booking.getRoomId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                bookingId
            );
            
            if (!available) {
                connection.rollback();
                return false;
            }
            boolean updated = bookingRepository.updateStatus(bookingId, Booking.BookingStatus.CONFIRMED);
            if (!updated) {
                connection.rollback();
                return false;
            }
            
            connection.commit();
            return true;
            
        } catch (Exception e) {
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (connection != null) connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean cancelBooking(int bookingId) {
        try {
            // Fetch booking to check current status
            Booking booking = bookingRepository.getById(bookingId);
            if (booking == null) {
                return false;
            }

            if (!booking.canBeCancelled()) {
                return false;
            }

            return bookingRepository.updateStatus(bookingId, Booking.BookingStatus.CANCELLED);
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean completeBooking(int bookingId) {
        try {
            Booking booking = bookingRepository.getById(bookingId);
            if (booking == null) {
                return false;
            }

            if (booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
                return false;
            }
            return bookingRepository.updateStatus(bookingId, Booking.BookingStatus.COMPLETED);
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Booking> getAll() {
        return bookingRepository.getAll();
    }

    @Override
    public Booking getById(int id) {
        return bookingRepository.getById(id);
    }

    @Override
    public boolean save(Booking entity) {
        return false;
    }

    @Override
    public boolean delete(int id) {
        return bookingRepository.delete(id);
    }

    @Override
    public boolean update(Booking entity) {
        return false;
    }
}
