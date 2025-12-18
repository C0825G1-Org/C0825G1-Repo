<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>${room.roomType} Room · Homestay</title>
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

                .nav-link {
                    color: var(--mocha) !important;
                    font-weight: 500;
                }

                .room-hero {
                    border-radius: 30px;
                    overflow: hidden;
                    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
                    margin-bottom: 2rem;
                }

                .room-hero img {
                    width: 100%;
                    height: 500px;
                    object-fit: cover;
                }

                .room-title {
                    font-family: 'Playfair Display', serif;
                    font-size: 3rem;
                    color: var(--dark-mocha);
                    margin-bottom: 1rem;
                }

                .badge-type {
                    background: var(--sage);
                    color: white;
                    padding: 0.5rem 1.5rem;
                    border-radius: 20px;
                    font-size: 1rem;
                }

                .price-tag {
                    font-family: 'Playfair Display', serif;
                    font-size: 2.5rem;
                    color: var(--mocha);
                    font-weight: 700;
                }

                .booking-card {
                    background: white;
                    border-radius: 24px;
                    padding: 2rem;
                    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
                    position: sticky;
                    top: 100px;
                }

                .form-label {
                    font-weight: 600;
                    color: var(--dark-mocha);
                    margin-bottom: 0.5rem;
                }

                .form-control {
                    border: 2px solid var(--light-sage);
                    border-radius: 12px;
                    padding: 0.8rem;
                }

                .form-control:focus {
                    border-color: var(--sage);
                    box-shadow: 0 0 0 3px rgba(156, 175, 136, 0.1);
                }

                .btn-book {
                    background: var(--sage);
                    color: white;
                    border: none;
                    padding: 1rem;
                    border-radius: 12px;
                    font-weight: 600;
                    font-size: 1.1rem;
                    transition: all 0.3s;
                }

                .btn-book:hover {
                    background: var(--mocha);
                    transform: translateY(-2px);
                    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
                }

                .detail-section {
                    background: white;
                    border-radius: 24px;
                    padding: 2rem;
                    margin-bottom: 2rem;
                    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
                }

                .price-preview {
                    background: linear-gradient(135deg, var(--sage), var(--mocha));
                    color: white;
                    padding: 1.5rem;
                    border-radius: 16px;
                    margin-top: 1rem;
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
                        <a class="nav-link" href="${pageContext.request.contextPath}/rooms">
                            <i class="bi bi-arrow-left"></i> Back to Rooms
                        </a>
                        <a class="nav-link" href="${pageContext.request.contextPath}/my-bookings">My Trips</a>
                    </div>
                </div>
            </nav>

            <div class="container my-5">
                <!-- Alert Messages -->
                <c:if test="${not empty sessionScope.errorMessage}">
                    <div class="alert alert-danger mb-4">
                        <i class="bi bi-exclamation-triangle"></i> ${sessionScope.errorMessage}
                    </div>
                    <c:remove var="errorMessage" scope="session" />
                </c:if>

                <div class="row">
                    <div class="col-lg-7">
                        <!-- Room Hero Image -->
                        <div class="room-hero">
                            <img src="${not empty room.imageUrl ? room.imageUrl : 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=1200'}"
                                alt="${room.roomType} Room">
                        </div>

                        <!-- Room Details -->
                        <div class="detail-section">
                            <div class="d-flex justify-content-between align-items-start mb-4">
                                <div>
                                    <h1 class="room-title">Room #${room.roomId}</h1>
                                    <span class="badge-type">${room.roomType}</span>
                                </div>
                                <div class="text-end">
                                    <div class="price-tag">${room.formattedPrice}</div>
                                    <small class="text-muted">per night</small>
                                </div>
                            </div>

                            <h5 class="mb-3"><i class="bi bi-info-circle text-muted"></i> About This Space</h5>
                            <p class="text-muted">${room.description}</p>

                            <hr class="my-4">

                            <h5 class="mb-3"><i class="bi bi-list-check text-muted"></i> What You'll Get</h5>
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <i class="bi bi-people-fill text-muted"></i>
                                    <strong class="ms-2">Sleeps ${room.sleepSlot}</strong>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <i class="bi bi-tag-fill text-muted"></i>
                                    <strong class="ms-2">${room.roomType} Vibe</strong>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-5">
                        <div class="booking-card">
                            <h4 class="mb-4"><i class="bi bi-calendar-check"></i> Book Your Stay</h4>

                            <form id="bookingForm" action="${pageContext.request.contextPath}/booking/create"
                                method="post">
                                <input type="hidden" name="roomId" value="${room.roomId}">

                                <div class="mb-3">
                                    <label for="guestName" class="form-label">Your Name *</label>
                                    <input type="text" class="form-control" id="guestName" name="guestName"
                                        placeholder="John Doe" required>
                                </div>

                                <div class="mb-3">
                                    <label for="guestEmail" class="form-label">Email Address *</label>
                                    <input type="email" class="form-control" id="guestEmail" name="guestEmail"
                                        placeholder="you@example.com" required>
                                    <small class="text-muted">We'll send confirmation here</small>
                                </div>

                                <div class="mb-3">
                                    <label for="checkInDate" class="form-label">Check-in *</label>
                                    <input type="date" class="form-control" id="checkInDate" name="checkInDate"
                                        required>
                                </div>

                                <div class="mb-3">
                                    <label for="checkOutDate" class="form-label">Check-out *</label>
                                    <input type="date" class="form-control" id="checkOutDate" name="checkOutDate"
                                        required>
                                </div>

                                <div class="price-preview d-none" id="pricePreview">
                                    <div class="d-flex justify-content-between mb-2">
                                        <span>Nights:</span>
                                        <strong id="nights">0</strong>
                                    </div>
                                    <div class="d-flex justify-content-between mb-2">
                                        <span>Price/night:</span>
                                        <strong>${room.formattedPrice}</strong>
                                    </div>
                                    <hr style="border-color: rgba(255,255,255,0.3);">
                                    <div class="d-flex justify-content-between">
                                        <span class="fs-5">Total:</span>
                                        <strong class="fs-4" id="totalPrice">0 VND</strong>
                                    </div>
                                </div>

                                <button type="submit" class="btn btn-book w-100 mt-3">
                                    <i class="bi bi-calendar-plus"></i> Book Now
                                </button>

                                <p class="text-muted small text-center mt-3 mb-0">
                                    <i class="bi bi-shield-check"></i> Status will be PENDING until admin confirms
                                </p>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
            <script>
                const today = new Date().toISOString().split('T')[0];
                document.getElementById('checkInDate').setAttribute('min', today);
                document.getElementById('checkOutDate').setAttribute('min', today);

                const pricePerNight = ${ room.roomPrice };

                function calculateTotal() {
                    const checkIn = document.getElementById('checkInDate').value;
                    const checkOut = document.getElementById('checkOutDate').value;
                    const preview = document.getElementById('pricePreview');

                    if (!checkIn || !checkOut) {
                        preview.classList.add('d-none');
                        return;
                    }

                    const checkInDate = new Date(checkIn);
                    const checkOutDate = new Date(checkOut);

                    if (checkOutDate <= checkInDate) {
                        preview.classList.add('d-none');
                        return;
                    }

                    const nights = Math.ceil((checkOutDate - checkInDate) / (1000 * 60 * 60 * 24));
                    const total = pricePerNight * nights;

                    document.getElementById('nights').textContent = nights;
                    document.getElementById('totalPrice').textContent = total.toLocaleString('vi-VN') + ' VND';
                    preview.classList.remove('d-none');
                }

                document.getElementById('checkInDate').addEventListener('change', function () {
                    const nextDay = new Date(this.value);
                    nextDay.setDate(nextDay.getDate() + 1);
                    document.getElementById('checkOutDate').setAttribute('min', nextDay.toISOString().split('T')[0]);
                    calculateTotal();
                });

                document.getElementById('checkOutDate').addEventListener('change', calculateTotal);

                document.getElementById('bookingForm').addEventListener('submit', function (e) {
                    const checkIn = new Date(document.getElementById('checkInDate').value);
                    const checkOut = new Date(document.getElementById('checkOutDate').value);

                    if (checkOut <= checkIn) {
                        e.preventDefault();
                        alert('Check-out date must be after check-in date!');
                        return false;
                    }
                });
            </script>
        </body>

        </html>