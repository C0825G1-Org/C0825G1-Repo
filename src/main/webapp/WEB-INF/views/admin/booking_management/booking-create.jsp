<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <!-- Page Header -->
        <div class="page-header">
            <h2><i class="bi bi-plus-circle"></i> Create New Booking</h2>
            <p class="text-muted mb-0">Create a new booking for a guest</p>
        </div>

        <!-- Back Button -->
        <div class="mb-3">
            <a href="${pageContext.request.contextPath}/admin/bookings" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left"></i> Back to List
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

        <!-- Create Booking Form -->
        <div class="card">
            <div class="card-body">
                <form method="post" action="${pageContext.request.contextPath}/admin/bookings" id="bookingForm">
                    <input type="hidden" name="action" value="create">

                    <div class="row">
                        <!-- Guest Information -->
                        <div class="col-md-6">
                            <h5 class="mb-3"><i class="bi bi-person"></i> Guest Information</h5>

                            <div class="mb-3">
                                <label for="guestName" class="form-label">Guest Name <span
                                        class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="guestName" name="guestName" required
                                    placeholder="Enter guest full name">
                            </div>

                            <div class="mb-3">
                                <label for="guestEmail" class="form-label">Guest Email <span
                                        class="text-danger">*</span></label>
                                <input type="email" class="form-control" id="guestEmail" name="guestEmail" required
                                    placeholder="guest@example.com">
                            </div>
                        </div>

                        <!-- Booking Information -->
                        <div class="col-md-6">
                            <h5 class="mb-3"><i class="bi bi-calendar-check"></i> Booking Information</h5>

                            <!-- Step 1: Select Dates First -->
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="checkInDate" class="form-label">
                                        <span class="badge bg-primary me-1">1</span> Check-in Date <span
                                            class="text-danger">*</span>
                                    </label>
                                    <input type="date" class="form-control" id="checkInDate" name="checkInDate"
                                        required>
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label for="checkOutDate" class="form-label">
                                        <span class="badge bg-primary me-1">2</span> Check-out Date <span
                                            class="text-danger">*</span>
                                    </label>
                                    <input type="date" class="form-control" id="checkOutDate" name="checkOutDate"
                                        required>
                                </div>
                            </div>

                            <!-- Step 2: Select Room (Disabled until dates selected) -->
                            <div class="mb-3">
                                <label for="roomId" class="form-label">
                                    <span class="badge bg-primary me-1">3</span> Select Room <span
                                        class="text-danger">*</span>
                                </label>
                                <select class="form-select" id="roomId" name="roomId" required disabled>
                                    <option value="">-- Please choose the check-in and check-out day first --</option>
                                </select>

                                <!-- Room Status Message -->
                                <div id="roomMessage" class="mt-2"></div>

                                <!-- Room Info -->
                                <div id="roomInfo" class="mt-2 text-muted small"></div>
                            </div>

                            <!-- Total Price Preview -->
                            <div class="alert alert-info" id="pricePreview" style="display: none;">
                                <h6 class="mb-1"><i class="bi bi-receipt"></i> Booking Summary</h6>
                                <div><strong>Nights:</strong> <span id="nights">0</span></div>
                                <div><strong>Total Price:</strong> <span id="totalPrice" class="text-success fw-bold">0
                                        VND</span></div>
                            </div>
                        </div>
                    </div>

                    <hr>

                    <!-- Submit Buttons -->
                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-primary" id="submitBtn" disabled>
                            <i class="bi bi-save"></i> Create Booking
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/bookings" class="btn btn-secondary">
                            <i class="bi bi-x-circle"></i> Cancel
                        </a>
                    </div>
                </form>
            </div>
        </div>

        <!-- JavaScript -->
        <script>
            const contextPath = '${pageContext.request.contextPath}';

            // Set minimum date to today
            const today = new Date().toISOString().split('T')[0];
            document.getElementById('checkInDate').setAttribute('min', today);
            document.getElementById('checkOutDate').setAttribute('min', today);

            // Track room data for price calculation
            let roomsData = {};

            // Check-in date change handler
            document.getElementById('checkInDate').addEventListener('change', function () {
                const checkOutInput = document.getElementById('checkOutDate');
                const nextDay = new Date(this.value);
                nextDay.setDate(nextDay.getDate() + 1);
                checkOutInput.setAttribute('min', nextDay.toISOString().split('T')[0]);

                // Clear check-out if it's before new min
                if (checkOutInput.value && new Date(checkOutInput.value) <= new Date(this.value)) {
                    checkOutInput.value = '';
                }

                checkAvailableRooms();
            });

            // Check-out date change handler
            document.getElementById('checkOutDate').addEventListener('change', function () {
                checkAvailableRooms();
            });

            // Room selection change handler
            document.getElementById('roomId').addEventListener('change', function () {
                updateRoomInfo();
                calculateTotal();
                updateSubmitButton();
            });

            // Check available rooms via AJAX
            function checkAvailableRooms() {
                const checkIn = document.getElementById('checkInDate').value;
                const checkOut = document.getElementById('checkOutDate').value;
                const roomSelect = document.getElementById('roomId');
                const roomMessage = document.getElementById('roomMessage');
                const pricePreview = document.getElementById('pricePreview');
                const roomInfo = document.getElementById('roomInfo');

                // Reset room selection
                if (roomSelect) {
                    roomSelect.innerHTML = '<option value="">-- Please choose the check-in and check-out day first --</option>';
                    roomSelect.disabled = true;
                }
                if (roomMessage) roomMessage.innerHTML = '';
                if (pricePreview) pricePreview.style.display = 'none';
                if (roomInfo) roomInfo.innerHTML = '';
                updateSubmitButton();

                // Both dates must be selected
                if (!checkIn || !checkOut) {
                    return;
                }

                // Validate dates
                if (new Date(checkOut) <= new Date(checkIn)) {
                    roomMessage.innerHTML = '<div class="alert alert-danger py-2 mb-0"><i class="bi bi-exclamation-triangle"></i> Check-out day must be greater than check-in day</div>';
                    return;
                }

                // Show loading
                roomMessage.innerHTML = '<div class="alert alert-secondary py-2 mb-0"><i class="bi bi-hourglass-split"></i> Checking empty rooms...</div>';

                // Build URL
                const apiUrl = contextPath + '/admin/api/available-rooms?checkIn=' + checkIn + '&checkOut=' + checkOut;

                // Fetch available rooms
                fetch(apiUrl)
                    .then(response => {
                        if (!response.ok) throw new Error('Network error');
                        return response.json();
                    })
                    .then(rooms => {
                        roomsData = {};

                        if (!rooms || rooms.length === 0) {
                            // No rooms available
                            roomSelect.innerHTML = '<option value="">-- There no empty room --</option>';
                            roomSelect.disabled = true;
                            roomMessage.innerHTML = '<div class="alert alert-warning py-2 mb-0"><i class="bi bi-exclamation-circle"></i> There are currently no empty rooms. PLease choose another day.</div>';
                        } else {
                            // Rooms available
                            roomSelect.innerHTML = '<option value="">-- Choose your room --</option>';
                            rooms.forEach(room => {
                                roomsData[room.roomId] = room;
                                const option = document.createElement('option');
                                option.value = room.roomId;
                                option.textContent = 'Room #' + room.roomId + ' - ' + room.roomType + ' - ' + room.formattedPrice + '/night';
                                option.setAttribute('data-price', room.roomPrice);
                                option.setAttribute('data-type', room.roomType);
                                option.setAttribute('data-slot', room.sleepSlot);
                                roomSelect.appendChild(option);
                            });
                            roomSelect.disabled = false;
                            roomMessage.innerHTML = '<div class="alert alert-success py-2 mb-0"><i class="bi bi-check-circle"></i> Have ' + rooms.length + ' empty rooms. Please choose your room.</div>';
                        }
                        updateSubmitButton();
                    })
                    .catch(error => {
                        roomMessage.innerHTML = '<div class="alert alert-danger py-2 mb-0"><i class="bi bi-x-circle"></i> Error when check empty room. Please try again.</div>';
                    });
            }

            function updateRoomInfo() {
                const select = document.getElementById('roomId');
                const option = select.options[select.selectedIndex];
                const roomInfo = document.getElementById('roomInfo');

                if (option && option.value) {
                    const roomType = option.getAttribute('data-type');
                    const sleepSlot = option.getAttribute('data-slot');
                    roomInfo.innerHTML = '<i class="bi bi-info-circle"></i> ' + roomType + ' room - Capacity: ' + sleepSlot + ' people';
                } else {
                    roomInfo.innerHTML = '';
                }
            }

            function calculateTotal() {
                const roomSelect = document.getElementById('roomId');
                const checkInInput = document.getElementById('checkInDate');
                const checkOutInput = document.getElementById('checkOutDate');
                const pricePreview = document.getElementById('pricePreview');
                const nightsEl = document.getElementById('nights');
                const totalPriceEl = document.getElementById('totalPrice');

                if (!roomSelect || !checkInInput || !checkOutInput || !pricePreview) return;

                const checkIn = checkInInput.value;
                const checkOut = checkOutInput.value;

                if (!roomSelect.value || !checkIn || !checkOut) {
                    pricePreview.style.display = 'none';
                    return;
                }

                const option = roomSelect.options[roomSelect.selectedIndex];
                if (!option) return;

                const pricePerNight = parseFloat(option.getAttribute('data-price'));

                const checkInDate = new Date(checkIn);
                const checkOutDate = new Date(checkOut);

                if (checkOutDate <= checkInDate) {
                    pricePreview.style.display = 'none';
                    return;
                }

                const nights = Math.ceil((checkOutDate - checkInDate) / (1000 * 60 * 60 * 24));
                const totalPrice = pricePerNight * nights;

                if (nightsEl) nightsEl.textContent = nights;
                if (totalPriceEl) totalPriceEl.textContent = totalPrice.toLocaleString('vi-VN') + ' VND';
                pricePreview.style.display = 'block';
            }

            function updateSubmitButton() {
                const roomSelect = document.getElementById('roomId');
                const checkIn = document.getElementById('checkInDate').value;
                const checkOut = document.getElementById('checkOutDate').value;
                const submitBtn = document.getElementById('submitBtn');

                if (checkIn && checkOut && roomSelect.value) {
                    submitBtn.disabled = false;
                } else {
                    submitBtn.disabled = true;
                }
            }

            // Form validation
            document.getElementById('bookingForm').addEventListener('submit', function (e) {
                const checkIn = new Date(document.getElementById('checkInDate').value);
                const checkOut = new Date(document.getElementById('checkOutDate').value);
                const roomId = document.getElementById('roomId').value;

                if (checkOut <= checkIn) {
                    e.preventDefault();
                    document.getElementById('roomMessage').innerHTML = '<div class="alert alert-danger py-2 mb-0"><i class="bi bi-exclamation-triangle"></i> Ngày check-out phải sau ngày check-in!</div>';
                    return false;
                }

                if (!roomId) {
                    e.preventDefault();
                    document.getElementById('roomMessage').innerHTML = '<div class="alert alert-danger py-2 mb-0"><i class="bi bi-exclamation-triangle"></i> Please choose your room!</div>';
                    return false;
                }
            });
        </script>