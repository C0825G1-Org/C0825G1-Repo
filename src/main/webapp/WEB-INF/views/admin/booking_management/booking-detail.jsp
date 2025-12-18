<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <!-- Page Header -->
        <div class="page-header">
            <h2><i class="bi bi-file-text"></i> Booking Details #${booking.bookingId}</h2>
            <p class="text-muted mb-0">Complete information about this booking</p>
        </div>

        <!-- Back Button -->
        <div class="mb-3">
            <a href="${pageContext.request.contextPath}/admin/bookings" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left"></i> Back to List
            </a>
        </div>

        <!-- Booking Details -->
        <div class="row">
            <!-- Booking Information Card -->
            <div class="col-md-8">
                <div class="card mb-3">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0"><i class="bi bi-calendar-check"></i> Booking Information</h5>
                    </div>
                    <div class="card-body">
                        <table class="table table-borderless">
                            <tr>
                                <th style="width: 200px;">Booking ID:</th>
                                <td><strong>#${booking.bookingId}</strong></td>
                            </tr>
                            <tr>
                                <th>Status:</th>
                                <td>
                                    <c:choose>
                                        <c:when test="${booking.status == 'PENDING'}">
                                            <span class="badge bg-warning text-dark fs-6">PENDING</span>
                                        </c:when>
                                        <c:when test="${booking.status == 'CONFIRMED'}">
                                            <span class="badge bg-success fs-6">CONFIRMED</span>
                                        </c:when>
                                        <c:when test="${booking.status == 'CANCELLED'}">
                                            <span class="badge bg-danger fs-6">CANCELLED</span>
                                        </c:when>
                                        <c:when test="${booking.status == 'COMPLETED'}">
                                            <span class="badge bg-info fs-6">COMPLETED</span>
                                        </c:when>
                                    </c:choose>
                                </td>
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
                            <tr>
                                <th>Total Price:</th>
                                <td>
                                    <h5 class="text-primary mb-0">${booking.formattedPrice}</h5>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>

                <!-- Room Information Card -->
                <div class="card">
                    <div class="card-header bg-info text-white">
                        <h5 class="mb-0"><i class="bi bi-door-open"></i> Room Information</h5>
                    </div>
                    <div class="card-body">
                        <c:if test="${room != null}">
                            <div class="row">
                                <div class="col-md-4">
                                    <c:if test="${not empty room.imageUrl}">
                                        <img src="${room.imageUrl}" class="img-fluid rounded" alt="Room image">
                                    </c:if>
                                </div>
                                <div class="col-md-8">
                                    <table class="table table-borderless">
                                        <tr>
                                            <th style="width: 150px;">Room ID:</th>
                                            <td><strong>#${room.roomId}</strong></td>
                                        </tr>
                                        <tr>
                                            <th>Room Type:</th>
                                            <td><span class="badge bg-info">${room.roomType}</span></td>
                                        </tr>
                                        <tr>
                                            <th>Capacity:</th>
                                            <td><i class="bi bi-people"></i> ${room.sleepSlot} people</td>
                                        </tr>
                                        <tr>
                                            <th>Price per Night:</th>
                                            <td>${room.formattedPrice}</td>
                                        </tr>
                                        <tr>
                                            <th>Description:</th>
                                            <td>${room.description}</td>
                                        </tr>
                                    </table>
                                </div>
                            </div>
                        </c:if>
                        <c:if test="${room == null}">
                            <div class="alert alert-warning">
                                <i class="bi bi-exclamation-triangle"></i> Room information not available
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>

            <!-- Guest Information & Actions Card -->
            <div class="col-md-4">
                <div class="card mb-3">
                    <div class="card-header bg-success text-white">
                        <h5 class="mb-0"><i class="bi bi-person"></i> Guest Information</h5>
                    </div>
                    <div class="card-body">
                        <div class="mb-3">
                            <label class="text-muted small">Guest Name</label>
                            <p class="mb-0"><strong>${booking.guestName}</strong></p>
                        </div>
                        <div class="mb-0">
                            <label class="text-muted small">Email Address</label>
                            <p class="mb-0">
                                <i class="bi bi-envelope"></i> ${booking.guestEmail}
                            </p>
                        </div>
                    </div>
                </div>

                <!-- Actions Card -->
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0"><i class="bi bi-gear"></i> Actions</h5>
                    </div>
                    <div class="card-body">
                        <div class="d-grid gap-2">
                            <!-- Print Invoice Button (always available) -->
                            <a href="${pageContext.request.contextPath}/admin/bookings?action=invoice&id=${booking.bookingId}"
                                class="btn btn-outline-primary w-100" target="_blank">
                                <i class="bi bi-printer"></i> Print Invoice
                            </a>

                            <!-- Edit Button (conditional - only if editable) -->
                            <c:if test="${booking.canBeEdited()}">
                                <a href="${pageContext.request.contextPath}/admin/bookings?action=edit&id=${booking.bookingId}"
                                    class="btn btn-warning w-100">
                                    <i class="bi bi-pencil-square"></i> Edit Booking
                                </a>
                            </c:if>

                            <!-- Approve Button -->
                            <c:if test="${booking.canBeApproved()}">
                                <form method="post" action="${pageContext.request.contextPath}/admin/bookings"
                                    style="margin-bottom: 0;">
                                    <input type="hidden" name="action" value="approve">
                                    <input type="hidden" name="id" value="${booking.bookingId}">
                                    <button type="submit" class="btn btn-success w-100"
                                        onclick="return confirm('Approve this booking?');">
                                        <i class="bi bi-check-circle"></i> Approve Booking
                                    </button>
                                </form>
                            </c:if>

                            <!-- Complete Button -->
                            <c:if test="${booking.status == 'CONFIRMED'}">
                                <form method="post" action="${pageContext.request.contextPath}/admin/bookings"
                                    style="margin-bottom: 0;">
                                    <input type="hidden" name="action" value="complete">
                                    <input type="hidden" name="id" value="${booking.bookingId}">
                                    <button type="submit" class="btn btn-primary w-100"
                                        onclick="return confirm('Mark this booking as completed?');">
                                        <i class="bi bi-check-all"></i> Mark as Completed
                                    </button>
                                </form>
                            </c:if>

                            <!-- Cancel Button -->
                            <c:if test="${booking.canBeCancelled()}">
                                <form method="post" action="${pageContext.request.contextPath}/admin/bookings"
                                    style="margin-bottom: 0;">
                                    <input type="hidden" name="action" value="cancel">
                                    <input type="hidden" name="id" value="${booking.bookingId}">
                                    <button type="submit" class="btn btn-danger w-100"
                                        onclick="return confirm('Cancel this booking?');">
                                        <i class="bi bi-x-circle"></i> Cancel Booking
                                    </button>
                                </form>
                            </c:if>

                            <!-- If already completed or cancelled -->
                            <c:if test="${booking.status == 'COMPLETED' || booking.status == 'CANCELLED'}">
                                <div class="alert alert-secondary mb-0">
                                    <i class="bi bi-info-circle"></i> No actions available for this booking
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>