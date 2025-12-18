<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <div class="admin-sidebar border-end vh-100 sticky-top" style="width: 250px;">
            <div class="p-3">
                <h6 class="text-muted text-uppercase small mb-3">Management</h6>
                <ul class="nav flex-column">
                    <li class="nav-item mb-2">
                        <a class="nav-link ${pageContext.request.requestURI.contains('/admin/rooms') ? 'active' : ''} rounded"
                            href="${pageContext.request.contextPath}/admin/rooms">
                            <i class="bi bi-door-open"></i> Room Management
                        </a>
                    </li>
                    <li class="nav-item mb-2">
                        <a class="nav-link ${pageContext.request.requestURI.contains('/admin/bookings') ? 'active' : ''} rounded"
                            href="${pageContext.request.contextPath}/admin/bookings">
                            <i class="bi bi-calendar-check"></i> Booking Management
                        </a>
                    </li>
                </ul>

                <hr class="my-3">

                <h6 class="text-muted text-uppercase small mb-3">Statistics</h6>
                <ul class="nav flex-column">
                    <li class="nav-item mb-2">
                        <a class="nav-link text-dark rounded" href="#">
                            <i class="bi bi-bar-chart"></i> Dashboard
                            <span class="badge bg-secondary ms-2">Soon</span>
                        </a>
                    </li>
                    <li class="nav-item mb-2">
                        <a class="nav-link text-dark rounded" href="#">
                            <i class="bi bi-graph-up"></i> Reports
                            <span class="badge bg-secondary ms-2">Soon</span>
                        </a>
                    </li>
                </ul>
            </div>
        </div>