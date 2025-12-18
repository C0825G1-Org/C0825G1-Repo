<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <!-- Page Header -->
        <div class="page-header">
            <h2><i class="bi bi-door-open"></i> Room Management</h2>
            <p class="text-muted mb-0">Manage all rooms in the system</p>
        </div>

        <!-- Action Buttons -->
        <div class="mb-3 d-flex justify-content-between align-items-center">
            <a href="${pageContext.request.contextPath}/admin/rooms?action=create" class="btn btn-primary">
                <i class="bi bi-plus-circle"></i> Add New Room
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

        <!-- Rooms Table -->
        <div class="card">
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Type</th>
                                <th>Capacity</th>
                                <th>Price/Night</th>
                                <th>Status</th>
                                <th>Description</th>
                                <th class="text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="room" items="${rooms}">
                                <tr>
                                    <td><strong>#${room.roomId}</strong></td>
                                    <td>
                                        <span class="badge bg-info">${room.roomType}</span>
                                    </td>
                                    <td><i class="bi bi-people"></i> ${room.sleepSlot} people</td>
                                    <td><strong>${room.formattedPrice}</strong></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${room.status.toString() == 'AVAILABLE'}">
                                                <span class="badge bg-success">AVAILABLE</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary">UNAVAILABLE</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-truncate" style="max-width: 200px;">${room.description}</td>
                                    <td>
                                        <div class="btn-group btn-group-sm" role="group">
                                            <!-- Edit Button -->
                                            <a href="${pageContext.request.contextPath}/admin/rooms?action=edit&id=${room.roomId}"
                                                class="btn btn-outline-warning" title="Edit">
                                                <i class="bi bi-pencil"></i>
                                            </a>

                                            <!-- Toggle Status Button -->
                                            <form method="post" action="${pageContext.request.contextPath}/admin/rooms"
                                                style="display: inline;">
                                                <input type="hidden" name="action" value="updateStatus">
                                                <input type="hidden" name="id" value="${room.roomId}">
                                                <input type="hidden" name="status"
                                                    value="${room.status.toString() == 'AVAILABLE' ? 'UNAVAILABLE' : 'AVAILABLE'}">
                                                <button type="submit" class="btn btn-outline-secondary"
                                                    title="Toggle to ${room.status.toString() == 'AVAILABLE' ? 'UNAVAILABLE' : 'AVAILABLE'}">
                                                    <i
                                                        class="bi ${room.status.toString() == 'AVAILABLE' ? 'bi-eye-slash' : 'bi-check-circle'}"></i>
                                                </button>
                                            </form>

                                            <!-- Delete Button -->
                                            <form method="post" action="${pageContext.request.contextPath}/admin/rooms"
                                                style="display: inline;">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="id" value="${room.roomId}">
                                                <button type="submit" class="btn btn-outline-danger" title="Delete"
                                                    onclick="return confirm('Delete room #${room.roomId}? This will fail if room has active bookings.');">
                                                    <i class="bi bi-trash"></i>
                                                </button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>

                            <c:if test="${empty rooms}">
                                <tr>
                                    <td colspan="7" class="text-center text-muted py-4">
                                        <i class="bi bi-inbox" style="font-size: 2rem;"></i>
                                        <p class="mb-0 mt-2">No rooms found. Add your first room!</p>
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>