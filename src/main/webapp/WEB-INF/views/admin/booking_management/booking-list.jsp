<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!-- Page Header -->
            <div class="page-header">
                <h2><i class="bi bi-calendar-check"></i> Booking Management</h2>
                <p class="text-muted mb-0">Manage all bookings in the system</p>
            </div>

            <!-- Success/Error Messages -->
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
            <!-- Action Bar -->
            <div class="d-flex justify-content-between align-items-center mb-3">
                <div>
                    <a href="${pageContext.request.contextPath}/admin/bookings?action=create" class="btn btn-primary">
                        <i class="bi bi-plus-circle"></i> Create New Booking
                    </a>
                </div>
                <div class="d-flex gap-2">
                    <!-- Filter by Status -->
                    <select class="form-select" style="width: 200px;" onchange="filterByStatus(this.value)">
                        <option value="">All Status</option>
                        <option value="PENDING">Pending</option>
                        <option value="CONFIRMED">Confirmed</option>
                        <option value="CANCELLED">Cancelled</option>
                        <option value="COMPLETED">Completed</option>
                        <option value="CANCELLED_REQUEST">Cancel Request</option>
                    </select>

                    <!-- Search -->
                    <div class="input-group" style="width: 300px;">
                        <input type="text" class="form-control" placeholder="Search by guest email..." id="searchInput">
                        <button class="btn btn-outline-secondary" type="button">
                            <i class="bi bi-search"></i>
                        </button>
                    </div>
                </div>
            </div>

            <!-- Statistics Cards -->
            <div class="row mb-4">
                <div class="col-md-3">
                    <div class="card bg-warning text-white">
                        <div class="card-body">
                            <h6 class="card-title">Pending</h6>
                            <h3 class="mb-0">
                                <c:set var="pendingCount" value="0" />
                                <c:forEach var="booking" items="${bookings}">
                                    <c:if test="${booking.status == 'PENDING'}">
                                        <c:set var="pendingCount" value="${pendingCount + 1}" />
                                    </c:if>
                                </c:forEach>
                                ${pendingCount}
                            </h3>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card bg-success text-white">
                        <div class="card-body">
                            <h6 class="card-title">Confirmed</h6>
                            <h3 class="mb-0">
                                <c:set var="confirmedCount" value="0" />
                                <c:forEach var="booking" items="${bookings}">
                                    <c:if test="${booking.status == 'CONFIRMED'}">
                                        <c:set var="confirmedCount" value="${confirmedCount + 1}" />
                                    </c:if>
                                </c:forEach>
                                ${confirmedCount}
                            </h3>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card bg-danger text-white">
                        <div class="card-body">
                            <h6 class="card-title">Cancelled</h6>
                            <h3 class="mb-0">
                                <c:set var="cancelledCount" value="0" />
                                <c:forEach var="booking" items="${bookings}">
                                    <c:if test="${booking.status == 'CANCELLED'}">
                                        <c:set var="cancelledCount" value="${cancelledCount + 1}" />
                                    </c:if>
                                </c:forEach>
                                ${cancelledCount}
                            </h3>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card bg-info text-white">
                        <div class="card-body">
                            <h6 class="card-title">Completed</h6>
                            <h3 class="mb-0">
                                <c:set var="completedCount" value="0" />
                                <c:forEach var="booking" items="${bookings}">
                                    <c:if test="${booking.status == 'COMPLETED'}">
                                        <c:set var="completedCount" value="${completedCount + 1}" />
                                    </c:if>
                                </c:forEach>
                                ${completedCount}
                            </h3>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Bookings Table -->
            <div class="card">
                <div class="card-body">
                    <c:if test="${empty bookings}">
                        <div class="alert alert-info">
                            <i class="bi bi-info-circle"></i> No bookings found in the system.
                        </div>
                    </c:if>

                    <c:if test="${not empty bookings}">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle" id="bookingsTable">
                                <thead class="table-light">
                                    <tr>
                                        <th>ID</th>
                                        <th>Guest Name</th>
                                        <th>Email</th>
                                        <th>Room ID</th>
                                        <th>Check-in</th>
                                        <th>Check-out</th>
                                        <th>Nights</th>
                                        <th>Total Price</th>
                                        <th>Status</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="booking" items="${bookings}">
                                        <tr>
                                            <td>#${booking.bookingId}</td>
                                            <td>${booking.guestName}</td>
                                            <td>${booking.guestEmail}</td>
                                            <td>Room #${booking.roomId}</td>
                                            <td>${booking.checkInDate}</td>
                                            <td>${booking.checkOutDate}</td>
                                            <td>${booking.numberOfNights} nights</td>
                                            <td>${booking.formattedPrice}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${booking.status == 'PENDING'}">
                                                        <span class="badge bg-warning text-dark">PENDING</span>
                                                    </c:when>
                                                    <c:when test="${booking.status == 'CONFIRMED'}">
                                                        <span class="badge bg-success">CONFIRMED</span>
                                                    </c:when>
                                                    <c:when test="${booking.status == 'CANCELLED'}">
                                                        <span class="badge bg-danger">CANCELLED</span>
                                                    </c:when>
                                                    <c:when test="${booking.status == 'COMPLETED'}">
                                                        <span class="badge bg-info">COMPLETED</span>
                                                    </c:when>
                                                    <c:when test="${booking.status == 'CANCELLED_REQUEST'}">
                                                        <span class="badge bg-secondary">CANCEL REQUEST</span>
                                                    </c:when>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div class="btn-group" role="group">
                                                    <a href="${pageContext.request.contextPath}/admin/bookings?action=detail&id=${booking.bookingId}"
                                                        class="btn btn-sm btn-outline-info" title="View Details">
                                                        <i class="bi bi-eye"></i>
                                                    </a>

                                                    <!-- Print Invoice Button (always available) -->
                                                    <a href="${pageContext.request.contextPath}/admin/bookings?action=invoice&id=${booking.bookingId}"
                                                        class="btn btn-sm btn-outline-secondary" title="Print Invoice"
                                                        target="_blank">
                                                        <i class="bi bi-printer"></i>
                                                    </a>

                                                    <!-- Show Approve button only for PENDING -->
                                                    <c:if test="${booking.canBeApproved()}">
                                                        <form method="post"
                                                            action="${pageContext.request.contextPath}/admin/bookings"
                                                            style="display: inline;">
                                                            <input type="hidden" name="action" value="approve">
                                                            <input type="hidden" name="id" value="${booking.bookingId}">
                                                            <button type="submit" class="btn btn-sm btn-outline-success"
                                                                title="Approve"
                                                                onclick="return confirm('Approve booking #${booking.bookingId}?');">
                                                                <i class="bi bi-check-circle"></i>
                                                            </button>
                                                        </form>
                                                    </c:if>

                                                    <!-- Show Cancel button for PENDING or CONFIRMED -->
                                                    <c:if test="${booking.canBeCancelled()}">
                                                        <form method="post"
                                                            action="${pageContext.request.contextPath}/admin/bookings"
                                                            style="display: inline;">
                                                            <input type="hidden" name="action" value="cancel">
                                                            <input type="hidden" name="id" value="${booking.bookingId}">
                                                            <button type="submit" class="btn btn-sm btn-outline-danger"
                                                                title="Cancel"
                                                                onclick="return confirm('Cancel booking #${booking.bookingId}?');">
                                                                <i class="bi bi-x-circle"></i>
                                                            </button>
                                                        </form>
                                                    </c:if>

                                                    <!-- Show Complete button only for CONFIRMED -->
                                                    <c:if test="${booking.status == 'CONFIRMED'}">
                                                        <form method="post"
                                                            action="${pageContext.request.contextPath}/admin/bookings"
                                                            style="display: inline;">
                                                            <input type="hidden" name="action" value="complete">
                                                            <input type="hidden" name="id" value="${booking.bookingId}">
                                                            <button type="submit" class="btn btn-sm btn-outline-primary"
                                                                title="Mark as Completed"
                                                                onclick="return confirm('Mark booking #${booking.bookingId} as completed?');">
                                                                <i class="bi bi-check-all"></i>
                                                            </button>
                                                        </form>
                                                    </c:if>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <!-- Pagination info -->
                        <div class="mt-3 text-muted">
                            Showing <strong>${bookings.size()}</strong> booking(s)
                        </div>
                    </c:if>
                </div>
            </div>

            <!-- JavaScript for filter and search -->
            <script>
                // Filter by status function
                function filterByStatus(status) {
                    const rows = document.querySelectorAll('#bookingsTable tbody tr');
                    rows.forEach(row => {
                        if (status === '' || row.textContent.includes(status)) {
                            row.style.display = '';
                        } else {
                            row.style.display = 'none';
                        }
                    });
                }

                // Search functionality
                document.getElementById('searchInput').addEventListener('input', function (e) {
                    const searchTerm = e.target.value.toLowerCase();
                    const rows = document.querySelectorAll('#bookingsTable tbody tr');

                    rows.forEach(row => {
                        const email = row.cells[2].textContent.toLowerCase();
                        const guestName = row.cells[1].textContent.toLowerCase();

                        if (email.includes(searchTerm) || guestName.includes(searchTerm)) {
                            row.style.display = '';
                        } else {
                            row.style.display = 'none';
                        }
                    });
                });
            </script>