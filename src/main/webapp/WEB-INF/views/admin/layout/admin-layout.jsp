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

            <!-- Admin Theme CSS -->
            <style>
                /* Admin Color Scheme - Mocha & Nature Theme */
                :root {
                    --admin-cream: #FDF5E6;
                    --admin-mocha: #8B7355;
                    --admin-sage: #9CAF88;
                    --admin-dark-mocha: #5D4E37;
                    --admin-light-sage: #C8D5B9;
                }

                /* Body & Layout */
                body {
                    display: flex;
                    flex-direction: column;
                    min-height: 100vh;
                    background-color: var(--admin-cream) !important;
                }

                .main-wrapper {
                    display: flex;
                    flex: 1;
                }

                .content-area {
                    flex: 1;
                    padding: 2rem;
                    background-color: var(--admin-cream) !important;
                    overflow-y: auto;
                }

                /* Navbar */
                .navbar.admin-navbar {
                    background: linear-gradient(135deg, var(--admin-mocha), var(--admin-dark-mocha)) !important;
                }

                /* Sidebar */
                .admin-sidebar {
                    background-color: var(--admin-cream) !important;
                }

                .nav-link {
                    transition: all 0.3s ease;
                    color: var(--admin-dark-mocha) !important;
                }

                .nav-link:hover {
                    background-color: var(--admin-light-sage) !important;
                }

                .nav-link.active {
                    font-weight: 500;
                    background-color: var(--admin-sage) !important;
                    color: white !important;
                }

                /* Buttons - Override Bootstrap blue completely */
                .btn-primary,
                button.btn-primary,
                a.btn-primary {
                    background-color: var(--admin-sage) !important;
                    border-color: var(--admin-sage) !important;
                    color: white !important;
                }

                .btn-primary:hover,
                .btn-primary:focus,
                .btn-primary:active {
                    background-color: var(--admin-mocha) !important;
                    border-color: var(--admin-mocha) !important;
                    color: white !important;
                }

                /* Outline buttons - preserve icon visibility */
                .btn-outline-primary {
                    color: var(--admin-sage) !important;
                    border-color: var(--admin-sage) !important;
                    background-color: transparent !important;
                }

                .btn-outline-primary:hover {
                    background-color: var(--admin-sage) !important;
                    border-color: var(--admin-sage) !important;
                    color: white !important;
                }

                .btn-outline-secondary {
                    color: var(--admin-mocha) !important;
                    border-color: var(--admin-mocha) !important;
                }

                .btn-outline-secondary:hover {
                    background-color: var(--admin-mocha) !important;
                    color: white !important;
                }

                .btn-outline-info {
                    color: #17a2b8 !important;
                    border-color: #17a2b8 !important;
                }

                .btn-outline-info:hover {
                    background-color: #17a2b8 !important;
                    color: white !important;
                }

                .btn-outline-danger {
                    color: #dc3545 !important;
                    border-color: #dc3545 !important;
                }

                .btn-outline-danger:hover {
                    background-color: #dc3545 !important;
                    color: white !important;
                }

                .btn-outline-warning {
                    color: #ffc107 !important;
                    border-color: #ffc107 !important;
                }

                .btn-outline-warning:hover {
                    background-color: #ffc107 !important;
                    color: #000 !important;
                }

                /* Ensure icons are visible */
                .btn i,
                .btn .bi {
                    opacity: 1 !important;
                    visibility: visible !important;
                }

                /* Cards */
                .card {
                    border: none;
                    box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
                    background: white;
                    border-radius: 12px;
                }

                .card-header {
                    background-color: var(--admin-light-sage) !important;
                    color: var(--admin-dark-mocha) !important;
                    border-bottom: 2px solid var(--admin-sage) !important;
                }

                .page-header {
                    background: white;
                    padding: 1.5rem;
                    border-radius: 0.5rem;
                    margin-bottom: 2rem;
                    box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
                    border-left: 4px solid var(--admin-sage);
                }

                /* Tables */
                .table thead th {
                    background-color: var(--admin-sage) !important;
                    color: white !important;
                }

                /* Override Bootstrap color utilities */
                .bg-primary {
                    background-color: var(--admin-sage) !important;
                }

                .text-primary {
                    color: var(--admin-sage) !important;
                }

                .border-primary {
                    border-color: var(--admin-sage) !important;
                }

                /* Badges */
                .badge.bg-primary,
                .badge.bg-info {
                    background-color: var(--admin-sage) !important;
                }

                .badge.bg-secondary {
                    background-color: var(--admin-mocha) !important;
                }

                /* Forms */
                .form-control:focus,
                .form-select:focus {
                    border-color: var(--admin-sage) !important;
                    box-shadow: 0 0 0 0.2rem rgba(156, 175, 136, 0.25) !important;
                }

                /* Links */
                a {
                    color: var(--admin-mocha);
                }

                a:hover {
                    color: var(--admin-dark-mocha);
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