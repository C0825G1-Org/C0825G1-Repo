<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <!-- Page Header -->
        <div class="page-header">
            <h2><i class="bi bi-pencil-square"></i> Edit Booking #${booking.bookingId}</h2>
            <p class="text-muted mb-0">Modify booking information</p>
        </div>

        <!-- Back Button -->
        <div class="mb-3">
            <a href="${pageContext.request.contextPath}/admin/bookings?action=detail&id=${booking.bookingId}"
                class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left"></i> Back to Details
            </a>
        </div>

        <!-- Alert Messages -->
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle"></i> ${sessionScope.successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="successMessage" scope="session" />
        </c:if>

        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle"></i> ${sessionScope.errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="errorMessage" scope="session" />
        </c:if>

        <!-- Edit Booking Form -->
        <div class="card">
            <div class="card-body">
                <form method="post" action="${pageContext.request.contextPath}/admin/bookings" id="bookingForm">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="bookingId" value="${booking.bookingId}">

                    <div class="row">
                        <!-- Guest Information -->
                        <div class="col-md-6">
                            <h5 class="mb-3"><i class="bi bi-person"></i> Guest Information</h5>

                            <div class="mb-3">
                                <label for="guestName" class="form-label">Guest Name <span
                                        class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="guestName" name="guestName" required
                                    value="${booking.guestName}" placeholder="Enter guest full name">
                            </div>

                            <div class="mb-3">
                                <label for="guestEmail" class="form-label">Guest Email <span
                                        class="text-danger">*</span></label>
                                <input type="email" class="form-control" id="guestEmail" name="guestEmail" required
                                    value="${booking.guestEmail}" placeholder="guest@example.com">
                            </div>
                        </div>

                        <!-- Booking Information -->
                        <div class="col-md-6">
                            <h5 class="mb-3"><i class="bi bi-calendar-check"></i> Booking Information</h5>

                            <div class="mb-3">
                                <label for="roomId" class="form-label">Select Room <span
                                        class="text-danger">*</span></label>
                                <select class="form-select" id="roomId" name="roomId" required
                                    onchange="updateRoomInfo()">
                                    <option value="">-- Choose a room --</option>
                                    <c:forEach var="room" items="${rooms}">
                                        <option value="${room.roomId}" data-price="${room.roomPrice}"
                                            data-type="${room.roomType}" data-slot="${room.sleepSlot}"
                                            ${room.roomId==booking.roomId ? 'selected' : '' }>
                                            Room #${room.roomId} - ${room.roomType} - ${room.formattedPrice}/night
                                        </option>
                                    </c:forEach>
                                </select>
                                <div id="roomInfo" class="mt-2 text-muted small"></div>
                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="checkInDate" class="form-label">Check-in Date <span
                                            class="text-danger">*</span></label>
                                    <input type="date" class="form-control" id="checkInDate" name="checkInDate" required
                                        value="${booking.checkInDate}" onchange="calculateTotal()">
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label for="checkOutDate" class="form-label">Check-out Date <span
                                            class="text-danger">*</span></label>
                                    <input type="date" class="form-control" id="checkOutDate" name="checkOutDate"
                                        required value="${booking.checkOutDate}" onchange="calculateTotal()">
                                </div>
                            </div>

                            <!-- Total Price Preview -->
                            <div class="alert alert-info" id="pricePreview" style="display: block;">
                                <h6 class="mb-1">Booking Summary</h6>
                                <div><strong>Nights:</strong> <span id="nights">${booking.numberOfNights}</span></div>
                                <div><strong>Total Price:</strong> <span
                                        id="totalPrice">${booking.formattedPrice}</span></div>
                            </div>
                        </div>
                    </div>

                    <hr>

                    <!-- Submit Buttons -->
                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-save"></i> Update Booking
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/bookings?action=detail&id=${booking.bookingId}"
                            class="btn btn-secondary">
                            <i class="bi bi-x-circle"></i> Cancel
                        </a>
                    </div>
                </form>
            </div>
        </div>

        <!-- JavaScript -->
        <script>
            // Set minimum date to today
            const today = new Date().toISOString().split('T')[0];
            document.getElementById('checkInDate').setAttribute('min', today);
            document.getElementById('checkOutDate').setAttribute('min', today);

            // Trigger initial room info display
            updateRoomInfo();

            function updateRoomInfo() {
                const select = document.getElementById('roomId');
                const option = select.options[select.selectedIndex];
                const roomInfo = document.getElementById('roomInfo');

                if (option.value) {
                    const roomType = option.getAttribute('data-type');
                    const sleepSlot = option.getAttribute('data-slot');
                    roomInfo.innerHTML = '<i class="bi bi-info-circle"></i> ' + roomType + ' room - Capacity: ' + sleepSlot + ' people';
                } else {
                    roomInfo.innerHTML = '';
                }

                calculateTotal();
            }

            function calculateTotal() {
                const roomSelect = document.getElementById('roomId');
                const checkIn = document.getElementById('checkInDate').value;
                const checkOut = document.getElementById('checkOutDate').value;
                const pricePreview = document.getElementById('pricePreview');

                if (!roomSelect.value || !checkIn || !checkOut) {
                    pricePreview.style.display = 'none';
                    return;
                }

                const option = roomSelect.options[roomSelect.selectedIndex];
                const pricePerNight = parseFloat(option.getAttribute('data-price'));

                const checkInDate = new Date(checkIn);
                const checkOutDate = new Date(checkOut);

                if (checkOutDate <= checkInDate) {
                    pricePreview.style.display = 'none';
                    alert('Check-out date must be after check-in date!');
                    return;
                }

                const nights = Math.ceil((checkOutDate - checkInDate) / (1000 * 60 * 60 * 24));
                const totalPrice = pricePerNight * nights;

                document.getElementById('nights').textContent = nights;
                document.getElementById('totalPrice').textContent = totalPrice.toLocaleString('vi-VN') + ' VND';
                pricePreview.style.display = 'block';
            }

            // Validate check-out date
            document.getElementById('checkInDate').addEventListener('change', function () {
                const checkOutInput = document.getElementById('checkOutDate');
                const nextDay = new Date(this.value);
                nextDay.setDate(nextDay.getDate() + 1);
                checkOutInput.setAttribute('min', nextDay.toISOString().split('T')[0]);
            });

            // Form validation
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