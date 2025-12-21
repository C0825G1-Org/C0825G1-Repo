package com.codegym.homestay_booking.repository;

import com.codegym.homestay_booking.entity.Payment;
import com.codegym.homestay_booking.entity.Payment.PaymentStatus;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PaymentRepository {
    
    public Payment insert(Payment payment) {
        String sql = "INSERT INTO payment (transaction_code, vnp_txn_ref, amount, status, " +
                     "guest_name, guest_email, room_id, check_in_date, check_out_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = BaseRepository.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, payment.getTransactionCode());
            ps.setString(2, payment.getVnpTxnRef());
            ps.setFloat(3, payment.getAmount());
            ps.setString(4, payment.getStatus().name());
            ps.setString(5, payment.getGuestName());
            ps.setString(6, payment.getGuestEmail());
            ps.setInt(7, payment.getRoomId());
            ps.setDate(8, Date.valueOf(payment.getCheckInDate()));
            ps.setDate(9, Date.valueOf(payment.getCheckOutDate()));
            
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    payment.setPaymentId(rs.getInt(1));
                }
                return payment;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Payment getByTransactionCode(String transactionCode) {
        String sql = "SELECT * FROM payment WHERE transaction_code = ?";
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(sql);
            ps.setString(1, transactionCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Payment getByVnpTxnRef(String vnpTxnRef) {
        String sql = "SELECT * FROM payment WHERE vnp_txn_ref = ?";
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(sql);
            ps.setString(1, vnpTxnRef);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean updateStatus(int paymentId, PaymentStatus status, String responseCode, 
                                String transactionNo, Integer bookingId) {
        String sql = "UPDATE payment SET status = ?, vnp_response_code = ?, " +
                     "vnp_transaction_no = ?, booking_id = ?, paid_at = ? WHERE payment_id = ?";
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(sql);
            ps.setString(1, status.name());
            ps.setString(2, responseCode);
            ps.setString(3, transactionNo);
            if (bookingId != null) {
                ps.setInt(4, bookingId);
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setTimestamp(5, status == PaymentStatus.SUCCESS ? Timestamp.valueOf(LocalDateTime.now()) : null);
            ps.setInt(6, paymentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private Payment mapResultSet(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setPaymentId(rs.getInt("payment_id"));
        p.setTransactionCode(rs.getString("transaction_code"));
        p.setVnpTxnRef(rs.getString("vnp_txn_ref"));
        p.setAmount(rs.getFloat("amount"));
        p.setStatus(PaymentStatus.valueOf(rs.getString("status")));
        p.setGuestName(rs.getString("guest_name"));
        p.setGuestEmail(rs.getString("guest_email"));
        p.setRoomId(rs.getInt("room_id"));
        p.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
        p.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
        
        int bookingId = rs.getInt("booking_id");
        if (!rs.wasNull()) {
            p.setBookingId(bookingId);
        }
        
        p.setVnpResponseCode(rs.getString("vnp_response_code"));
        p.setVnpTransactionNo(rs.getString("vnp_transaction_no"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) p.setCreatedAt(createdAt.toLocalDateTime());
        
        Timestamp paidAt = rs.getTimestamp("paid_at");
        if (paidAt != null) p.setPaidAt(paidAt.toLocalDateTime());
        
        return p;
    }
}
