<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <!-- Page Header -->
        <div class="page-header">
            <h2><i class="bi bi-pencil-square"></i> Edit Room #${room.roomId}</h2>
            <p class="text-muted mb-0">Modify room information</p>
        </div>

        <!-- Back Button -->
        <div class="mb-3">
            <a href="${pageContext.request.contextPath}/admin/rooms" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left"></i> Back to List
            </a>
        </div>

        <!-- Alert Messages -->
        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle"></i> ${sessionScope.errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="errorMessage" scope="session" />
        </c:if>

        <!-- Edit Room Form -->
        <div class="card">
            <div class="card-body">
                <form method="post" action="${pageContext.request.contextPath}/admin/rooms">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="roomId" value="${room.roomId}">

                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="roomType" class="form-label">Room Type <span
                                        class="text-danger">*</span></label>
                                <select class="form-select" id="roomType" name="roomType" required>
                                    <option value="Family" ${room.roomType.toString()=='Family' ? 'selected' : '' }>
                                        Family</option>
                                    <option value="Business" ${room.roomType.toString()=='Business' ? 'selected' : '' }>
                                        Business</option>
                                    <option value="Honey_Moon" ${room.roomType.toString()=='Honey_Moon' ? 'selected'
                                        : '' }>Honey Moon</option>
                                </select>
                            </div>

                            <div class="mb-3">
                                <label for="sleepSlot" class="form-label">Capacity (People) <span
                                        class="text-danger">*</span></label>
                                <input type="number" class="form-control" id="sleepSlot" name="sleepSlot" required
                                    value="${room.sleepSlot}" min="1" max="10">
                            </div>

                            <div class="mb-3">
                                <label for="status" class="form-label">Status <span class="text-danger">*</span></label>
                                <select class="form-select" id="status" name="status" required>
                                    <option value="AVAILABLE" ${room.status.toString()=='AVAILABLE' ? 'selected' : '' }>
                                        Available</option>
                                    <option value="UNAVAILABLE" ${room.status.toString()=='UNAVAILABLE' ? 'selected'
                                        : '' }>Unavailable (Hidden)</option>
                                </select>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="roomPrice" class="form-label">Price per Night (VND) <span
                                        class="text-danger">*</span></label>
                                <input type="number" class="form-control" id="roomPrice" name="roomPrice" required
                                    value="${room.roomPrice}" min="1" step="any">
                                <small class="text-muted">Note: Price change only affects new bookings</small>
                            </div>

                            <div class="mb-3">
                                <label for="imageUrl" class="form-label">Image URL</label>
                                <input type="url" class="form-control" id="imageUrl" name="imageUrl"
                                    value="${room.imageUrl}" placeholder="https://example.com/room.jpg">
                            </div>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="description" class="form-label">Description <span
                                class="text-danger">*</span></label>
                        <textarea class="form-control" id="description" name="description" required
                            rows="4">${room.description}</textarea>
                    </div>

                    <hr>

                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-save"></i> Update Room
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/rooms" class="btn btn-secondary">
                            <i class="bi bi-x-circle"></i> Cancel
                        </a>
                    </div>
                </form>
            </div>
        </div>