<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Edit Booking · Homestay</title>
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

                .edit-card {
                    background: white;
                    border-radius: 24px;
                    padding: 2.5rem;
                    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
                }

                .info-card {
                    background: linear-gradient(135deg, var(--sage), var(--mocha));
                    color: white;
                    border-radius: 20px;
                    padding: 2rem;
                    margin-bottom: 2rem;
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

                .form-control:disabled {
                    background: #f5f5f5;
                    border-color: #ddd;
                }

                .btn-save {
                    background: var(--sage);
                    color: white;
                    border: none;
                    padding: 1rem 2.5rem;
                    border-radius: 12px;
                    font-weight: 600;
                    font-size: 1.1rem;
                }

                .btn-save:hover {
                    background: var(--mocha);
                    transform: translateY(-2px);
                }

                .alert {
                    border-radius: 16px;
                    border: none;
                }

                .lock-icon {
                    color: var(--mocha);
                    margin-left: 0.5rem;
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
                <h1 class="page-title mb-4"><i class="bi bi-pencil-square"></i> Edit Your Stay</h1>

                <div class="row">
                    <div class="col-lg-8">
                        <div class="edit-card">
                            <form method="post" action="${pageContext.request.contextPath}/my-bookings">
                                <input type="hidden" name="action" value="update">
                                <input type="hidden" name="id" value="${booking.bookingId}">
                                <input type="hidden" name="email" value="${email}">

                                <h5 class="mb-4">Booking Information</h5>

                                <div class="row">
                                    <div class="col-md-6 mb-3">
                                        <label class="form-label">Guest Name</label>
                                        <input type="text" class="form-control" value="${booking.guestName}" disabled>
                                    </div>

                                    <div class="col-md-6 mb-3">
                                        <label class="form-label">Email Address</label>
                                        <input type="email" class="form-control" value="${booking.guestEmail}" disabled>
                                    </div>

                                    <div class="col-md-12 mb-3">
                                        <label class="form-label">Room</label>
                                        <input type="text" class="form-control" value="Room #${booking.roomId}"
                                            disabled>
                                    </div>
                                </div>

                                <hr class="my-4">

                                <h5 class="mb-4">Stay Dates</h5>

                                <div class="row">
                                    <div class="col-md-6 mb-3">
                                        <label for="checkInDate" class="form-label">
                                            Check-in Date *
                                        </label>
                                        <input type="date" class="form-control" id="checkInDate" name="checkInDate"
                                            value="${booking.checkInDate}" required ${!booking.canEditCheckIn()
                                            ? 'disabled' : '' }>
                                        <c:if test="${!booking.canEditCheckIn()}">
                                            <input type="hidden" name="checkInDate" value="${booking.checkInDate}">
                                        </c:if>
                                    </div>

                                    <div class="col-md-6 mb-3">
                                        <label for="checkOutDate" class="form-label">Check-out Date *</label>
                                        <input type="date" class="form-control" id="checkOutDate" name="checkOutDate"
                                            value="${booking.checkOutDate}" required ${!booking.canEditCheckOut()
                                            ? 'disabled' : '' }>
                                        <c:if test="${!booking.canEditCheckOut()}">
                                            <input type="hidden" name="checkOutDate" value="${booking.checkOutDate}">
                                        </c:if>
                                    </div>
                                </div>

                                <!-- Price Calculation -->
                                <div id="priceCalculation" class="alert alert-info d-none">
                                    <div class="row">
                                        <div class="col-6">
                                            <small class="text-muted">Giá cũ:</small>
                                            <div><strong id="oldPrice">${booking.formattedTotalPrice}</strong></div>
                                        </div>
                                        <div class="col-6">
                                            <small class="text-muted">Giá mới:</small>
                                            <div><strong id="newPrice">--</strong></div>
                                        </div>
                                    </div>
                                    <div id="priceDiffContainer" class="mt-2 d-none">
                                        <hr>
                                        <div class="d-flex justify-content-between align-items-center">
                                            <span id="priceDiffLabel">Chênh lệch:</span>
                                            <strong id="priceDiff" class="fs-5">--</strong>
                                        </div>
                                        <small id="paymentNote" class="text-warning d-none">
                                            <i class="bi bi-exclamation-triangle"></i> Bạn cần thanh toán thêm trước khi
                                            cập nhật
                                        </small>
                                    </div>
                                </div>

                                <!-- Hidden fields for payment calculation -->
                                <input type="hidden" id="originalPrice" value="${booking.totalPrice}">
                                <input type="hidden" id="roomPrice" value="${room.roomPrice}">
                                <input type="hidden" id="needsPayment" name="needsPayment" value="false">

                                <div class="d-flex gap-3 mt-4">
                                    <button type="submit" class="btn btn-save">
                                        <i class="bi bi-save"></i> Save Changes
                                    </button>
                                    <a href="${pageContext.request.contextPath}/my-bookings?email=${email}"
                                        class="btn btn-outline-secondary px-4" style="border-radius: 12px;">
                                        Cancel
                                    </a>
                                </div>
                            </form>
                        </div>
                    </div>

                    <div class="col-lg-4">
                        <div class="info-card">
                            <h5 class="mb-3"><i class="bi bi-info-circle"></i> Quy định chỉnh sửa</h5>
                            <ul class="mb-0" style="padding-left: 1.2rem;">
                                <li class="mb-2">Bạn có thể thay đổi cả check-in và check-out</li>
                                <li class="mb-2">Hệ thống sẽ kiểm tra phòng trống</li>
                                <li class="mb-2">Nếu giá mới cao hơn → cần thanh toán thêm</li>
                                <li>Nếu giá mới thấp hơn → không hoàn tiền</li>
                            </ul>
                        </div>

                        <div class="edit-card">
                            <h6 class="mb-3">Current Booking</h6>
                            <div class="mb-2">
                                <small class="text-muted">Status</small>
                                <div>
                                    <c:choose>
                                        <c:when test="${booking.status.toString() == 'PENDING'}">
                                            <span class="badge bg-warning">PENDING</span>
                                        </c:when>
                                        <c:when test="${booking.status.toString() == 'CONFIRMED'}">
                                            <span class="badge bg-success">CONFIRMED</span>
                                        </c:when>
                                    </c:choose>
                                </div>
                            </div>
                            <div class="mb-2">
                                <small class="text-muted">Current Total</small>
                                <div><strong>${booking.formattedTotalPrice}</strong></div>
                            </div>
                            <small class="text-muted">Price will update after saving</small>
                        </div>
                    </div>
                </div>
            </div>

            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
            <script>
                const checkInInput = document.getElementById('checkInDate');
                const checkOutInput = document.getElementById('checkOutDate');
                const originalPrice = parseFloat(document.getElementById('originalPrice').value) || 0;
                const roomPrice = parseFloat(document.getElementById('roomPrice').value) || 0;
                const priceCalcDiv = document.getElementById('priceCalculation');
                const newPriceSpan = document.getElementById('newPrice');
                const priceDiffContainer = document.getElementById('priceDiffContainer');
                const priceDiffSpan = document.getElementById('priceDiff');
                const priceDiffLabel = document.getElementById('priceDiffLabel');
                const paymentNote = document.getElementById('paymentNote');
                const needsPaymentInput = document.getElementById('needsPayment');
                const submitBtn = document.querySelector('button[type="submit"]');
                const today = new Date().toISOString().split('T')[0];

                // Set min dates
                if (checkInInput && !checkInInput.disabled) {
                    checkInInput.setAttribute('min', today);
                }

                function calculatePrice() {
                    const checkIn = new Date(checkInInput.value);
                    const checkOut = new Date(checkOutInput.value);

                    if (isNaN(checkIn) || isNaN(checkOut) || checkOut <= checkIn) {
                        priceCalcDiv.classList.add('d-none');
                        return;
                    }

                    const nights = Math.ceil((checkOut - checkIn) / (1000 * 60 * 60 * 24));
                    const newPrice = nights * roomPrice;
                    const diff = newPrice - originalPrice;

                    priceCalcDiv.classList.remove('d-none');
                    newPriceSpan.textContent = new Intl.NumberFormat('vi-VN').format(newPrice) + ' VND';

                    if (diff !== 0) {
                        priceDiffContainer.classList.remove('d-none');
                        if (diff > 0) {
                            priceDiffSpan.textContent = '+' + new Intl.NumberFormat('vi-VN').format(diff) + ' VND';
                            priceDiffSpan.className = 'fs-5 text-danger';
                            priceDiffLabel.textContent = 'Cần thanh toán thêm:';
                            paymentNote.classList.remove('d-none');
                            needsPaymentInput.value = 'true';
                            submitBtn.innerHTML = '<i class="bi bi-credit-card"></i> Thanh toán & Cập nhật';
                        } else {
                            priceDiffSpan.textContent = new Intl.NumberFormat('vi-VN').format(diff) + ' VND';
                            priceDiffSpan.className = 'fs-5 text-success';
                            priceDiffLabel.textContent = 'Giảm:';
                            paymentNote.classList.add('d-none');
                            needsPaymentInput.value = 'false';
                            submitBtn.innerHTML = '<i class="bi bi-save"></i> Lưu thay đổi';
                        }
                    } else {
                        priceDiffContainer.classList.add('d-none');
                        needsPaymentInput.value = 'false';
                        submitBtn.innerHTML = '<i class="bi bi-save"></i> Lưu thay đổi';
                    }
                }

                if (checkInInput) {
                    checkInInput.addEventListener('change', function () {
                        checkOutInput.setAttribute('min', this.value);
                        if (checkOutInput.value && checkOutInput.value <= this.value) {
                            const nextDay = new Date(this.value);
                            nextDay.setDate(nextDay.getDate() + 1);
                            checkOutInput.value = nextDay.toISOString().split('T')[0];
                        }
                        calculatePrice();
                    });
                }
                if (checkOutInput) {
                    checkOutInput.addEventListener('change', calculatePrice);
                }
            </script>
        </body>

        </html>