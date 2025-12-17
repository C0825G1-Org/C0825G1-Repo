<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>${pageTitle != null ? pageTitle : 'Admin Panel'} - Homestay Booking</title>

            <!-- Bootstrap 5 CSS -->
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

            <!-- Bootstrap Icons -->
            <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">

            <!-- Custom CSS -->
            <style>
                body {
                    display: flex;
                    flex-direction: column;
                    min-height: 100vh;
                }

                .main-wrapper {
                    display: flex;
                    flex: 1;
                }

                .content-area {
                    flex: 1;
                    padding: 2rem;
                    background-color: #f8f9fa;
                    overflow-y: auto;
                }

                .nav-link {
                    transition: all 0.3s ease;
                }

                .nav-link:hover {
                    background-color: #e9ecef;
                }

                .nav-link.active {
                    font-weight: 500;
                }

                .card {
                    border: none;
                    box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
                }

                .page-header {
                    background: white;
                    padding: 1.5rem;
                    border-radius: 0.5rem;
                    margin-bottom: 2rem;
                    box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
                }
            </style>
        </head>

        <body>
            <!-- Header -->
            <jsp:include page="header.jsp" />

            <!-- Main Content Area -->
            <div class="main-wrapper">
                <!-- Sidebar -->
                <jsp:include page="sidebar.jsp" />

                <!-- Content -->
                <div class="content-area">
                    <!-- Page content will be inserted here by child pages -->
                    <jsp:include page="/WEB-INF/views/admin/${contentPage}" />
                </div>
            </div>

            <!-- Footer -->
            <jsp:include page="footer.jsp" />

            <!-- Bootstrap 5 JS Bundle -->
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

            <!-- Custom JS (if needed) -->
            <script>
                // Auto-hide alerts after 5 seconds
                document.addEventListener('DOMContentLoaded', function () {
                    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
                    alerts.forEach(function (alert) {
                        setTimeout(function () {
                            const bsAlert = new bootstrap.Alert(alert);
                            bsAlert.close();
                        }, 5000);
                    });
                });
            </script>
        </body>

        </html>