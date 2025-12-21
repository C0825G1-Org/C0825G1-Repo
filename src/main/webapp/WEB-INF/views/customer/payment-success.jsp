<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán thành công - Homestay Booking</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .success-card {
            background: white;
            border-radius: 20px;
            padding: 40px;
            text-align: center;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            max-width: 500px;
        }
        .success-icon {
            width: 100px;
            height: 100px;
            background: linear-gradient(135deg, #00c853, #69f0ae);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 20px;
            animation: pulse 2s infinite;
        }
        .success-icon i {
            font-size: 50px;
            color: white;
        }
        @keyframes pulse {
            0%, 100% { transform: scale(1); }
            50% { transform: scale(1.05); }
        }
    </style>
</head>
<body>
    <div class="success-card">
        <div class="success-icon">
            <i class="bi bi-check-lg"></i>
        </div>
        
        <h2 class="text-success mb-3">Thanh toán thành công!</h2>
        
        <c:if test="${not empty sessionScope.successMessage}">
            <p class="text-muted mb-4">${sessionScope.successMessage}</p>
            <c:remove var="successMessage" scope="session"/>
        </c:if>
        
        <c:if test="${not empty sessionScope.lastBookingId}">
            <div class="alert alert-info">
                <strong>Mã đặt phòng:</strong> #${sessionScope.lastBookingId}
            </div>
            <c:remove var="lastBookingId" scope="session"/>
        </c:if>
        
        <p class="mb-4">
            Cảm ơn bạn đã đặt phòng! Chúng tôi đã gửi email xác nhận đến địa chỉ của bạn.
        </p>
        
        <div class="d-grid gap-2">
            <a href="${pageContext.request.contextPath}/customer/my-bookings" class="btn btn-primary btn-lg">
                <i class="bi bi-calendar-check"></i> Xem đặt phòng của tôi
            </a>
            <a href="${pageContext.request.contextPath}/" class="btn btn-outline-secondary">
                <i class="bi bi-house"></i> Về trang chủ
            </a>
        </div>
    </div>
</body>
</html>
