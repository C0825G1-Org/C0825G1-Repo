<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Change Room · Homestay</title>
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
            <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
            <link
                href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;700&family=Inter:wght@300;400;500;600&display=swap"
                rel="stylesheet">

            <style>
                :root {
                    --cream: #FDF5E6;
                    --mocha: #8B7355;
                    --sage: #9CAF88;
                    --dark-mocha: #5D4E37;
                    --light-sage: #C8D5B9;
                }

                body {
                    font-family: 'Inter', sans-serif;
                    background: var(--cream);
                    color: var(--dark-mocha);
                }

                .navbar {
                    background: rgba(253, 245, 230, 0.95);
                    backdrop-filter: blur(10px);
                    box-shadow: 0 2px 20px rgba(0, 0, 0, 0.05);
                }

                .navbar-brand {
                    font-family: 'Playfair Display', serif;
                    font-size: 1.5rem;
                    font-weight: 700;
                    color: var(--dark-mocha) !important;
                }

                .page-title {
                    font-family: 'Playfair Display', serif;
                    font-size: 2.5rem;
                    color: var(--dark-mocha);
                }

                .current-booking {
                    background: linear-gradient(135deg, var(--sage), var(--mocha));
                    color: white;
                    border-radius: 24px;
                    padding: 2rem;
                    margin-bottom: 3rem;
                }

                .room-card {
                    background: white;
                    border-radius: 20px;
                    overflow: hidden;
                    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
                    transition: all 0.3s;
                    height: 100%;
                }

                .room-card:hover {
                    transform: translateY(-8px);
                    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
                }

                .room-card img {
                    width: 100%;
                    height: 220px;
                    object-fit: cover;
                }

                .room-card-body {
                    padding: 1.5rem;
                }

                .badge-type {
                    background: var(--light-sage);
                    color: var(--dark-mocha);
                    padding: 0.4rem 1rem;
                    border-radius: 20px;
                    font-size: 0.85rem;
                    font-weight: 600;
                }

                .price-old {
                    color: #999;
                    text-decoration: line-through;
                    font-size: 0.9rem;
                }

                .price-new {
                    font-family: 'Playfair Display', serif;
                    font-size: 1.8rem;
                    color: var(--mocha);
                    font-weight: 700;
                }

                .btn-select {
                    background: var(--sage);
                    color: white;
                    border: none;
                    padding: 0.8rem;
                    border-radius: 12px;
                    font-weight: 600;
                    width: 100%;
                }

                .btn-select:hover {
                    background: var(--mocha);
                }

                .alert {
                    border-radius: 16px;
                    border: none;
                }
            </style>
        </head>

        <body>
            <nav class="navbar navbar-expand-lg sticky-top">
                <div class="container">
                    <a class="navbar-brand" href="${pageContext.request.contextPath}/rooms">
                        <i class="bi bi-tree"></i> Homestay
                    </a>
                    <div class="navbar-nav ms-auto">
                        <a class="nav-link" href="${pageContext.request.contextPath}/my-bookings?email=${email}">
                            <i class="bi bi-arrow-left"></i> Back to My Trips
                        </a>
                    </div>
                </div>
            </nav>

            <div class="container my-5">
                <h1 class="page-title mb-4"><i class="bi bi-arrow-left-right"></i> Switch Your Room</h1>
                <p class="text-muted mb-4">Choose a different vibe for your stay</p>

                <!-- Current Booking -->
                <div class="current-booking">
                    <h5 class="mb-3"><i class="bi bi-info-circle"></i> Current Booking</h5>
                    <div class="row">
                        <div class="col-md-3">
                            <small style="opacity: 0.9;">Room</small>
                            <div class="fs-5 fw-bold">Room #${booking.roomId}</div>
                        </div>
                        <div class="col-md-3">
                            <small style="opacity: 0.9;">Guest</small>
                            <div class="fw-semibold">${booking.guestName}</div>
                        </div>
                        <div class="col-md-3">
                            <small style="opacity: 0.9;">Dates</small>
                            <div class="fw-semibold">${booking.checkInDate} → ${booking.checkOutDate}</div>
                        </div>
                        <div class="col-md-3">
                            <small style="opacity: 0.9;">Current Price</small>
                            <div class="fs-5 fw-bold">${booking.formattedTotalPrice}</div>
                        </div>
                    </div>
                </div>

                <!-- Available Rooms -->
                <h4 class="mb-4">Available Rooms for Your Dates</h4>

                <c:choose>
                    <c:when test="${empty availableRooms}">
                        <div class="alert alert-warning">
                            <i class="bi bi-exclamation-triangle"></i> No other rooms available for your booking dates.
                            Try changing your dates instead!
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="row g-4">
                            <c:forEach var="room" items="${availableRooms}">
                                <div class="col-md-4">
                                    <div class="room-card">
                                        <img src="${not empty room.imageUrl ? room.imageUrl : 'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=600'}"
                                            alt="Room ${room.roomId}">
                                        <div class="room-card-body">
                                            <div class="d-flex justify-content-between align-items-start mb-2">
                                                <h5 class="mb-0">Room #${room.roomId}</h5>
                                                <span class="badge-type">${room.roomType}</span>
                                            </div>

                                            <p class="text-muted small mb-3">${room.description}</p>

                                            <div class="d-flex align-items-center gap-3 mb-2">
                                                <i class="bi bi-people text-muted"></i>
                                                <span class="small">${room.sleepSlot} guests</span>
                                            </div>

                                            <hr class="my-3">

                                            <div class="mb-3">
                                                <div class="price-old mb-1">${booking.formattedTotalPrice}</div>
                                                <c:set var="nights"
                                                    value="${booking.checkOutDate.toEpochDay() - booking.checkInDate.toEpochDay()}" />
                                                <c:set var="newTotal" value="${room.roomPrice * nights}" />
                                                <div class="price-new">${String.format("%,.0f", newTotal)} VND</div>
                                            </div>

                                            <form method="post" action="${pageContext.request.contextPath}/my-bookings">
                                                <input type="hidden" name="action" value="changeRoom">
                                                <input type="hidden" name="id" value="${booking.bookingId}">
                                                <input type="hidden" name="newRoomId" value="${room.roomId}">
                                                <input type="hidden" name="email" value="${email}">
                                                <button type="submit" class="btn btn-select"
                                                    onclick="return confirm('Switch to Room #${room.roomId}?');">
                                                    <i class="bi bi-check-circle"></i> Select This Room
                                                </button>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        </body>

        </html>