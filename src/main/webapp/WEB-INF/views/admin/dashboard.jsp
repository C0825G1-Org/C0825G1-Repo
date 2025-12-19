<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <div class="page-header">
                <h2><i class="bi bi-speedometer2"></i> Dashboard Overview</h2>
                <p class="text-muted mb-0">Quick insights into your homestay business</p>
            </div>

            <!-- KPI Cards Row -->
            <div class="row g-4 mb-4">
                <div class="col-md-3">
                    <div class="card h-100">
                        <div class="card-body text-center">
                            <i class="bi bi-calendar-check fs-1 text-primary mb-2"></i>
                            <h3 class="mb-1">${totalBookings}</h3>
                            <p class="text-muted mb-0">Total Bookings</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card h-100 border-warning">
                        <div class="card-body text-center">
                            <i class="bi bi-clock-history fs-1 text-warning mb-2"></i>
                            <h3 class="mb-1">${pendingBookings}</h3>
                            <p class="text-muted mb-0">Pending Approval</p>
                            <c:if test="${pendingBookings > 0}">
                                <a href="${pageContext.request.contextPath}/admin/bookings"
                                    class="btn btn-sm btn-outline-warning mt-2">
                                    Review Now
                                </a>
                            </c:if>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card h-100 border-success">
                        <div class="card-body text-center">
                            <i class="bi bi-check-circle fs-1 text-success mb-2"></i>
                            <h3 class="mb-1">${confirmedBookings}</h3>
                            <p class="text-muted mb-0">Confirmed</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card h-100 border-info">
                        <div class="card-body text-center">
                            <i class="bi bi-moon-stars fs-1 text-primary mb-2"></i>
                            <h3 class="mb-1">${avgStayDuration}</h3>
                            <p class="text-muted mb-0">Avg Stay (Nights)</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Charts Row 1: Revenue Line & Status Doughnut -->
            <div class="row g-4 mb-4">
                <div class="col-md-8">
                    <div class="card h-100">
                        <div class="card-header">
                            <h5 class="mb-0"><i class="bi bi-graph-up"></i> Revenue Over Time (Last 30 Days)</h5>
                        </div>
                        <div class="card-body">
                            <canvas id="revenueChart" height="80"></canvas>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card h-100">
                        <div class="card-header">
                            <h5 class="mb-0"><i class="bi bi-pie-chart"></i> Booking Status</h5>
                        </div>
                        <div class="card-body">
                            <canvas id="statusChart"></canvas>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Charts Row 2: Monthly Bar & Top Rooms Horizontal Bar -->
            <div class="row g-4 mb-4">
                <div class="col-md-6">
                    <div class="card h-100">
                        <div class="card-header">
                            <h5 class="mb-0"><i class="bi bi-bar-chart"></i> Monthly Booking Trends</h5>
                        </div>
                        <div class="card-body">
                            <canvas id="monthlyChart" height="100"></canvas>
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="card h-100">
                        <div class="card-header">
                            <h5 class="mb-0"><i class="bi bi-trophy"></i> Top Booked Rooms</h5>
                        </div>
                        <div class="card-body">
                            <canvas id="topRoomsChart" height="100"></canvas>
                        </div>
                    </div>
                </div>
            </div>

            <!-- AI Business Insight -->
            <div class="card mb-4">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="mb-0">
                        <i class="bi bi-lightbulb"></i> AI Business Insights (Beta)
                    </h5>
                    <form action="${pageContext.request.contextPath}/admin/dashboard" method="get"
                        style="display:inline;">
                        <input type="hidden" name="action" value="generateInsight">
                        <button type="submit" class="btn btn-sm btn-primary">
                            <i class="bi bi-stars"></i> Generate Insight
                        </button>
                    </form>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty sessionScope.aiInsight}">
                            <div class="alert alert-light border alert-permanent">
                                <small class="text-muted">
                                    <i class="bi bi-clock"></i> Generated:
                                    <fmt:formatDate value="${sessionScope.aiInsightTimestamp}"
                                        pattern="HH:mm:ss dd/MM/yyyy" type="both" />
                                </small>
                                <hr>
                                <div style="white-space: pre-wrap; line-height: 1.6;">
                                    ${sessionScope.aiInsight}
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center text-muted py-4">
                                <i class="bi bi-robot fs-1 mb-3"></i>
                                <p>Click "Generate Insight" to get AI-powered business analysis</p>
                                <small>Powered by Google Gemini Flash 2.5</small>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Recent Bookings -->
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="mb-0"><i class="bi bi-list-ul"></i> Recent Bookings</h5>
                    <a href="${pageContext.request.contextPath}/admin/bookings" class="btn btn-sm btn-outline-primary">
                        View All
                    </a>
                </div>
                <div class="card-body p-0">
                    <c:choose>
                        <c:when test="${empty recentBookings}">
                            <div class="text-center py-5 text-muted">
                                <i class="bi bi-inbox fs-1"></i>
                                <p class="mt-2">No bookings yet</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="table-responsive">
                                <table class="table table-hover mb-0">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Guest</th>
                                            <th>Room</th>
                                            <th>Check-in</th>
                                            <th>Status</th>
                                            <th>Price</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="booking" items="${recentBookings}">
                                            <tr>
                                                <td>#${booking.bookingId}</td>
                                                <td>${booking.guestName}</td>
                                                <td>Room #${booking.roomId}</td>
                                                <td>${booking.checkInDate}</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${booking.status == 'PENDING'}">
                                                            <span class="badge bg-warning">PENDING</span>
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
                                                <td>${booking.formattedTotalPrice}</td>
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/admin/bookings?action=detail&id=${booking.bookingId}"
                                                        class="btn btn-sm btn-outline-primary" title="View">
                                                        <i class="bi bi-eye"></i>
                                                    </a>
                                                    <c:if test="${booking.status.toString() == 'PENDING'}">
                                                        <form method="post"
                                                            action="${pageContext.request.contextPath}/admin/bookings"
                                                            style="display:inline;">
                                                            <input type="hidden" name="action" value="approve">
                                                            <input type="hidden" name="id" value="${booking.bookingId}">
                                                            <button type="submit" class="btn btn-sm btn-outline-success"
                                                                title="Approve">
                                                                <i class="bi bi-check"></i>
                                                            </button>
                                                        </form>
                                                    </c:if>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Chart.js -->
            <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>

            <script>
                // 1. Revenue Trend Line Chart
                const revenueDates = [];
                const revenueValues = [];
                <c:forEach var="data" items="${revenueTrend}">
                    revenueDates.push('${data.date}');
                    revenueValues.push(${data.revenue});
                </c:forEach>

                new Chart(document.getElementById('revenueChart'), {
                    type: 'line',
                    data: {
                        labels: revenueDates,
                        datasets: [{
                            label: 'Revenue',
                            data: revenueValues,
                            borderColor: '#9CAF88',
                            backgroundColor: 'rgba(156, 175, 136, 0.2)',
                            borderWidth: 3,
                            tension: 0.4,
                            fill: true
                        }]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: { display: false },
                            tooltip: {
                                callbacks: {
                                    label: ctx => 'Revenue: ' + ctx.parsed.y.toLocaleString('vi-VN') + ' VND'
                                }
                            }
                        },
                        scales: {
                            y: { beginAtZero: true }
                        }
                    }
                });

                // 2. Status Distribution Doughnut
                const statusLabels = [];
                const statusCounts = [];
                <c:forEach var="stat" items="${statusDistribution}">
                    statusLabels.push('${stat.status}');
                    statusCounts.push(${stat.count});
                </c:forEach>

                new Chart(document.getElementById('statusChart'), {
                    type: 'doughnut',
                    data: {
                        labels: statusLabels,
                        datasets: [{
                            data: statusCounts,
                            backgroundColor: ['#17A2B8', '#DC3545', '#6C757D', '#28A745', '#FFC107']
                        }]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: { position: 'bottom' }
                        }
                    }
                });

                // 3. Monthly Bookings Bar Chart
                const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
                const monthlyLabels = [];
                const monthlyCounts = [];
                <c:forEach var="mb" items="${monthlyBookings}">
                    monthlyLabels.push(monthNames[${mb.month} -1]);
                    monthlyCounts.push(${mb.count});
                </c:forEach>

                new Chart(document.getElementById('monthlyChart'), {
                    type: 'bar',
                    data: {
                        labels: monthlyLabels,
                        datasets: [{
                            label: 'Bookings',
                            data: monthlyCounts,
                            backgroundColor: '#9CAF88',
                            borderRadius: 8
                        }]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: { display: false }
                        },
                        scales: {
                            y: { beginAtZero: true }
                        }
                    }
                });

                // 4. Top Rooms Horizontal Bar
                const roomLabels = [];
                const roomCounts = [];
                <c:forEach var="room" items="${topRooms}">
                    roomLabels.push('Room #${room.roomId}');
                    roomCounts.push(${room.bookingCount});
                </c:forEach>

                new Chart(document.getElementById('topRoomsChart'), {
                    type: 'bar',
                    data: {
                        labels: roomLabels,
                        datasets: [{
                            label: 'Bookings',
                            data: roomCounts,
                            backgroundColor: '#8B7355',
                            borderRadius: 8
                        }]
                    },
                    options: {
                        indexAxis: 'y',
                        responsive: true,
                        plugins: {
                            legend: { display: false }
                        },
                        scales: {
                            x: { beginAtZero: true }
                        }
                    }
                });
            </script>