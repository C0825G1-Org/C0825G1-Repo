package com.codegym.homestay_booking.service;

import com.codegym.homestay_booking.entity.Booking;
import com.codegym.homestay_booking.entity.Room;
import com.codegym.homestay_booking.repository.BaseRepository;
import com.codegym.homestay_booking.repository.BookingRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class BookingService implements IBookingService {
    private BookingRepository bookingRepository = new BookingRepository();

    @Override
    public List<Booking> getByStatus(Booking.BookingStatus status) {
        return bookingRepository.getByStatus(status);
    }

    @Override
    public List<Booking> getByGuestEmail(String email) {
        return bookingRepository.getByGuestEmail(email);
    }

    /**
     * Update booking by customer with strict validation
     * Rules enforced:
     * - Ownership check
     * - Status check (PENDING/CONFIRMED only)
     * - Time window check (>1 day for CONFIRMED)
     * - Date validation
     * - Availability re-check
     * - Auto price recalculation
     */
    public boolean updateBookingByCustomer(
            int bookingId,
            String guestEmail,
            LocalDate newCheckIn,
            LocalDate newCheckOut) {

        Connection connection = null;
        try {
            connection = BaseRepository.getConnection();
            connection.setAutoCommit(false);

            // 1. Load existing booking
            Booking existingBooking = bookingRepository.getById(bookingId);
            if (existingBooking == null) {
                connection.rollback();
                return false;
            }

            // 2. Ownership check
            if (!existingBooking.getGuestEmail().equals(guestEmail)) {
                connection.rollback();
                return false;
            }

            // 3. Check if booking can be edited
            if (!existingBooking.canBeEdited()) {
                connection.rollback();
                return false;
            }

            // 4. Validate field-level permissions
            if (existingBooking.getStatus() == Booking.BookingStatus.CONFIRMED) {
                // CONFIRMED: can only change check-out, not check-in
                if (!newCheckIn.equals(existingBooking.getCheckInDate())) {
                    connection.rollback();
                    return false;
                }
            }

            // 5. Validate dates
            LocalDate today = LocalDate.now();
            if (newCheckIn.isBefore(today) || !newCheckOut.isAfter(newCheckIn)) {
                connection.rollback();
                return false;
            }

            // 6. Check room availability (excluding this booking)
            boolean available = bookingRepository.isRoomAvailable(
                    existingBooking.getRoomId(),
                    newCheckIn,
                    newCheckOut,
                    bookingId
            );

            if (!available) {
                connection.rollback();
                return false;
            }

            // 7. Recalculate price
            Room room = new com.codegym.homestay_booking.repository.RoomRepository()
                    .getById(existingBooking.getRoomId());
            long nights = java.time.temporal.ChronoUnit.DAYS.between(newCheckIn, newCheckOut);
            float newTotalPrice = room.getRoomPrice() * nights;

            // 8. Update booking
            Booking updatedBooking = new Booking();
            updatedBooking.setBookingId(bookingId);
            updatedBooking.setGuestName(existingBooking.getGuestName());
            updatedBooking.setGuestEmail(existingBooking.getGuestEmail());
            updatedBooking.setRoomId(existingBooking.getRoomId());
            updatedBooking.setCheckInDate(newCheckIn);
            updatedBooking.setCheckOutDate(newCheckOut);
            updatedBooking.setTotalPrice(newTotalPrice);
            updatedBooking.setStatus(existingBooking.getStatus());

            boolean updated = bookingRepository.update(updatedBooking, connection);
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

    public boolean changeRoom(int bookingId, int newRoomId, String guestEmail) {
        Connection connection = null;
        try {
            connection = BaseRepository.getConnection();
            connection.setAutoCommit(false);

            Booking booking = bookingRepository.getById(bookingId);
            if (booking == null || !booking.getGuestEmail().equals(guestEmail)) {
                connection.rollback();
                return false;
            }

            if (!booking.canChangeRoom() || booking.getRoomId() == newRoomId) {
                connection.rollback();
                return false;
            }

            if (!bookingRepository.isRoomAvailable(newRoomId, booking.getCheckInDate(),
                    booking.getCheckOutDate(), bookingId)) {
                connection.rollback();
                return false;
            }

            Room newRoom = new com.codegym.homestay_booking.repository.RoomRepository().getById(newRoomId);
            if (newRoom == null) {
                connection.rollback();
                return false;
            }

            long nights = java.time.temporal.ChronoUnit.DAYS.between(
                    booking.getCheckInDate(), booking.getCheckOutDate());
            float newTotalPrice = newRoom.getRoomPrice() * nights;

            Booking updated = new Booking();
            updated.setBookingId(bookingId);
            updated.setGuestName(booking.getGuestName());
            updated.setGuestEmail(booking.getGuestEmail());
            updated.setRoomId(newRoomId);
            updated.setCheckInDate(booking.getCheckInDate());
            updated.setCheckOutDate(booking.getCheckOutDate());
            updated.setTotalPrice(newTotalPrice);
            updated.setStatus(booking.getStatus());

            if (!bookingRepository.update(updated, connection)) {
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

            // Calculate total price
            Room room = new com.codegym.homestay_booking.repository.RoomRepository().getById(booking.getRoomId());
            if (room == null) {
                connection.rollback();
                return false;
            }

            long nights = java.time.temporal.ChronoUnit.DAYS.between(
                    booking.getCheckInDate(),
                    booking.getCheckOutDate()
            );
            float totalPrice = room.getRoomPrice() * nights;
            booking.setTotalPrice(totalPrice);

            // Check room availability
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
            } catch (SQLException e) {
            }
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

    /**
     * Request cancellation (for customers)
     * Changes status to CANCELLED_REQUEST
     * Admin must approve to change to CANCELLED
     */
    public boolean requestCancellation(int bookingId) {
        try {
            Booking booking = bookingRepository.getById(bookingId);
            if (booking == null) {
                return false;
            }

            // Only PENDING or CONFIRMED can request cancellation
            if (booking.getStatus() != Booking.BookingStatus.PENDING &&
                    booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
                return false;
            }

            return bookingRepository.updateStatus(bookingId, Booking.BookingStatus.CANCELLED_REQUEST);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Approve cancel request (for admin)
     * Changes CANCELLED_REQUEST → CANCELLED
     */
    public boolean approveCancelRequest(int bookingId) {
        try {
            Booking booking = bookingRepository.getById(bookingId);
            if (booking == null) {
                return false;
            }

            // Only CANCELLED_REQUEST can be approved
            if (booking.getStatus() != Booking.BookingStatus.CANCELLED_REQUEST) {
                return false;
            }

            return bookingRepository.updateStatus(bookingId, Booking.BookingStatus.CANCELLED);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reject cancel request (for admin)
     * Returns booking to PENDING status
     */
    public boolean rejectCancelRequest(int bookingId) {
        try {
            Booking booking = bookingRepository.getById(bookingId);
            if (booking == null) {
                return false;
            }

            // Only CANCELLED_REQUEST can be rejected
            if (booking.getStatus() != Booking.BookingStatus.CANCELLED_REQUEST) {
                return false;
            }

            // Return to PENDING status when rejected
            return bookingRepository.updateStatus(bookingId, Booking.BookingStatus.PENDING);

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
        return updateBooking(entity);
    }

    /**
     * Update booking with comprehensive validation
     * Business rules:
     * - COMPLETED/CANCELLED: Cannot update
     * - PENDING: Full update allowed
     * - CONFIRMED: Update only if check-in is in future + room availability check
     */
    public boolean updateBooking(Booking updatedBooking) {
        Connection connection = null;
        try {
            connection = BaseRepository.getConnection();
            connection.setAutoCommit(false);

            // 1. Get existing booking
            Booking existingBooking = bookingRepository.getById(updatedBooking.getBookingId());
            if (existingBooking == null) {
                connection.rollback();
                return false;
            }

            // 2. Check if booking can be edited (business rules)
            if (!existingBooking.canBeEdited()) {
                connection.rollback();
                return false;
            }

            // 3. Validate dates
            if (updatedBooking.getCheckOutDate().isBefore(updatedBooking.getCheckInDate()) ||
                    updatedBooking.getCheckOutDate().isEqual(updatedBooking.getCheckInDate())) {
                connection.rollback();
                return false;
            }

            // 4. Validate not in past
            LocalDate today = LocalDate.now();
            if (updatedBooking.getCheckInDate().isBefore(today)) {
                connection.rollback();
                return false;
            }

            // 5. If room changed, check availability
            boolean roomChanged = existingBooking.getRoomId() != updatedBooking.getRoomId();
            boolean datesChanged = !existingBooking.getCheckInDate().equals(updatedBooking.getCheckInDate()) ||
                    !existingBooking.getCheckOutDate().equals(updatedBooking.getCheckOutDate());

            if (roomChanged || datesChanged) {
                // Check if new room + dates are available (excluding this booking)
                boolean available = bookingRepository.isRoomAvailable(
                        updatedBooking.getRoomId(),
                        updatedBooking.getCheckInDate(),
                        updatedBooking.getCheckOutDate(),
                        updatedBooking.getBookingId()
                );

                if (!available) {
                    connection.rollback();
                    return false;
                }
            }

            // 6. Update booking
            boolean updated = bookingRepository.update(updatedBooking, connection);
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
}
