package com.codegym.homestay_booking.repository;

import com.codegym.homestay_booking.entity.Booking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public Booking createBooking(Booking booking) {
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(INSERT_BOOKING);
            ps.setString(1, booking.getGuestName());
            ps.setString(2, booking.getGuestEmail());
            ps.setInt(3, booking.getRoomId());
            ps.setDate(4, java.sql.Date.valueOf(booking.getCheckInDate()));
            ps.setDate(5, java.sql.Date.valueOf(booking.getCheckOutDate()));
            ps.setFloat(6, booking.getTotalPrice());
            ps.setString(7, booking.getStatus().toString());
            ps.executeUpdate();
            return booking;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
