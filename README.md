# 🏠 Homestay Booking System

A comprehensive web-based booking management system for homestay accommodations built with Java Servlet/JSP and MySQL.

![Java](https://img.shields.io/badge/Java-8+-orange?style=flat-square&logo=java)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.0-purple?style=flat-square&logo=bootstrap)
![Tomcat](https://img.shields.io/badge/Tomcat-9.0-yellow?style=flat-square&logo=apache-tomcat)

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Usage](#-usage)
- [API Endpoints](#-api-endpoints)
- [Database Schema](#-database-schema)
- [Business Rules](#-business-rules)

---

## ✨ Features

### Customer Portal
- 🔐 User authentication (Login/Register)
- 🏨 Browse available rooms
- 📅 Book rooms with date selection
- ✏️ Edit bookings (within rules)
- 🔄 Change rooms
- ❌ Request booking cancellation
- 📜 View booking history

### Admin Dashboard
- 📊 Dashboard with analytics & charts
- 🤖 AI-powered business insights (Gemini API)
- 📋 Booking management with advanced filters
- ✅ Approve/Reject bookings
- 🚫 Handle cancellation requests
- 🏠 Room management
- 👥 Customer management

### Technical Features
- 🔒 Transaction management for data integrity
- 🔍 Server-side pagination, filtering & search
- ⚡ AJAX-based room availability check
- 📱 Responsive Bootstrap 5 UI
- 💳 Automatic price calculation

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|------------|
| **Backend** | Java 8+, Servlet 4.0, JSP |
| **Frontend** | HTML5, CSS3, JavaScript, Bootstrap 5 |
| **Database** | MySQL 8.0 |
| **Server** | Apache Tomcat 9.0 |
| **Build Tool** | Gradle |
| **AI Integration** | Google Gemini API |
| **Charts** | Chart.js |

---

## 📁 Project Structure

```
homestay_booking/
├── src/main/java/com/codegym/homestay_booking/
│   ├── controller/
│   │   ├── admin/          # Admin controllers
│   │   │   ├── BookingController.java
│   │   │   ├── DashboardServlet.java
│   │   │   ├── AvailableRoomsServlet.java  # AJAX API
│   │   │   └── ...
│   │   └── customer/       # Customer controllers
│   │       ├── CustomerBookingController.java
│   │       └── ...
│   ├── entity/             # Data models
│   │   ├── Booking.java
│   │   ├── Room.java
│   │   └── User.java
│   ├── repository/         # Database access
│   │   ├── BookingRepository.java
│   │   ├── RoomRepository.java
│   │   └── BaseRepository.java
│   ├── service/            # Business logic
│   │   ├── BookingService.java
│   │   └── AIInsightService.java
│   └── config/             # Configuration
│       └── ConfigLoader.java
├── src/main/webapp/
│   ├── WEB-INF/views/
│   │   ├── admin/          # Admin JSP pages
│   │   └── customer/       # Customer JSP pages
│   └── assets/             # CSS, JS, images
├── build.gradle
└── README.md
```

---

## 🚀 Installation

### Prerequisites
- Java JDK 8 or higher
- Apache Tomcat 9.0
- MySQL 8.0
- Gradle (optional, wrapper included)

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/C0825G1-Org/C0825G1-Repo.git
   cd homestay_booking
   ```

2. **Create MySQL database**
   ```sql
   CREATE DATABASE homestay_booking;
   ```

3. **Import database schema**
   ```bash
   mysql -u root -p homestay_booking < database/schema.sql
   ```

4. **Configure database connection**
   
   Edit `src/main/java/.../repository/BaseRepository.java`:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/homestay_booking";
   private static final String USER = "your_username";
   private static final String PASSWORD = "your_password";
   ```

5. **Build and deploy**
   ```bash
   ./gradlew build
   # Deploy WAR to Tomcat
   ```

6. **Access the application**
   - Customer: `http://localhost:8080/homestay_booking/`
   - Admin: `http://localhost:8080/homestay_booking/admin`

---

## ⚙️ Configuration

### Gemini AI API (Optional)
Create file: `src/main/java/.../config/key`
```
your-gemini-api-key
```

---

## 📖 Usage

### Admin Workflow

1. **Create Booking**
   ```
   Select Check-in → Select Check-out → AJAX checks rooms → Select Room → Submit
   ```

2. **Manage Bookings**
   - Filter by: Status, Room, Date Range, Search
   - Actions: Confirm, Cancel, Complete

### Customer Workflow

1. **Book a Room**
   - Browse rooms → Select dates → Confirm booking

2. **Manage Bookings**
   - Edit dates (PENDING/CONFIRMED)
   - Change room (PENDING only)
   - Request cancellation

---

## 🔌 API Endpoints

### AJAX API

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/admin/api/available-rooms` | GET | Get rooms available for date range |

**Parameters:**
- `checkIn` - Check-in date (YYYY-MM-DD)
- `checkOut` - Check-out date (YYYY-MM-DD)

**Response:**
```json
[
  {"roomId": 1, "roomType": "Family", "roomPrice": 500000, "sleepSlot": 4},
  {"roomId": 2, "roomType": "Business", "roomPrice": 800000, "sleepSlot": 2}
]
```

---

## 🗄 Database Schema

### Tables

```sql
-- Users
CREATE TABLE user (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    full_name VARCHAR(255),
    role ENUM('ADMIN', 'CUSTOMER')
);

-- Rooms
CREATE TABLE room (
    room_id INT PRIMARY KEY AUTO_INCREMENT,
    room_type ENUM('Family', 'Business', 'Honey_Moon'),
    sleep_slot INT,
    room_price DECIMAL(10,2),
    status ENUM('AVAILABLE', 'UNAVAILABLE'),
    image_url VARCHAR(500),
    description TEXT
);

-- Bookings
CREATE TABLE booking (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    room_id INT,
    guest_name VARCHAR(255),
    guest_email VARCHAR(255),
    check_in_date DATE,
    check_out_date DATE,
    total_price DECIMAL(10,2),
    status ENUM('PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED', 'CANCELLED_REQUEST'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES room(room_id)
);
```

---

## 📜 Business Rules

### Booking Status Flow
```
PENDING → CONFIRMED → COMPLETED
    ↓         ↓
CANCELLED ← CANCELLED_REQUEST
```

### Edit Rules

| Status | Can Edit Dates | Can Change Room | Can Cancel |
|--------|---------------|-----------------|------------|
| PENDING | ✅ Both | ✅ Yes | ✅ Direct |
| CONFIRMED | ✅ Check-out only | ❌ No | ⚠️ Request |
| COMPLETED | ❌ No | ❌ No | ❌ No |
| CANCELLED | ❌ No | ❌ No | ❌ No |

### Room Availability
```sql
-- Room is NOT available if exists booking where:
status IN ('PENDING', 'CONFIRMED')
AND check_in_date < new_check_out
AND check_out_date > new_check_in
```

---

## 👥 Contributors

- **C0825G1-Org** - Development Team

---

## 📄 License

This project is developed for educational purposes.

---

<p align="center">
  Made with ❤️ by Tri
</p>