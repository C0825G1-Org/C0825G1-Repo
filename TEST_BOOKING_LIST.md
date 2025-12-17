# Test Guide - Booking List Feature

## ✅ Đã hoàn thành

### 1. **BookingController Servlet**
- **Path:** `/admin/bookings`
- **Method:** `doGet()`
- **Chức năng:** Lấy tất cả bookings từ `BookingService.getAll()`

### 2. **booking-list.jsp**
- **Location:** `/WEB-INF/views/admin/booking_management/booking-list.jsp`
- **Features:**
  - Statistics cards (Pending, Confirmed, Cancelled, Completed)
  - Filterable table by status
  - Search by guest email/name
  - Action buttons (Approve, Cancel, Complete)
  - Responsive design với Bootstrap 5

---

## 🧪 Cách Test

### **Bước 1: Deploy Application**

```bash
# Nếu dùng IntelliJ IDEA
1. Run → Edit Configurations
2. Add Tomcat Server (Local)
3. Deploy artifact
4. Start server

# Nếu dùng Gradle
./gradlew war
# Deploy file .war vào Tomcat webapps
```

### **Bước 2: Truy cập URL**

```
http://localhost:8080/homestay_booking/admin/bookings
```

**Expected result:**
- Hiển thị trang Booking Management
- 4 statistics cards ở trên
- Table với danh sách bookings

---

## 📊 Test Cases

### **Test 1: Hiển thị danh sách bookings**
✅ **Expected:**
- Table hiển thị tất cả bookings từ database
- Mỗi row có: ID, Guest Name, Email, Room ID, Dates, Nights, Price, Status, Actions

### **Test 2: Statistics Cards**
✅ **Expected:**
- Card "Pending" đếm đúng số booking với status PENDING
- Card "Confirmed" đếm số booking CONFIRMED
- Card "Cancelled" đếm số CANCELLED
- Card "Completed" đếm số COMPLETED

### **Test 3: Filter by Status**
✅ **Steps:**
1. Click dropdown "All Status"
2. Chọn "Pending"

**Expected:** Chỉ hiển thị bookings với status PENDING

### **Test 4: Search by Email**
✅ **Steps:**
1. Nhập email vào search box
2. Ví dụ: "john@example.com"

**Expected:** Filter bookings có email chứa "john"

### **Test 5: Status Badges**
✅ **Expected:**
- PENDING: Badge màu vàng (warning)
- CONFIRMED: Badge màu xanh lá (success)
- CANCELLED: Badge màu đỏ (danger)
- COMPLETED: Badge màu xanh dương (info)

### **Test 6: Action Buttons**
✅ **Expected:**
- PENDING booking: Hiển thị Approve + Cancel buttons
- CONFIRMED booking: Hiển thị Cancel + Complete buttons
- CANCELLED booking: Không có action buttons
- COMPLETED booking: Không có action buttons

---

## 🐛 Potential Issues & Solutions

### **Issue 1: Empty List**
**Symptom:** "No bookings found in the system"

**Solution:** Chèn data test vào database
```sql
INSERT INTO booking (guest_name, guest_email, room_id, check_in_date, check_out_date, total_price, status)
VALUES 
('John Doe', 'john@example.com', 1, '2024-12-20', '2024-12-23', 6000000, 'PENDING'),
('Jane Smith', 'jane@example.com', 2, '2024-12-25', '2024-12-28', 6000000, 'CONFIRMED'),
('Bob Wilson', 'bob@example.com', 3, '2024-12-15', '2024-12-18', 6000000, 'COMPLETED');
```

### **Issue 2: 404 Error**
**Symptom:** Page not found

**Possible causes:**
- Context path sai
- Servlet mapping sai
- Application chưa deploy

**Solution:**
- Check context path trong Tomcat configuration
- Verify `@WebServlet("/admin/bookings")` annotation
- Redeploy application

### **Issue 3: SQLException**
**Symptom:** Error loading bookings

**Possible causes:**
- Database connection failed
- Table không tồn tại
- `BaseRepository` config sai

**Solution:**
- Check `BaseRepository` database credentials
- Run database creation script
- Check MySQL service đang chạy

### **Issue 4: Date Display Issues**
**Symptom:** Dates không hiển thị hoặc format sai

**Note:** LocalDate sẽ hiển thị format ISO (yyyy-MM-dd) mặc định. Nếu muốn custom format, cần thêm method trong Booking entity:

```java
public String getFormattedCheckIn() {
    return checkInDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
}
```

---

## 📸 Screenshots to Verify

1. **Full page view** - Statistics cards + table
2. **Filter dropdown** - Test filter by status
3. **Search function** - Test search by email
4. **Action buttons** - Verify correct buttons show for each status
5. **Empty state** - "No bookings found" message

---

## 🔄 Next Steps (Not implemented yet)

- [ ] Implement **Approve Booking** functionality (doPost in controller)
- [ ] Implement **Cancel Booking** functionality
- [ ] Implement **Complete Booking** functionality
- [ ] Add **View Details** page
- [ ] Add **Create New Booking** form
- [ ] Add pagination (if needed for large datasets)

---

## 💡 Notes

**Lint Warnings:**
- JSP có một số lint warnings về JSP EL trong JavaScript
- Đây là false positives, không ảnh hưởng functionality
- Code sẽ chạy bình thường

**Performance:**
- `getAll()` load tất cả bookings vào memory
- OK cho demo/small dataset
- Với production, nên implement pagination
