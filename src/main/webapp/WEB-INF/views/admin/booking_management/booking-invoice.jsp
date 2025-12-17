<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Invoice - ${invoiceNumber}</title>

            <!-- Bootstrap 5 -->
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
            <link rel="stylesheet"
                href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

            <style>
                /* Screen styles */
                body {
                    background-color: #f5f5f5;
                    padding: 20px;
                }

                .invoice-container {
                    max-width: 800px;
                    margin: 0 auto;
                    background: white;
                    padding: 40px;
                    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                }

                .invoice-header {
                    border-bottom: 3px solid #0d6efd;
                    padding-bottom: 20px;
                    margin-bottom: 30px;
                }

                .invoice-title {
                    color: #0d6efd;
                    font-size: 32px;
                    font-weight: bold;
                    margin-bottom: 10px;
                }

                .section-title {
                    background-color: #f8f9fa;
                    padding: 10px 15px;
                    border-left: 4px solid #0d6efd;
                    margin-bottom: 15px;
                    font-weight: bold;
                    color: #333;
                }

                .info-table {
                    width: 100%;
                    margin-bottom: 20px;
                }

                .info-table th {
                    width: 40%;
                    padding: 8px;
                    text-align: left;
                    color: #666;
                    font-weight: normal;
                }

                .info-table td {
                    padding: 8px;
                    font-weight: 500;
                }

                .payment-summary {
                    background-color: #f8f9fa;
                    padding: 20px;
                    border-radius: 5px;
                }

                .payment-row {
                    display: flex;
                    justify-content: space-between;
                    padding: 8px 0;
                    border-bottom: 1px solid #dee2e6;
                }

                .payment-row:last-child {
                    border-bottom: none;
                }

                .total-row {
                    font-size: 20px;
                    font-weight: bold;
                    color: #0d6efd;
                    padding-top: 15px;
                    border-top: 3px solid #0d6efd;
                }

                .footer-note {
                    margin-top: 40px;
                    padding-top: 20px;
                    border-top: 2px solid #dee2e6;
                    text-align: center;
                    color: #666;
                }

                .actions {
                    margin-bottom: 20px;
                    text-align: center;
                }

                .status-badge {
                    display: inline-block;
                    padding: 5px 15px;
                    border-radius: 20px;
                    font-size: 14px;
                    font-weight: bold;
                }

                /* Print styles */
                @media print {
                    body {
                        background: white;
                        padding: 0;
                    }

                    .invoice-container {
                        box-shadow: none;
                        padding: 20px;
                        max-width: 100%;
                    }

                    .actions {
                        display: none !important;
                    }

                    .no-print {
                        display: none !important;
                    }
                }
            </style>
        </head>

        <body>
            <!-- Action Buttons (not printed) -->
            <div class="actions no-print">
                <button onclick="window.print()" class="btn btn-primary btn-lg">
                    <i class="bi bi-printer"></i> Print Invoice
                </button>
                <a href="${pageContext.request.contextPath}/admin/bookings?action=detail&id=${booking.bookingId}"
                    class="btn btn-secondary btn-lg">
                    <i class="bi bi-arrow-left"></i> Back to Details
                </a>
            </div>

            <!-- Invoice Container -->
            <div class="invoice-container">

                <!-- 1. INVOICE HEADER -->
                <div class="invoice-header">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="invoice-title">INVOICE</div>
                            <p class="text-muted mb-0">Homestay Booking System</p>
                        </div>
                        <div class="col-md-6 text-end">
                            <h5 class="text-primary mb-2">${invoiceNumber}</h5>
                            <p class="mb-1"><strong>Issue Date:</strong> ${issueDate}</p>
                            <p class="mb-1"><strong>Booking ID:</strong> #${booking.bookingId}</p>
                            <c:choose>
                                <c:when test="${booking.status == 'PENDING'}">
                                    <span class="status-badge bg-warning text-dark">PENDING</span>
                                </c:when>
                                <c:when test="${booking.status == 'CONFIRMED'}">
                                    <span class="status-badge bg-success text-white">CONFIRMED</span>
                                </c:when>
                                <c:when test="${booking.status == 'COMPLETED'}">
                                    <span class="status-badge bg-info text-white">COMPLETED</span>
                                </c:when>
                                <c:when test="${booking.status == 'CANCELLED'}">
                                    <span class="status-badge bg-danger text-white">CANCELLED</span>
                                </c:when>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <!-- 2. GUEST INFORMATION -->
                <div class="section-title">
                    <i class="bi bi-person-circle"></i> Guest Information
                </div>
                <table class="info-table">
                    <tr>
                        <th>Guest Name:</th>
                        <td>${booking.guestName}</td>
                    </tr>
                    <tr>
                        <th>Email:</th>
                        <td>${booking.guestEmail}</td>
                    </tr>
                </table>

                <!-- 3. ROOM & STAY INFORMATION -->
                <div class="section-title">
                    <i class="bi bi-door-open"></i> Room & Stay Information
                </div>
                <table class="info-table">
                    <tr>
                        <th>Room:</th>
                        <td>Room #${room.roomId}</td>
                    </tr>
                    <tr>
                        <th>Room Type:</th>
                        <td>${room.roomType}</td>
                    </tr>
                    <tr>
                        <th>Capacity:</th>
                        <td>${room.sleepSlot} people</td>
                    </tr>
                    <tr>
                        <th>Price per Night:</th>
                        <td>${room.formattedPrice}</td>
                    </tr>
                    <tr>
                        <th>Check-in Date:</th>
                        <td><i class="bi bi-calendar3"></i> ${booking.checkInDate}</td>
                    </tr>
                    <tr>
                        <th>Check-out Date:</th>
                        <td><i class="bi bi-calendar3"></i> ${booking.checkOutDate}</td>
                    </tr>
                    <tr>
                        <th>Number of Nights:</th>
                        <td><strong>${booking.numberOfNights}</strong> nights</td>
                    </tr>
                </table>

                <!-- 4. PAYMENT SUMMARY -->
                <div class="section-title">
                    <i class="bi bi-credit-card"></i> Payment Summary
                </div>
                <div class="payment-summary">
                    <div class="payment-row">
                        <span>Subtotal:</span>
                        <span>${booking.formattedPrice}</span>
                    </div>
                    <div class="payment-row">
                        <span>Tax (VAT):</span>
                        <span>0 VND</span>
                    </div>
                    <div class="payment-row">
                        <span>Discount:</span>
                        <span>0 VND</span>
                    </div>
                    <div class="payment-row total-row">
                        <span>TOTAL AMOUNT:</span>
                        <span>${booking.formattedPrice}</span>
                    </div>
                </div>

                <!-- 5. FOOTER -->
                <div class="footer-note">
                    <p class="mb-2"><strong>Thank you for choosing our homestay!</strong></p>
                    <p class="mb-1"><i class="bi bi-house-heart"></i> Homestay Booking System</p>
                    <p class="mb-1"><i class="bi bi-geo-alt"></i> 123 Beach Road, Da Nang, Vietnam</p>
                    <p class="mb-0"><i class="bi bi-telephone"></i> Hotline: +84 123 456 789</p>
                </div>

            </div>

            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
        </body>

        </html>