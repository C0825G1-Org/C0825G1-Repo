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
                <div>
                    <button class="btn btn-outline-secondary" type="button" data-bs-toggle="collapse"
                        data-bs-target="#advancedFilters" aria-expanded="false">
                        <i class="bi bi-funnel"></i> Advanced Filters
                    </button>
                </div>
            </div>

            <!-- Filter Form -->
            <form action="${pageContext.request.contextPath}/admin/bookings" method="get" id="filterForm">
                <!-- Basic Filters Row -->
                <div class="row mb-3 g-2">
                    <div class="col-md-3">
                        <select class="form-select" name="status" onchange="this.form.submit()">
                            <option value="">All Status</option>
                            <option value="PENDING" ${statusFilter=='PENDING' ? 'selected' : '' }>Pending</option>
                            <option value="CONFIRMED" ${statusFilter=='CONFIRMED' ? 'selected' : '' }>Confirmed</option>
                            <option value="CANCELLED" ${statusFilter=='CANCELLED' ? 'selected' : '' }>Cancelled</option>
                            <option value="COMPLETED" ${statusFilter=='COMPLETED' ? 'selected' : '' }>Completed</option>
                            <option value="CANCELLED_REQUEST" ${statusFilter=='CANCELLED_REQUEST' ? 'selected' : '' }>
                                Cancel Request</option>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <div class="input-group">
                            <input type="text" class="form-control" placeholder="Search by name or email..."
                                name="search" value="${searchKeyword}">
                            <button class="btn btn-outline-secondary" type="submit">
                                <i class="bi bi-search"></i>
                            </button>
                        </div>
                    </div>
                    <div class="col-md-2">
                        <c:if
                            test="${not empty statusFilter || not empty searchKeyword || not empty checkInFrom || not empty checkInTo || not empty checkOutFrom || not empty checkOutTo || not empty roomIdFilter}">
                            <a href="${pageContext.request.contextPath}/admin/bookings"
                                class="btn btn-outline-danger w-100">
                                <i class="bi bi-x-circle"></i> Clear All
                            </a>
                        </c:if>
                    </div>
                </div>

                <!-- Advanced Filters (Collapsible) -->
                <div class="collapse ${not empty checkInFrom || not empty checkInTo || not empty checkOutFrom || not empty checkOutTo || not empty roomIdFilter ? 'show' : ''}"
                    id="advancedFilters">
                    <div class="card card-body mb-3 bg-light">
                        <div class="row g-3">
                            <!-- Room Filter -->
                            <div class="col-md-4">
                                <label class="form-label fw-bold"><i class="bi bi-door-open"></i> Room</label>
                                <select class="form-select" name="roomId">
                                    <option value="">All Rooms</option>
                                    <c:forEach var="room" items="${allRooms}">
                                        <option value="${room.roomId}" ${roomIdFilter==String.valueOf(room.roomId)
                                            ? 'selected' : '' }>
                                            Room #${room.roomId} - ${room.roomType}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <!-- Check-in Date Range -->
                            <div class="col-md-4">
                                <label class="form-label fw-bold"><i class="bi bi-calendar-event"></i> Check-in
                                    Range</label>
                                <div class="row g-2">
                                    <div class="col-6">
                                        <input type="date" class="form-control" name="checkInFrom"
                                            value="${checkInFrom}">
                                        <small class="text-muted">From</small>
                                    </div>
                                    <div class="col-6">
                                        <input type="date" class="form-control" name="checkInTo" value="${checkInTo}">
                                        <small class="text-muted">To</small>
                                    </div>
                                </div>
                            </div>

                            <!-- Check-out Date Range -->
                            <div class="col-md-4">
                                <label class="form-label fw-bold"><i class="bi bi-calendar-check"></i> Check-out
                                    Range</label>
                                <div class="row g-2">
                                    <div class="col-6">
                                        <input type="date" class="form-control" name="checkOutFrom"
                                            value="${checkOutFrom}">
                                        <small class="text-muted">From</small>
                                    </div>
                                    <div class="col-6">
                                        <input type="date" class="form-control" name="checkOutTo" value="${checkOutTo}">
                                        <small class="text-muted">To</small>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="mt-3">
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-filter"></i> Apply Filters
                            </button>
                        </div>
                    </div>
                </div>
            </form>

            <!-- Statistics Cards (System-wide) -->
            <div class="row mb-4">
                <div class="col-md-2">
                    <div class="card bg-primary text-white">
                        <div class="card-body text-center">
                            <h6 class="card-title mb-1">Total</h6>
                            <h3 class="mb-0">${allBookingsCount}</h3>
                        </div>
                    </div>
                </div>
                <div class="col-md-2">
                    <div class="card bg-warning text-dark">
                        <div class="card-body text-center">
                            <h6 class="card-title mb-1">Pending</h6>
                            <h3 class="mb-0">${allPendingCount}</h3>
                        </div>
                    </div>
                </div>
                <div class="col-md-2">
                    <div class="card bg-success text-white">
                        <div class="card-body text-center">
                            <h6 class="card-title mb-1">Confirmed</h6>
                            <h3 class="mb-0">${allConfirmedCount}</h3>
                        </div>
                    </div>
                </div>
                <div class="col-md-2">
                    <div class="card bg-info text-white">
                        <div class="card-body text-center">
                            <h6 class="card-title mb-1">Completed</h6>
                            <h3 class="mb-0">${allCompletedCount}</h3>
                        </div>
                    </div>
                </div>
                <div class="col-md-2">
                    <div class="card bg-danger text-white">
                        <div class="card-body text-center">
                            <h6 class="card-title mb-1">Cancelled</h6>
                            <h3 class="mb-0">${allCancelledCount}</h3>
                        </div>
                    </div>
                </div>
                <div class="col-md-2">
                    <div class="card bg-secondary text-white">
                        <div class="card-body text-center">
                            <h6 class="card-title mb-1">Cancel Req</h6>
                            <h3 class="mb-0">${allCancelRequestCount}</h3>
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
                                                    <c:if test="${booking.status.toString() == 'CANCELLED_REQUEST'}">
                                                        <!-- Approve Cancel button -->
                                                        <form method="post"
                                                            action="${pageContext.request.contextPath}/admin/bookings"
                                                            style="display: inline;">
                                                            <input type="hidden" name="action"
                                                                value="approveCancelRequest">
                                                            <input type="hidden" name="id" value="${booking.bookingId}">
                                                            <button type="submit" class="btn btn-sm btn-outline-success"
                                                                title="Approve Cancellation">
                                                                <i class="bi bi-check-circle"></i>
                                                            </button>
                                                        </form>
                                                        <!-- Reject Cancel button -->
                                                        <form method="post"
                                                            action="${pageContext.request.contextPath}/admin/bookings"
                                                            style="display: inline;">
                                                            <input type="hidden" name="action"
                                                                value="rejectCancelRequest">
                                                            <input type="hidden" name="id" value="${booking.bookingId}">
                                                            <button type="submit" class="btn btn-sm btn-outline-danger"
                                                                title="Reject Cancellation">
                                                                <i class="bi bi-x-circle"></i>
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

                        <!-- Pagination -->
                        <c:set var="filterParams"
                            value="${not empty statusFilter ? '&status='.concat(statusFilter) : ''}${not empty searchKeyword ? '&search='.concat(searchKeyword) : ''}${not empty checkInFrom ? '&checkInFrom='.concat(checkInFrom) : ''}${not empty checkInTo ? '&checkInTo='.concat(checkInTo) : ''}${not empty checkOutFrom ? '&checkOutFrom='.concat(checkOutFrom) : ''}${not empty checkOutTo ? '&checkOutTo='.concat(checkOutTo) : ''}${not empty roomIdFilter ? '&roomId='.concat(roomIdFilter) : ''}" />

                        <div class="d-flex justify-content-between align-items-center mt-3">
                            <div class="text-muted">
                                Showing <strong>${(currentPage - 1) * pageSize + 1}</strong> -
                                <strong>${(currentPage - 1) * pageSize + bookings.size()}</strong>
                                of <strong>${totalBookings}</strong> bookings
                            </div>

                            <c:if test="${totalPages > 1}">
                                <nav aria-label="Page navigation">
                                    <ul class="pagination mb-0">
                                        <!-- Previous Button -->
                                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                            <a class="page-link"
                                                href="${pageContext.request.contextPath}/admin/bookings?page=${currentPage - 1}${filterParams}">
                                                <i class="bi bi-chevron-left"></i> Previous
                                            </a>
                                        </li>

                                        <!-- Page Numbers -->
                                        <c:forEach begin="1" end="${totalPages}" var="pageNum">
                                            <c:choose>
                                                <c:when
                                                    test="${totalPages <= 7 || pageNum == 1 || pageNum == totalPages || (pageNum >= currentPage - 2 && pageNum <= currentPage + 2)}">
                                                    <li class="page-item ${pageNum == currentPage ? 'active' : ''}">
                                                        <a class="page-link"
                                                            href="${pageContext.request.contextPath}/admin/bookings?page=${pageNum}${filterParams}">${pageNum}</a>
                                                    </li>
                                                </c:when>
                                                <c:when
                                                    test="${pageNum == currentPage - 3 || pageNum == currentPage + 3}">
                                                    <li class="page-item disabled">
                                                        <span class="page-link">...</span>
                                                    </li>
                                                </c:when>
                                            </c:choose>
                                        </c:forEach>

                                        <!-- Next Button -->
                                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                            <a class="page-link"
                                                href="${pageContext.request.contextPath}/admin/bookings?page=${currentPage + 1}${filterParams}">
                                                Next <i class="bi bi-chevron-right"></i>
                                            </a>
                                        </li>
                                    </ul>
                                </nav>
                            </c:if>
                        </div>
                    </c:if>
                </div>
            </div>

            <!-- Active Filters Summary -->
            <c:if
                test="${not empty statusFilter || not empty searchKeyword || not empty checkInFrom || not empty checkInTo || not empty checkOutFrom || not empty checkOutTo || not empty roomIdFilter}">
                <div class="alert alert-info mt-3 mb-0">
                    <i class="bi bi-funnel"></i>
                    <strong>Active Filters:</strong>
                    <c:if test="${not empty statusFilter}">
                        <span class="badge bg-primary ms-2">Status: ${statusFilter}</span>
                    </c:if>
                    <c:if test="${not empty searchKeyword}">
                        <span class="badge bg-secondary ms-2">Search: "${searchKeyword}"</span>
                    </c:if>
                    <c:if test="${not empty roomIdFilter}">
                        <span class="badge bg-info ms-2">Room: #${roomIdFilter}</span>
                    </c:if>
                    <c:if test="${not empty checkInFrom || not empty checkInTo}">
                        <span class="badge bg-success ms-2">Check-in: ${checkInFrom} - ${checkInTo}</span>
                    </c:if>
                    <c:if test="${not empty checkOutFrom || not empty checkOutTo}">
                        <span class="badge bg-warning text-dark ms-2">Check-out: ${checkOutFrom} - ${checkOutTo}</span>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/admin/bookings"
                        class="btn btn-sm btn-outline-danger ms-3">
                        <i class="bi bi-x-circle"></i> Clear All
                    </a>
                </div>
            </c:if>