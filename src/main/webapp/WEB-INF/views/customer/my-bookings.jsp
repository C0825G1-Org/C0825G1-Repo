<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>My Trips · Homestay</title>
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
                    min-height: 100vh;
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
                    font-size: 3rem;
                    color: var(--dark-mocha);
                    margin-bottom: 1rem;
                }

                .search-card {
                    background: white;
                    border-radius: 24px;
                    padding: 2rem;
                    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
                    margin-bottom: 3rem;
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

                .btn-search {
                    background: var(--sage);
                    color: white;
                    border: none;
                    padding: 0.8rem 2rem;
                    border-radius: 12px;
                    font-weight: 600;
                }

                .btn-search:hover {
                    background: var(--mocha);
                }

                .booking-card {
                    background: white;
                    border-radius: 20px;
                    padding: 1.5rem;
                    margin-bottom: 1.5rem;
                    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
                    transition: all 0.3s;
                    border-left: 4px solid var(--light-sage);
                }

                .booking-card:hover {
                    transform: translateX(8px);
                    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
                }

                .booking-card.status-confirmed {
                    border-left-color: var(--sage);
                }

                .booking-card.status-pending {
                    border-left-color: #FFA500;
                }

                .booking-card.status-cancelled {
                    border-left-color: #DC3545;
                }

                .status-badge {
                    padding: 0.5rem 1rem;
                    border-radius: 20px;
                    font-size: 0.85rem;
                    font-weight: 600;
                }

                .status-pending {
                    background: #FFF3CD;
                    color: #856404;
                }

                .status-confirmed {
                    background: #D1F2EB;
                    color: #0F5132;
                }

                .status-cancelled {
                    background: #F8D7DA;
                    color: #721C24;
                }

                .status-completed {
                    background: #CFE2FF;
                    color: #084298;
                }

                .status-cancelled-request {
                    background: #F8D7DA;
                    color: #721C24;
                }

                .btn-action {
                    padding: 0.5rem 1rem;
                    border-radius: 10px;
                    font-size: 0.9rem;
                    transition: all 0.3s;
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
                        <a class="nav-link" href="${pageContext.request.contextPath}/rooms">Discover</a>
                    </div>
                </div>
            </nav>

            <div class="container my-5">
                <h1 class="page-title">My Trips</h1>
                <p class="text-muted mb-4">Manage your bookings and upcoming stays</p>

                <!-- Alerts -->
                <c:if test="${not empty sessionScope.successMessage}">
                    <div class="alert alert-success">
                        <i class="bi bi-check-circle"></i> ${sessionScope.successMessage}
                    </div>
                    <c:remove var="successMessage" scope="session" />
                </c:if>

                <c:if test="${not empty sessionScope.errorMessage}">
                    <div class="alert alert-danger">
                        <i class="bi bi-exclamation-triangle"></i> ${sessionScope.errorMessage}
                    </div>
                    <c:remove var="errorMessage" scope="session" />
                </c:if>

                <!-- Search Card -->
                <div class="search-card">
                    <h5 class="mb-3"><i class="bi bi-search"></i> Find Your Bookings</h5>
                    <form method="get" action="${pageContext.request.contextPath}/my-bookings" class="row g-3">
                        <div class="col-md-8">
                            <input type="email" class="form-control" name="email" placeholder="Enter your email address"
                                value="${email}" required>
                        </div>
                        <div class="col-md-4">
                            <button type="submit" class="btn btn-search w-100">
                                <i class="bi bi-search"></i> Search
                            </button>
                        </div>
                    </form>
                </div>

                <!-- Bookings List -->
                <c:choose>
                    <c:when test="${empty bookings}">
                        <div class="text-center py-5">
                            <i class="bi bi-inbox fs-1 text-muted"></i>
                            <p class="mt-3 text-muted">No bookings found. Enter your email above to search.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <h5 class="mb-4">Your Bookings (${bookings.size()})</h5>

                        <c:forEach var="booking" items="${bookings}">
                            <div
                                class="booking-card status-${booking.status.toString().toLowerCase().replace('_', '-')}">
                                <div class="row align-items-center">
                                    <div class="col-md-3">
                                        <h6 class="mb-1 text-muted">Room</h6>
                                        <strong class="fs-5">Room #${booking.roomId}</strong>
                                    </div>
                                    <div class="col-md-2">
                                        <h6 class="mb-1 text-muted">Check-in</h6>
                                        <strong>${booking.checkInDate}</strong>
                                    </div>
                                    <div class="col-md-2">
                                        <h6 class="mb-1 text-muted">Check-out</h6>
                                        <strong>${booking.checkOutDate}</strong>
                                    </div>
                                    <div class="col-md-2">
                                        <h6 class="mb-1 text-muted">Total</h6>
                                        <strong class="text-success">${booking.formattedTotalPrice}</strong>
                                    </div>
                                    <div class="col-md-3 text-end">
                                        <div class="mb-2">
                                            <c:choose>
                                                <c:when test="${booking.status.toString() == 'PENDING'}">
                                                    <span class="status-badge status-pending">
                                                        <i class="bi bi-clock"></i> Pending
                                                    </span>
                                                </c:when>
                                                <c:when test="${booking.status.toString() == 'CONFIRMED'}">
                                                    <span class="status-badge status-confirmed">
                                                        <i class="bi bi-check-circle"></i> Confirmed
                                                    </span>
                                                </c:when>
                                                <c:when test="${booking.status.toString() == 'CANCELLED'}">
                                                    <span class="status-badge status-cancelled">
                                                        <i class="bi bi-x-circle"></i> Cancelled
                                                    </span>
                                                </c:when>
                                                <c:when test="${booking.status.toString() == 'COMPLETED'}">
                                                    <span class="status-badge status-completed">
                                                        <i class="bi bi-check-all"></i> Completed
                                                    </span>
                                                </c:when>
                                                <c:when test="${booking.status.toString() == 'CANCELLED_REQUEST'}">
                                                    <span class="status-badge status-cancelled-request">
                                                        <i class="bi bi-x-circle"></i> Cancel Pending
                                                    </span>
                                                </c:when>
                                            </c:choose>
                                        </div>

                                        <div class="d-flex gap-2 justify-content-end flex-wrap">
                                            <c:if test="${booking.canBeEdited()}">
                                                <a href="${pageContext.request.contextPath}/my-bookings?action=edit&id=${booking.bookingId}&email=${email}"
                                                    class="btn btn-sm btn-outline-primary btn-action">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                            </c:if>

                                            <c:if test="${booking.canChangeRoom()}">
                                                <a href="${pageContext.request.contextPath}/my-bookings?action=changeRoom&id=${booking.bookingId}&email=${email}"
                                                    class="btn btn-sm btn-outline-info btn-action">
                                                    <i class="bi bi-arrow-left-right"></i>
                                                </a>
                                            </c:if>

                                            <c:if
                                                test="${booking.status.toString() == 'PENDING' || booking.status.toString() == 'CONFIRMED'}">
                                                <form method="post"
                                                    action="${pageContext.request.contextPath}/my-bookings"
                                                    style="display: inline;">
                                                    <input type="hidden" name="action" value="cancel">
                                                    <input type="hidden" name="id" value="${booking.bookingId}">
                                                    <input type="hidden" name="email" value="${email}">
                                                    <button type="submit"
                                                        class="btn btn-sm btn-outline-danger btn-action"
                                                        onclick="return confirm('Request cancellation?');">
                                                        <i class="bi bi-x-lg"></i>
                                                    </button>
                                                </form>
                                            </c:if>

                                            <c:if test="${booking.status.toString() == 'CANCELLED_REQUEST'}">
                                                <small class="text-warning">
                                                    <i class="bi bi-clock"></i> Awaiting approval
                                                </small>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>

            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        </body>

        </html>