<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Error - Homestay Booking</title>
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
            <link rel="stylesheet"
                href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
            <style>
                body {
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    min-height: 100vh;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                }

                .error-container {
                    max-width: 600px;
                    background: white;
                    padding: 40px;
                    border-radius: 15px;
                    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
                    text-align: center;
                }

                .error-icon {
                    font-size: 80px;
                    color: #dc3545;
                    margin-bottom: 20px;
                }

                .error-code {
                    font-size: 48px;
                    font-weight: bold;
                    color: #333;
                    margin-bottom: 10px;
                }

                .error-message {
                    font-size: 20px;
                    color: #666;
                    margin-bottom: 30px;
                }

                .error-details {
                    background: #f8f9fa;
                    padding: 20px;
                    border-radius: 8px;
                    margin-bottom: 20px;
                    text-align: left;
                }

                .error-details pre {
                    margin: 0;
                    white-space: pre-wrap;
                    word-wrap: break-word;
                    font-size: 12px;
                    color: #dc3545;
                }

                .btn-home {
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    border: none;
                    padding: 12px 30px;
                    font-size: 16px;
                }
            </style>
        </head>

        <body>
            <div class="error-container">
                <div class="error-icon">
                    <i class="bi bi-exclamation-triangle-fill"></i>
                </div>

                <div class="error-code">Oops!</div>

                <div class="error-message">
                    <c:choose>
                        <c:when test="${not empty errorMessage}">
                            ${errorMessage}
                        </c:when>
                        <c:otherwise>
                            Something went wrong. Please try again later.
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Show detailed error in development -->
                <c:if test="${not empty exception}">
                    <div class="error-details">
                        <h6 class="text-danger mb-2">
                            <i class="bi bi-bug"></i> Error Details (Development Mode):
                        </h6>
                        <pre>${exception.message}</pre>
                    </div>
                </c:if>

                <div class="d-grid gap-2">
                    <a href="${pageContext.request.contextPath}/admin/bookings" class="btn btn-primary btn-home">
                        <i class="bi bi-house-door"></i> Go to Booking List
                    </a>
                    <button onclick="history.back()" class="btn btn-outline-secondary">
                        <i class="bi bi-arrow-left"></i> Go Back
                    </button>
                </div>

                <div class="mt-4 text-muted small">
                    <p class="mb-0">If this problem persists, please contact the administrator.</p>
                </div>
            </div>

            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
        </body>

        </html>