<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Homestay · Where Vibes Meet Comfort</title>
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
            <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
            <link rel="preconnect" href="https://fonts.googleapis.com">
            <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
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

                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                }

                body {
                    font-family: 'Inter', sans-serif;
                    background: var(--cream);
                    color: var(--dark-mocha);
                    overflow-x: hidden;
                }

                /* Hero Section */
                .hero {
                    min-height: 90vh;
                    background: linear-gradient(135deg, var(--sage) 0%, var(--mocha) 100%);
                    position: relative;
                    overflow: hidden;
                    display: flex;
                    align-items: center;
                }

                .hero::before {
                    content: '';
                    position: absolute;
                    top: 0;
                    left: 0;
                    right: 0;
                    bottom: 0;
                    background: url('https://images.unsplash.com/photo-1566073771259-6a8506099945?w=1600') center/cover;
                    opacity: 0.15;
                    mix-blend-mode: overlay;
                }

                .hero-content {
                    position: relative;
                    z-index: 2;
                }

                .hero h1 {
                    font-family: 'Playfair Display', serif;
                    font-size: clamp(3rem, 8vw, 7rem);
                    font-weight: 700;
                    line-height: 1.1;
                    color: white;
                    text-shadow: 2px 4px 12px rgba(0, 0, 0, 0.2);
                }

                .hero-subtitle {
                    font-size: 1.5rem;
                    color: rgba(255, 255, 255, 0.9);
                    font-weight: 300;
                    letter-spacing: 2px;
                }

                .btn-hero {
                    background: white;
                    color: var(--dark-mocha);
                    border: none;
                    padding: 1rem 2.5rem;
                    font-size: 1.1rem;
                    font-weight: 600;
                    border-radius: 50px;
                    transition: all 0.3s ease;
                    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
                }

                .btn-hero:hover {
                    transform: translateY(-3px);
                    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.25);
                    background: var(--cream);
                }

                /* Floating Stickers */
                .sticker {
                    position: absolute;
                    animation: float 3s ease-in-out infinite;
                }

                .sticker-1 {
                    top: 10%;
                    right: 10%;
                    animation-delay: 0s;
                }

                .sticker-2 {
                    bottom: 20%;
                    left: 15%;
                    animation-delay: 1s;
                }

                @keyframes float {

                    0%,
                    100% {
                        transform: translateY(0px) rotate(0deg);
                    }

                    50% {
                        transform: translateY(-20px) rotate(5deg);
                    }
                }

                /* Navbar */
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

                .nav-link {
                    color: var(--mocha) !important;
                    font-weight: 500;
                    transition: color 0.3s;
                }

                .nav-link:hover {
                    color: var(--sage) !important;
                }

                /* Bento Grid */
                .bento-section {
                    padding: 5rem 0;
                }

                .section-title {
                    font-family: 'Playfair Display', serif;
                    font-size: 3rem;
                    color: var(--dark-mocha);
                    margin-bottom: 3rem;
                    text-align: center;
                }

                .bento-card {
                    border-radius: 24px;
                    overflow: hidden;
                    position: relative;
                    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
                    cursor: pointer;
                    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
                }

                .bento-card:hover {
                    transform: scale(1.03) translateY(-8px);
                    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
                }

                .bento-card img {
                    width: 100%;
                    height: 100%;
                    object-fit: cover;
                    transition: transform 0.4s ease;
                }

                .bento-card:hover img {
                    transform: scale(1.1);
                }

                .bento-overlay {
                    position: absolute;
                    bottom: 0;
                    left: 0;
                    right: 0;
                    background: linear-gradient(to top, rgba(0, 0, 0, 0.8), transparent);
                    padding: 2rem;
                    transform: translateY(100%);
                    transition: transform 0.4s ease;
                }

                .bento-card:hover .bento-overlay {
                    transform: translateY(0);
                }

                .bento-title {
                    font-family: 'Playfair Display', serif;
                    font-size: 1.8rem;
                    color: white;
                    margin-bottom: 0.5rem;
                }

                .bento-price {
                    color: var(--light-sage);
                    font-size: 1.2rem;
                    font-weight: 600;
                }

                .bento-type {
                    display: inline-block;
                    background: rgba(255, 255, 255, 0.2);
                    backdrop-filter: blur(10px);
                    padding: 0.3rem 1rem;
                    border-radius: 20px;
                    color: white;
                    font-size: 0.9rem;
                    margin-top: 0.5rem;
                }

                /* Large Bento */
                .bento-large {
                    min-height: 500px;
                }

                /* Medium Bento */
                .bento-medium {
                    min-height: 350px;
                }

                /* Small Bento */
                .bento-small {
                    min-height: 250px;
                }

                /* Filter Section */
                .filter-bar {
                    background: white;
                    padding: 2rem;
                    border-radius: 20px;
                    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
                    margin-bottom: 3rem;
                }

                .filter-bar select,
                .filter-bar input {
                    border: 2px solid var(--light-sage);
                    border-radius: 12px;
                    padding: 0.8rem;
                    font-size: 1rem;
                }

                .filter-bar select:focus,
                .filter-bar input:focus {
                    border-color: var(--sage);
                    box-shadow: 0 0 0 3px rgba(156, 175, 136, 0.1);
                }

                .btn-filter {
                    background: var(--sage);
                    color: white;
                    border: none;
                    padding: 0.8rem 2rem;
                    border-radius: 12px;
                    font-weight: 600;
                    transition: all 0.3s;
                }

                .btn-filter:hover {
                    background: var(--mocha);
                    transform: translateY(-2px);
                }

                /* Scroll Animations */
                .fade-in {
                    opacity: 0;
                    transform: translateY(30px);
                    transition: opacity 0.6s ease, transform 0.6s ease;
                }

                .fade-in.visible {
                    opacity: 1;
                    transform: translateY(0);
                }

                /* Footer */
                .footer {
                    background: var(--dark-mocha);
                    color: white;
                    padding: 3rem 0;
                    margin-top: 5rem;
                }
            </style>
        </head>

        <body>
            <!-- Navbar -->
            <nav class="navbar navbar-expand-lg sticky-top">
                <div class="container">
                    <a class="navbar-brand" href="${pageContext.request.contextPath}/rooms">
                        <i class="bi bi-tree"></i> Homestay
                    </a>
                    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                        <span class="navbar-toggler-icon"></span>
                    </button>
                    <div class="collapse navbar-collapse justify-content-end" id="navbarNav">
                        <ul class="navbar-nav gap-3">
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/rooms">Discover</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/my-bookings">My Trips</a>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>

            <!-- Hero Section -->
            <section class="hero">
                <div class="container">
                    <div class="row align-items-center">
                        <div class="col-lg-8">
                            <div class="hero-content">
                                <p class="hero-subtitle mb-3">Escape • Explore • Experience</p>
                                <h1 class="mb-4">Stay <br>Somewhere<br>Different</h1>
                                <p class="text-white fs-5 mb-5" style="max-width: 500px; opacity: 0.9;">
                                    Cozy homestays where every corner is Instagram-worthy
                                </p>
                                <a href="#rooms" class="btn btn-hero">
                                    Explore Stays <i class="bi bi-arrow-right ms-2"></i>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Floating Stickers -->
                <div class="sticker sticker-1" style="width: 80px; height: 80px;">
                    <svg viewBox="0 0 100 100" fill="none">
                        <circle cx="50" cy="50" r="40" fill="white" opacity="0.8" />
                        <text x="50" y="60" text-anchor="middle" font-size="30">☀️</text>
                    </svg>
                </div>
                <div class="sticker sticker-2" style="width: 100px; height: 100px;">
                    <svg viewBox="0 0 100 100" fill="none">
                        <circle cx="50" cy="50" r="45" fill="white" opacity="0.8" />
                        <text x="50" y="65" text-anchor="middle" font-size="18" fill="#5D4E37">Stay Chill</text>
                    </svg>
                </div>
            </section>

            <!-- Filter Bar -->
            <section class="container mt-5" id="rooms">
                <div class="filter-bar fade-in">
                    <form method="get" action="${pageContext.request.contextPath}/rooms"
                        class="row g-3 align-items-end">
                        <div class="col-md-3">
                            <label class="form-label fw-semibold">Room Vibe</label>
                            <select name="type" class="form-select">
                                <option value="">All Vibes</option>
                                <option value="Family" ${type=='Family' ? 'selected' : '' }>Family</option>
                                <option value="Business" ${type=='Business' ? 'selected' : '' }>Business</option>
                                <option value="Honey_Moon" ${type=='Honey_Moon' ? 'selected' : '' }>Honey Moon</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label fw-semibold">Min Budget</label>
                            <input type="number" name="minPrice" class="form-control" placeholder="500,000 VND"
                                value="${minPrice}">
                        </div>
                        <div class="col-md-3">
                            <label class="form-label fw-semibold">Max Budget</label>
                            <input type="number" name="maxPrice" class="form-control" placeholder="2,000,000 VND"
                                value="${maxPrice}">
                        </div>
                        <div class="col-md-3">
                            <button type="submit" class="btn btn-filter w-100">
                                <i class="bi bi-search"></i> Find Stays
                            </button>
                        </div>
                    </form>
                </div>
            </section>

            <!-- Bento Grid Rooms -->
            <section class="bento-section">
                <div class="container">
                    <h2 class="section-title fade-in">Curated Spaces</h2>

                    <c:choose>
                        <c:when test="${empty rooms}">
                            <div class="text-center py-5">
                                <i class="bi bi-inbox fs-1 text-muted"></i>
                                <p class="mt-3 text-muted">No rooms match your vibe. Try different filters!</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="row g-4">
                                <c:forEach var="room" items="${rooms}" varStatus="status">
                                    <c:choose>
                                        <%-- First room: Large Bento --%>
                                            <c:when test="${status.index == 0}">
                                                <div class="col-md-8">
                                                    <a href="${pageContext.request.contextPath}/rooms?action=detail&id=${room.roomId}"
                                                        class="text-decoration-none">
                                                        <div class="bento-card bento-large fade-in">
                                                            <img src="${not empty room.imageUrl ? room.imageUrl : 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800'}"
                                                                alt="${room.roomType}">
                                                            <div class="bento-overlay">
                                                                <h3 class="bento-title">Room #${room.roomId}</h3>
                                                                <p class="bento-price">${room.formattedPrice}/night</p>
                                                                <span class="bento-type">${room.roomType}</span>
                                                            </div>
                                                        </div>
                                                    </a>
                                                </div>
                                            </c:when>

                                            <%-- Second room: Medium Bento --%>
                                                <c:when test="${status.index == 1}">
                                                    <div class="col-md-4">
                                                        <a href="${pageContext.request.contextPath}/rooms?action=detail&id=${room.roomId}"
                                                            class="text-decoration-none">
                                                            <div class="bento-card bento-medium fade-in">
                                                                <img src="${not empty room.imageUrl ? room.imageUrl : 'https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=600'}"
                                                                    alt="${room.roomType}">
                                                                <div class="bento-overlay">
                                                                    <h3 class="bento-title">Room #${room.roomId}</h3>
                                                                    <p class="bento-price">${room.formattedPrice}/night
                                                                    </p>
                                                                    <span class="bento-type">${room.roomType}</span>
                                                                </div>
                                                            </div>
                                                        </a>
                                                    </div>
                                                </c:when>

                                                <%-- Remaining rooms: Alternate sizes --%>
                                                    <c:otherwise>
                                                        <c:choose>
                                                            <c:when test="${status.index % 3 == 0}">
                                                                <div class="col-md-4">
                                                                    <a href="${pageContext.request.contextPath}/rooms?action=detail&id=${room.roomId}"
                                                                        class="text-decoration-none">
                                                                        <div class="bento-card bento-small fade-in">
                                                                            <img src="${not empty room.imageUrl ? room.imageUrl : 'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=600'}"
                                                                                alt="${room.roomType}">
                                                                            <div class="bento-overlay">
                                                                                <h3 class="bento-title">Room
                                                                                    #${room.roomId}</h3>
                                                                                <p class="bento-price">
                                                                                    ${room.formattedPrice}/night</p>
                                                                                <span
                                                                                    class="bento-type">${room.roomType}</span>
                                                                            </div>
                                                                        </div>
                                                                    </a>
                                                                </div>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <div class="col-md-8">
                                                                    <a href="${pageContext.request.contextPath}/rooms?action=detail&id=${room.roomId}"
                                                                        class="text-decoration-none">
                                                                        <div class="bento-card bento-medium fade-in">
                                                                            <img src="${not empty room.imageUrl ? room.imageUrl : 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=800'}"
                                                                                alt="${room.roomType}">
                                                                            <div class="bento-overlay">
                                                                                <h3 class="bento-title">Room
                                                                                    #${room.roomId}</h3>
                                                                                <p class="bento-price">
                                                                                    ${room.formattedPrice}/night</p>
                                                                                <span
                                                                                    class="bento-type">${room.roomType}</span>
                                                                            </div>
                                                                        </div>
                                                                    </a>
                                                                </div>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>

            <!-- Footer -->
            <footer class="footer">
                <div class="container text-center">
                    <p class="mb-0">&copy; 2025 Homestay · Where Vibes Meet Comfort</p>
                    <p class="mt-2 opacity-75">Made with ☕ for Gen Z travelers</p>
                </div>
            </footer>

            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
            <script>
                // Scroll Animation
                const observerOptions = {
                    threshold: 0.1,
                    rootMargin: '0px 0px -50px 0px'
                };

                const observer = new IntersectionObserver((entries) => {
                    entries.forEach(entry => {
                        if (entry.isIntersecting) {
                            entry.target.classList.add('visible');
                        }
                    });
                }, observerOptions);

                document.querySelectorAll('.fade-in').forEach(el => observer.observe(el));

                // Smooth scroll
                document.querySelectorAll('a[href^="#"]').forEach(anchor => {
                    anchor.addEventListener('click', function (e) {
                        e.preventDefault();
                        const target = document.querySelector(this.getAttribute('href'));
                        if (target) {
                            target.scrollIntoView({ behavior: 'smooth', block: 'start' });
                        }
                    });
                });
            </script>
        </body>

        </html>