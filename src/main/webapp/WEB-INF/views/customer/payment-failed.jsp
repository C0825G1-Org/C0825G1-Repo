<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="vi">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Thanh toán thất bại - Homestay Booking</title>
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
            <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
            <style>
                body {
                    background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%);
                    min-height: 100vh;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }

                .failed-card {
                    background: white;
                    border-radius: 20px;
                    padding: 40px;
                    text-align: center;
                    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
                    max-width: 500px;
                }

                .failed-icon {
                    width: 100px;
                    height: 100px;
                    background: linear-gradient(135deg, #ff5252, #f44336);
                    border-radius: 50%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    margin: 0 auto 20px;
                }

                .failed-icon i {
                    font-size: 50px;
                    color: white;
                }
            </style>
        </head>

        <body>
            <div class="failed-card">
                <div class="failed-icon">
                    <i class="bi bi-x-lg"></i>
                </div>

                <h2 class="text-danger mb-3">Thanh toán thất bại!</h2>

                <c:if test="${not empty sessionScope.errorMessage}">
                    <div class="alert alert-danger">
                        ${sessionScope.errorMessage}
                    </div>
                    <c:remove var="errorMessage" scope="session" />
                </c:if>

                <p class="text-muted mb-4">
                    Giao dịch không thành công. Vui lòng thử lại hoặc liên hệ hỗ trợ.
                </p>

                <div class="d-grid gap-2">
                    <a href="${pageContext.request.contextPath}/customer/booking" class="btn btn-primary btn-lg">
                        <i class="bi bi-arrow-repeat"></i> Thử lại
                    </a>
                    <a href="${pageContext.request.contextPath}/" class="btn btn-outline-secondary">
                        <i class="bi bi-house"></i> Về trang chủ
                    </a>
                </div>
            </div>
        </body>

        </html>