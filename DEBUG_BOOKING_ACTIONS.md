# Debug Guide - Booking Actions Not Working

## 🐛 Issue Fixed: Date Parameter Order

### Problem
The `isRoomAvailable()` method was passing dates in wrong order to SQL query.

### SQL Overlap Logic
```sql
-- To find overlapping bookings:
WHERE check_in_date < ? AND check_out_date > ?
```

**Explanation:**
- `check_in_date < NEW_CHECK_OUT` → Existing booking starts before new booking ends
- `check_out_date > NEW_CHECK_IN` → Existing booking ends after new booking starts

**Examples:**
```
Existing: [Dec 20 --- Dec 25]
New:         [Dec 23 -------- Dec 28]
              ↑
         Overlap happens because:
         - Existing check_in (20) < New check_out (28) ✓
         - Existing check_out (25) > New check_in (23) ✓
```

### What Was Wrong
```java
// ❌ BEFORE (WRONG)
ps.setDate(2, java.sql.Date.valueOf(checkInDate));   // Wrong position!
ps.setDate(3, java.sql.Date.valueOf(checkOutDate));  // Wrong position!
```

This was comparing:
- `existing.check_in_date < NEW_CHECK_IN` (wrong!)
- `existing.check_out_date > NEW_CHECK_OUT` (wrong!)

### What Is Fixed Now
```java
// ✅ AFTER (CORRECT)
ps.setDate(2, java.sql.Date.valueOf(checkOutDate));  // check_in_date < new checkOut
ps.setDate(3, java.sql.Date.valueOf(checkInDate));   // check_out_date > new checkIn
```

---

## 🧪 How to Test the Fix

### Test 1: Approve Should Work Now

```sql
-- Setup: Create a PENDING booking
INSERT INTO booking (guest_name, guest_email, room_id, check_in_date, check_out_date, total_price, status)
VALUES ('Test User', 'test@email.com', 1, '2024-12-20', '2024-12-23', 6000000, 'PENDING');
```

**Steps:**
1. Redeploy application
2. Go to `/admin/bookings`
3. Click Approve on the PENDING booking

**Expected:** ✅ Success! Status changes to CONFIRMED

---

### Test 2: Overlap Detection Should Work

```sql
-- Setup: Two bookings for same room with overlap
INSERT INTO booking (guest_name, guest_email, room_id, check_in_date, check_out_date, total_price, status)
VALUES 
('User A', 'a@test.com', 1, '2024-12-20', '2024-12-25', 5000000, 'PENDING'),
('User B', 'b@test.com', 1, '2024-12-22', '2024-12-27', 5000000, 'CONFIRMED');
```

**Steps:**
1. Try to approve User A's booking

**Expected:** ❌ Error - Room not available (because User B overlaps)

---

### Test 3: Non-Overlapping Should Work

```sql
-- Setup: Two bookings for same room WITHOUT overlap
DELETE FROM booking WHERE room_id = 1;

INSERT INTO booking (guest_name, guest_email, room_id, check_in_date, check_out_date, total_price, status)
VALUES 
('User A', 'a@test.com', 1, '2024-12-20', '2024-12-23', 3000000, 'PENDING'),
('User B', 'b@test.com', 1, '2024-12-25', '2024-12-28', 3000000, 'CONFIRMED');
```

**Steps:**
1. Try to approve User A's booking

**Expected:** ✅ Success! No overlap (23 < 25)

---

## 🔍 Additional Debugging

If still not working, add these debug statements:

### In approveBooking():

```java
@Override
public boolean approveBooking(int bookingId) {
    Connection connection = null;
    try {
        connection = BaseRepository.getConnection();
        connection.setAutoCommit(false);
        
        Booking booking = bookingRepository.getById(bookingId);
        System.out.println("DEBUG: Booking found: " + (booking != null));
        if (booking == null) {
            System.out.println("DEBUG: Booking is null!");
            connection.rollback();
            return false;
        }
        
        System.out.println("DEBUG: Booking status: " + booking.getStatus());
        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            System.out.println("DEBUG: Status is not PENDING!");
            connection.rollback();
            return false;
        }
        
        System.out.println("DEBUG: Checking availability for room " + booking.getRoomId());
        boolean available = bookingRepository.isRoomAvailable(
            booking.getRoomId(),
            booking.getCheckInDate(),
            booking.getCheckOutDate(),
            bookingId
        );
        
        System.out.println("DEBUG: Room available: " + available);
        if (!available) {
            System.out.println("DEBUG: Room NOT available!");
            connection.rollback();
            return false;
        }
        
        System.out.println("DEBUG: Updating status to CONFIRMED");
        boolean updated = bookingRepository.updateStatus(bookingId, Booking.BookingStatus.CONFIRMED);
        System.out.println("DEBUG: Update result: " + updated);
        
        if (!updated) {
            connection.rollback();
            return false;
        }
        
        connection.commit();
        System.out.println("DEBUG: Transaction committed successfully!");
        return true;
        
    } catch (Exception e) {
        System.out.println("DEBUG: Exception occurred: " + e.getMessage());
        e.printStackTrace();
        // ... rest of error handling
    }
}
```

### Check Console Output

When you click Approve, check Tomcat console for output like:
```
DEBUG: Booking found: true
DEBUG: Booking status: PENDING
DEBUG: Checking availability for room 1
DEBUG: Room available: true
DEBUG: Updating status to CONFIRMED
DEBUG: Update result: true
DEBUG: Transaction committed successfully!
```

If you see `Room available: false`, the overlap detection is working correctly.

---

## 🛠️ Other Potential Issues

### Issue 1: Enum Comparison Problem

If debugging shows status is not PENDING even though it is in DB:

**Check:** Database stores string "PENDING" vs Java enum
```java
// Make sure database has exact match
SELECT * FROM booking WHERE status = 'PENDING';  -- Not 'Pending' or 'pending'
```

### Issue 2: updateStatus Not Working

Add debug in Repository:
```java
public boolean updateStatus(int bookingId, Booking.BookingStatus status) {
    try {
        PreparedStatement ps = BaseRepository.getConnection().prepareStatement(UPDATE_STATUS);
        ps.setString(1, status.toString());
        ps.setInt(2, bookingId);
        
        System.out.println("DEBUG updateStatus: " + status + " for booking " + bookingId);
        int rows = ps.executeUpdate();
        System.out.println("DEBUG rows affected: " + rows);
        
        return rows > 0;
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}
```

### Issue 3: Transaction Issues

Test without transaction first:
```java
// Simplified version to test
public boolean simpleApprove(int bookingId) {
    Booking booking = bookingRepository.getById(bookingId);
    if (booking == null || booking.getStatus() != BookingStatus.PENDING) {
        return false;
    }
    return bookingRepository.updateStatus(bookingId, BookingStatus.CONFIRMED);
}
```

---

## ✅ Verification Checklist

After fix, verify:
- [ ] Approve PENDING booking → Success
- [ ] Approve CONFIRMED booking → Fails (status check)
- [ ] Approve with overlapping dates → Fails (availability check)
- [ ] Approve without overlap → Success
- [ ] Cancel PENDING → Success
- [ ] Cancel CONFIRMED → Success
- [ ] Complete CONFIRMED → Success
- [ ] Console shows no SQL errors
- [ ] Success/error messages display correctly

---

## 📝 Summary

**Root cause:** Date parameters passed in wrong order to SQL
**Fix:** Swapped `checkInDate` and `checkOutDate` in PreparedStatement setters
**Impact:** Now correctly detects overlaps and approves valid bookings

Redeploy and test now! 🚀
