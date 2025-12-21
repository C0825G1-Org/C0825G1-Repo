# Homestay Booking Management System

A comprehensive web-based booking management system for homestay businesses, featuring QR payment integration, automated PDF invoice generation, analytics dashboard, and AI-powered business insights.

---

## Table of Contents

- [Introduction](#introduction)
- [Tech Stack](#tech-stack)
- [System Architecture](#system-architecture)
- [Features](#features)
- [Booking Status Flow](#booking-status-flow)
- [Payment & Invoice](#payment--invoice)
- [Dashboard & Analytics](#dashboard--analytics)
- [AI Insights](#ai-insights)
- [Database Schema](#database-schema)
- [Installation](#installation)
- [Future Roadmap](#future-roadmap)
- [Author](#author)

---

## Introduction

### Problem Statement

Small and medium homestay businesses often struggle with:
- Manual booking management leading to overbooking
- Lack of real-time room availability tracking
- Time-consuming invoice generation
- No data-driven insights for business decisions

### Solution

This system provides a complete digital solution for homestay management:
- **Real-time booking management** with conflict prevention
- **Automated payment processing** via bank QR code
- **Instant PDF invoice generation** sent automatically to customers
- **Analytics dashboard** with business performance metrics
- **AI-powered insights** for strategic decision making

### Target Users

| Role | Capabilities |
|------|--------------|
| **Customer** | Browse rooms, make bookings, pay online, receive invoices |
| **Admin** | Manage rooms, process bookings, view analytics, get AI insights |

---

## Tech Stack

### Backend
| Technology | Purpose |
|------------|---------|
| Java Servlet | Request handling & business logic |
| JSP | Server-side rendering |
| JDBC | Database connectivity |
| MySQL | Data persistence |
| JavaMail API | Email delivery |
| Apache PDFBox | PDF invoice generation |

### Frontend
| Technology | Purpose |
|------------|---------|
| JSP + JSTL | Dynamic page rendering |
| Bootstrap 5 | Responsive UI components |
| Chart.js | Dashboard visualizations |
| HTML/CSS/JavaScript | Client-side interactivity |

### Integrations
| Service | Purpose |
|---------|---------|
| VNPay | QR code payment gateway |
| Gmail SMTP | Transactional emails |
| Google Gemini 2.5 Flash | AI business insights |

---

## System Architecture

### MVC Pattern

```
┌─────────────────────────────────────────────────────────────┐
│                        CLIENT (Browser)                      │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     CONTROLLER LAYER                         │
│  (CustomerRoomController, BookingController, PaymentController)│
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      SERVICE LAYER                           │
│  (BookingService, RoomService, EmailService, VNPayService)   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    REPOSITORY LAYER                          │
│    (BookingRepository, RoomRepository, PaymentRepository)    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      MySQL DATABASE                          │
└─────────────────────────────────────────────────────────────┘
```

### Booking & Payment Flow

```
Customer selects room → Enters details → Initiates payment
                                              │
                                              ▼
                                    VNPay QR generated
                                              │
                                              ▼
                              Customer completes bank transfer
                                              │
                                              ▼
                              VNPay callback → Verify signature
                                              │
                                              ▼
                              Create booking (CONFIRMED)
                                              │
                                              ▼
                              Generate PDF → Send email
```

---

## Features

### Customer Features

| Feature | Description |
|---------|-------------|
| Room Browsing | View all available rooms with images, pricing, and capacity |
| Availability Search | Find rooms available for specific date ranges |
| Online Booking | Book rooms with real-time availability validation |
| QR Payment | Pay via bank transfer using VNPay QR code |
| Email Invoice | Automatically receive PDF invoice after payment |
| Booking Management | View, edit, or request cancellation of bookings |

### Admin Features

| Feature | Description |
|---------|-------------|
| Room CRUD | Create, read, update, delete room listings |
| Image Upload | Upload room photos from local storage |
| Booking Management | View all bookings, update status, manual creation |
| Invoice Generation | Print/download PDF invoices for any booking |
| Dashboard | Visual analytics with charts and KPIs |
| AI Insights | AI-generated business recommendations |

---

## Booking Status Flow

```
                    ┌───────────────┐
                    │    PENDING    │
                    │ (Admin create)│
                    └───────┬───────┘
                            │
              ┌─────────────┴─────────────┐
              │                           │
              ▼                           ▼
    ┌─────────────────┐         ┌─────────────────┐
    │   CONFIRMED     │         │   CANCELLED     │
    │ (Payment done)  │         │ (Rejected/Cancel)│
    └────────┬────────┘         └─────────────────┘
             │
             ▼
    ┌─────────────────┐
    │   COMPLETED     │
    │(After checkout) │
    └─────────────────┘
```

### Status Definitions

| Status | Trigger | Editable |
|--------|---------|----------|
| **PENDING** | Admin creates booking manually | Yes (before check-in) |
| **CONFIRMED** | Customer pays via VNPay | Yes (before check-in) |
| **COMPLETED** | Admin marks after guest checkout | No |
| **CANCELLED** | Admin rejects or cancels | No |

---

## Payment & Invoice

### Payment Methods

| Method | User | Flow |
|--------|------|------|
| VNPay QR | Customer | Scan QR → Pay via bank app → Auto-confirm |
| Manual | Admin | Create booking directly (no payment required) |

### Invoice System

- **Generation**: PDF created using Apache PDFBox
- **Content**: Homestay info, guest details, booking summary, total amount
- **Delivery**: 
  - Customer: Auto-sent via email after successful payment
  - Admin: Download/print from booking management

---

## Dashboard & Analytics

### Available Charts

| Chart | Business Value |
|-------|----------------|
| Revenue Over Time | Track income trends, identify peak periods |
| Bookings by Status | Monitor conversion rates, identify bottlenecks |
| Monthly Bookings | Seasonal demand analysis |
| Top Booked Rooms | Identify high-performers, optimize pricing |
| Average Stay Duration | Guest behavior insights |

### Key Metrics

- Total Revenue
- Active Bookings
- Available Rooms
- Occupancy Rate

---

## AI Insights

### Functionality

The system integrates Google Gemini 2.5 Flash to analyze booking data and provide:

- **Business Performance Assessment**: Overall health of the business
- **Trend Analysis**: Booking patterns and seasonality
- **Actionable Recommendations**: Specific steps to improve revenue
- **Risk Identification**: Potential issues to address

### Use Case

Admin accesses the AI Report section to receive intelligent analysis without manual data processing, enabling data-driven decision making.

---

## Database Schema

### Core Tables

| Table | Purpose |
|-------|---------|
| `room` | Room information (type, price, capacity, status, image) |
| `booking` | Reservation records with guest info and dates |
| `payment` | Transaction records for VNPay integration |

### Relationships

```
room (1) ────────< (N) booking
booking (1) ────────< (N) payment
```

---

## Installation

### Prerequisites

- JDK 8 or higher
- Apache Tomcat 9.x
- MySQL 8.x
- Gradle
- Gmail account with App Password (for email feature)

### Setup Steps

1. **Clone repository**
   ```bash
   git clone https://github.com/your-username/homestay-booking.git
   cd homestay-booking
   ```

2. **Create database**
   ```sql
   CREATE DATABASE homestay_booking;
   ```

3. **Import SQL schema**
   - Execute `database/schema.sql`

4. **Configure database connection**
   - Update `BaseRepository.java` with your MySQL credentials

5. **Configure email (optional)**
   - Update `EmailConfig.java` with Gmail and App Password

6. **Build project**
   ```bash
   ./gradlew build
   ```

7. **Deploy to Tomcat**
   - Copy WAR file to `tomcat/webapps/`
   - Or run via IDE with Tomcat integration

8. **Access application**
   - Customer: `http://localhost:8080/`
   - Admin: `http://localhost:8080/admin`

---

## Future Roadmap

| Feature | Description |
|---------|-------------|
| Multi-gateway Payment | Add Momo, ZaloPay support |
| Dynamic Pricing | Adjust prices based on demand/season |
| Mobile Application | Native iOS/Android apps |
| AI Demand Forecasting | Predict future booking volumes |
| Loyalty Program | Points and rewards for repeat customers |
| Multi-language | Support English, Vietnamese, etc. |

---

## Author

**Project Purpose**: Educational project for Java Web Development (Module 4)

**Technologies Demonstrated**:
- Java Servlet/JSP architecture
- Database design and JDBC
- Payment gateway integration
- Email automation
- PDF generation
- AI API integration
- Responsive web design

---

## License

This project is for educational purposes.