package com.codegym.homestay_booking.repository;

import com.codegym.homestay_booking.entity.Booking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingRepository {
    private static final String INSERT_BOOKING =
            "INSERT INTO booking (guest_name, guest_email, room_id, check_in_date, check_out_date, total_price, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_ALL =
            "SELECT * FROM booking ORDER BY booking_id DESC";

    private static final String SELECT_BY_ID =
            "SELECT * FROM booking WHERE booking_id = ?";

    private static final String SELECT_BY_STATUS =
            "SELECT * FROM booking WHERE status = ? ORDER BY booking_id DESC";

    private static final String SELECT_BY_EMAIL =
            "SELECT * FROM booking WHERE guest_email = ? ORDER BY booking_id DESC";

    private static final String UPDATE_STATUS =
            "UPDATE booking SET status = ? WHERE booking_id = ?";

    private static final String UPDATE_BOOKING =
            "UPDATE booking SET guest_name = ?, guest_email = ?, room_id = ?, " +
                    "check_in_date = ?, check_out_date = ?, total_price = ?, status = ? " +
                    "WHERE booking_id = ?";

    private static final String DELETE_BOOKING =
            "DELETE FROM booking WHERE booking_id = ?";

    private static final String CHECK_AVAILABILITY =
            "SELECT COUNT(*) FROM booking " +
                    "WHERE room_id = ? " +
                    "AND status IN ('PENDING', 'CONFIRMED') " +
                    "AND check_in_date < ? " +
                    "AND check_out_date > ?";

    private static final String CHECK_AVAILABILITY_EXCLUDE =
            "SELECT COUNT(*) FROM booking " +
                    "WHERE room_id = ? " +
                    "AND status IN ('PENDING', 'CONFIRMED') " +
                    "AND check_in_date < ? " +
                    "AND check_out_date > ? " +
                    "AND booking_id != ?";

    private Booking mapResultSetToBooking(ResultSet rs) throws java.sql.SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setGuestName(rs.getString("guest_name"));
        booking.setGuestEmail(rs.getString("guest_email"));
        booking.setRoomId(rs.getInt("room_id"));
        booking.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
        booking.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
        booking.setTotalPrice(rs.getFloat("total_price"));
        booking.setStatus(Booking.BookingStatus.valueOf(rs.getString("status")));
        return booking;
    }

    public boolean insert(Booking booking) {
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(INSERT_BOOKING);
            ps.setString(1, booking.getGuestName());
            ps.setString(2, booking.getGuestEmail());
            ps.setInt(3, booking.getRoomId());
            ps.setDate(4, java.sql.Date.valueOf(booking.getCheckInDate()));
            ps.setDate(5, java.sql.Date.valueOf(booking.getCheckOutDate()));
            ps.setFloat(6, booking.getTotalPrice());
            ps.setString(7, booking.getStatus().toString());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Booking> getAll() {
        List<Booking> bookings = new ArrayList<>();
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(SELECT_ALL);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    public Booking getById(int id) {
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(SELECT_BY_ID);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {  // MUST call rs.next() first!
                return mapResultSetToBooking(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Booking> getByStatus(Booking.BookingStatus status) {
        List<Booking> bookings = new ArrayList<>();
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(SELECT_BY_STATUS);
            ps.setString(1, status.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public List<Booking> getByEmail(String email) {
        List<Booking> bookings = new ArrayList<>();
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(SELECT_BY_EMAIL);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public boolean updateStatus(int bookingId, Booking.BookingStatus status) {
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(UPDATE_STATUS);
            ps.setString(1, status.toString());
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int bookingId) {
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(DELETE_BOOKING);
            ps.setInt(1, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isRoomAvailable(int roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(CHECK_AVAILABILITY);
            ps.setInt(1, roomId);
            // SQL: check_in_date < ? AND check_out_date > ?
            // Overlap check: existing.checkIn < new.checkOut AND existing.checkOut > new.checkIn
            ps.setDate(2, java.sql.Date.valueOf(checkOutDate));  // check_in_date < new checkOut
            ps.setDate(3, java.sql.Date.valueOf(checkInDate));   // check_out_date > new checkIn
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isRoomAvailable(int roomId, LocalDate checkInDate, LocalDate checkOutDate, int bookingId) {
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(CHECK_AVAILABILITY_EXCLUDE);
            ps.setInt(1, roomId);
            // SQL: check_in_date < ? AND check_out_date > ? AND booking_id != ?
            ps.setDate(2, java.sql.Date.valueOf(checkOutDate));  // check_in_date < new checkOut
            ps.setDate(3, java.sql.Date.valueOf(checkInDate));   // check_out_date > new checkIn
            ps.setInt(4, bookingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
