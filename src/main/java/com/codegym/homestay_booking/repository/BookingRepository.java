package com.codegym.homestay_booking.repository;

import com.codegym.homestay_booking.entity.Booking;
import com.codegym.homestay_booking.dto.RevenueData;
import com.codegym.homestay_booking.dto.StatusCount;
import com.codegym.homestay_booking.dto.MonthlyBooking;
import com.codegym.homestay_booking.dto.RoomBookingCount;

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
    
    private static final String SELECT_ALL_PAGINATED =
            "SELECT * FROM booking ORDER BY booking_id DESC LIMIT ? OFFSET ?";
    
    private static final String COUNT_ALL =
            "SELECT COUNT(*) FROM booking";
    
    private static final String SEARCH_PAGINATED =
            "SELECT * FROM booking WHERE (guest_name LIKE ? OR guest_email LIKE ?) ORDER BY booking_id DESC LIMIT ? OFFSET ?";
    
    private static final String COUNT_SEARCH =
            "SELECT COUNT(*) FROM booking WHERE (guest_name LIKE ? OR guest_email LIKE ?)";
    
    private static final String FILTER_BY_STATUS_PAGINATED =
            "SELECT * FROM booking WHERE status = ? ORDER BY booking_id DESC LIMIT ? OFFSET ?";
    
    private static final String COUNT_BY_STATUS_FILTER =
            "SELECT COUNT(*) FROM booking WHERE status = ?";
    
    private static final String SEARCH_WITH_STATUS_PAGINATED =
            "SELECT * FROM booking WHERE status = ? AND (guest_name LIKE ? OR guest_email LIKE ?) ORDER BY booking_id DESC LIMIT ? OFFSET ?";
    
    private static final String COUNT_SEARCH_WITH_STATUS =
            "SELECT COUNT(*) FROM booking WHERE status = ? AND (guest_name LIKE ? OR guest_email LIKE ?)";

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
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(
                INSERT_BOOKING, java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, booking.getGuestName());
            ps.setString(2, booking.getGuestEmail());
            ps.setInt(3, booking.getRoomId());
            ps.setDate(4, java.sql.Date.valueOf(booking.getCheckInDate()));
            ps.setDate(5, java.sql.Date.valueOf(booking.getCheckOutDate()));
            ps.setFloat(6, booking.getTotalPrice());
            ps.setString(7, booking.getStatus().toString());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    booking.setBookingId(rs.getInt(1));
                }
                return true;
            }
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
    
    public List<Booking> getPaginated(int page, int pageSize) {
        List<Booking> bookings = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(SELECT_ALL_PAGINATED);
            ps.setInt(1, pageSize);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    public int getTotalCount() {
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(COUNT_ALL);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Flexible filter method supporting all combinations of filters
     */
    public List<Booking> filterBookings(String status, String search, 
            LocalDate checkInFrom, LocalDate checkInTo,
            LocalDate checkOutFrom, LocalDate checkOutTo,
            Integer roomId,
            int page, int pageSize) {
        
        List<Booking> bookings = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM booking WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        // Status filter
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        
        // Search filter (name or email)
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (guest_name LIKE ? OR guest_email LIKE ?)");
            String searchPattern = "%" + search + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        // Room filter
        if (roomId != null) {
            sql.append(" AND room_id = ?");
            params.add(roomId);
        }
        
        // Check-in date range
        if (checkInFrom != null) {
            sql.append(" AND check_in_date >= ?");
            params.add(java.sql.Date.valueOf(checkInFrom));
        }
        if (checkInTo != null) {
            sql.append(" AND check_in_date <= ?");
            params.add(java.sql.Date.valueOf(checkInTo));
        }
        
        // Check-out date range
        if (checkOutFrom != null) {
            sql.append(" AND check_out_date >= ?");
            params.add(java.sql.Date.valueOf(checkOutFrom));
        }
        if (checkOutTo != null) {
            sql.append(" AND check_out_date <= ?");
            params.add(java.sql.Date.valueOf(checkOutTo));
        }
        
        sql.append(" ORDER BY booking_id DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);
        
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    /**
     * Count filtered bookings
     */
    public int countFilteredBookings(String status, String search,
            LocalDate checkInFrom, LocalDate checkInTo,
            LocalDate checkOutFrom, LocalDate checkOutTo,
            Integer roomId) {
        
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM booking WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (guest_name LIKE ? OR guest_email LIKE ?)");
            String searchPattern = "%" + search + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (roomId != null) {
            sql.append(" AND room_id = ?");
            params.add(roomId);
        }
        
        if (checkInFrom != null) {
            sql.append(" AND check_in_date >= ?");
            params.add(java.sql.Date.valueOf(checkInFrom));
        }
        if (checkInTo != null) {
            sql.append(" AND check_in_date <= ?");
            params.add(java.sql.Date.valueOf(checkInTo));
        }
        
        if (checkOutFrom != null) {
            sql.append(" AND check_out_date >= ?");
            params.add(java.sql.Date.valueOf(checkOutFrom));
        }
        if (checkOutTo != null) {
            sql.append(" AND check_out_date <= ?");
            params.add(java.sql.Date.valueOf(checkOutTo));
        }
        
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Search with pagination
    public List<Booking> searchPaginated(String keyword, int page, int pageSize) {
        List<Booking> bookings = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        String searchPattern = "%" + keyword + "%";
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(SEARCH_PAGINATED);
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setInt(3, pageSize);
            ps.setInt(4, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    public int countSearch(String keyword) {
        String searchPattern = "%" + keyword + "%";
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(COUNT_SEARCH);
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Filter by status with pagination
    public List<Booking> filterByStatusPaginated(Booking.BookingStatus status, int page, int pageSize) {
        List<Booking> bookings = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(FILTER_BY_STATUS_PAGINATED);
            ps.setString(1, status.toString());
            ps.setInt(2, pageSize);
            ps.setInt(3, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    public int countByStatusFilter(Booking.BookingStatus status) {
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(COUNT_BY_STATUS_FILTER);
            ps.setString(1, status.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Search with status filter and pagination
    public List<Booking> searchWithStatusPaginated(Booking.BookingStatus status, String keyword, int page, int pageSize) {
        List<Booking> bookings = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        String searchPattern = "%" + keyword + "%";
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(SEARCH_WITH_STATUS_PAGINATED);
            ps.setString(1, status.toString());
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setInt(4, pageSize);
            ps.setInt(5, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    public int countSearchWithStatus(Booking.BookingStatus status, String keyword) {
        String searchPattern = "%" + keyword + "%";
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(COUNT_SEARCH_WITH_STATUS);
            ps.setString(1, status.toString());
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
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
    
    /**
     * Update booking information (simple version without transaction)
     */
    public boolean update(Booking booking) {
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(UPDATE_BOOKING);
            ps.setString(1, booking.getGuestName());
            ps.setString(2, booking.getGuestEmail());
            ps.setInt(3, booking.getRoomId());
            ps.setDate(4, java.sql.Date.valueOf(booking.getCheckInDate()));
            ps.setDate(5, java.sql.Date.valueOf(booking.getCheckOutDate()));
            ps.setFloat(6, booking.getTotalPrice());
            ps.setString(7, booking.getStatus().toString());
            ps.setInt(8, booking.getBookingId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Update booking with transaction support (for concurrent edits)
     */
    public boolean update(Booking booking, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(UPDATE_BOOKING);
        ps.setString(1, booking.getGuestName());
        ps.setString(2, booking.getGuestEmail());
        ps.setInt(3, booking.getRoomId());
        ps.setDate(4, java.sql.Date.valueOf(booking.getCheckInDate()));
        ps.setDate(5, java.sql.Date.valueOf(booking.getCheckOutDate()));
        ps.setFloat(6, booking.getTotalPrice());
        ps.setString(7, booking.getStatus().toString());
        ps.setInt(8, booking.getBookingId());
        
        return ps.executeUpdate() > 0;
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
    
    /**
     * Get all bookings for a guest by email
     * For customer booking lookup
     */
    public List<Booking> getByGuestEmail(String email) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM booking WHERE guest_email = ? ORDER BY booking_id DESC";
        try {
            PreparedStatement ps = BaseRepository.getConnection().prepareStatement(sql);
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
    
    // Dashboard Statistics Methods
    
    public int countAllBookings() {
        String sql = "SELECT COUNT(*) FROM booking";
        try (PreparedStatement ps = BaseRepository.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int countByStatus(Booking.BookingStatus status) {
        String sql = "SELECT COUNT(*) FROM booking WHERE status = ?";
        try (PreparedStatement ps = BaseRepository.getConnection().prepareStatement(sql)) {
            ps.setString(1, status.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public double sumTotalRevenue() {
        String sql = "SELECT SUM(total_price) FROM booking WHERE status IN ('CONFIRMED', 'COMPLETED')";
        try (PreparedStatement ps = BaseRepository.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public double sumRevenueByMonth(int month, int year) {
        String sql = "SELECT SUM(total_price) FROM booking " +
                    "WHERE status IN ('CONFIRMED', 'COMPLETED') " +
                    "AND MONTH(check_in_date) = ? AND YEAR(check_in_date) = ?";
        try (PreparedStatement ps = BaseRepository.getConnection().prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public List<Booking> getRecentBookings(int limit) {
        String sql = "SELECT * FROM booking ORDER BY booking_id DESC LIMIT ?";
        List<Booking> bookings = new ArrayList<>();
        try (PreparedStatement ps = BaseRepository.getConnection().prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    public List<RevenueData> getRevenueTrend(int days) {
        String sql = "SELECT DATE(check_in_date) AS booking_date, SUM(total_price) AS daily_revenue " +
                    "FROM booking " +
                    "WHERE status IN ('CONFIRMED', 'COMPLETED') " +
                    "AND check_in_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY) " +
                    "GROUP BY DATE(check_in_date) " +
                    "ORDER BY booking_date ASC";
        List<RevenueData> revenueData = new ArrayList<>();
        try (PreparedStatement ps = BaseRepository.getConnection().prepareStatement(sql)) {
            ps.setInt(1, days);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RevenueData data = new RevenueData();
                data.setDate(rs.getString("booking_date"));
                data.setRevenue(rs.getDouble("daily_revenue"));
                revenueData.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return revenueData;
    }
    
    public List<StatusCount> getStatusDistribution() {
        String sql = "SELECT status, COUNT(*) AS total FROM booking GROUP BY status";
        List<StatusCount> statusCounts = new ArrayList<>();
        try (PreparedStatement ps = BaseRepository.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                StatusCount sc = new StatusCount();
                sc.setStatus(rs.getString("status"));
                sc.setCount(rs.getInt("total"));
                statusCounts.add(sc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statusCounts;
    }
    
    public List<MonthlyBooking> getMonthlyBookings() {
        String sql = "SELECT MONTH(check_in_date) AS month, COUNT(*) AS total " +
                    "FROM booking " +
                    "WHERE YEAR(check_in_date) = YEAR(CURDATE()) " +
                    "GROUP BY MONTH(check_in_date) ORDER BY month";
        List<MonthlyBooking> monthlyBookings = new ArrayList<>();
        try (PreparedStatement ps = BaseRepository.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MonthlyBooking mb = new MonthlyBooking();
                mb.setMonth(rs.getInt("month"));
                mb.setCount(rs.getInt("total"));
                monthlyBookings.add(mb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return monthlyBookings;
    }
    
    public List<RoomBookingCount> getTopRooms(int limit) {
        String sql = "SELECT room_id, COUNT(*) AS total_bookings " +
                    "FROM booking " +
                    "WHERE status IN ('CONFIRMED', 'COMPLETED') " +
                    "GROUP BY room_id " +
                    "ORDER BY total_bookings DESC LIMIT ?";
        List<RoomBookingCount> topRooms = new ArrayList<>();
        try (PreparedStatement ps = BaseRepository.getConnection().prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RoomBookingCount rbc = new RoomBookingCount();
                rbc.setRoomId(rs.getInt("room_id"));
                rbc.setBookingCount(rs.getInt("total_bookings"));
                topRooms.add(rbc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topRooms;
    }
    
    public double getAverageStayDuration() {
        String sql = "SELECT AVG(DATEDIFF(check_out_date, check_in_date)) AS avg_nights " +
                    "FROM booking " +
                    "WHERE status IN ('CONFIRMED', 'COMPLETED')";
        try (PreparedStatement ps = BaseRepository.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("avg_nights");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
