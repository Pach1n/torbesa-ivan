<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session.getAttribute("sessionUser") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Torbesa - Gaming Platform</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        .hero-section {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 100px 0;
        }
        .game-card { transition: transform 0.3s ease-in-out, box-shadow 0.3s ease-in-out; }
        .game-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
        }
        .footer { background-color: #2c3e50; color: white; }
    </style>
</head>
<body>
    <jsp:include page="menu.jsp" />

    <!-- Hero Section -->
    <section class="hero-section text-center">
        <div class="container">
            <h1 class="display-1 fw-bold mb-4">
                <i class="bi bi-joystick"></i> Welcome to Torbesa
            </h1>
            <p class="lead fs-3">Your Gateway to Educational Gaming</p>
            <p class="lead fs-4">Learn to code & play our games</p>
            <p class="fs-5">Challenge yourself with fun and educational games</p>
            <p class="fs-6">Develop new games to learn web development</p>
        </div>
    </section>

    <div class="container my-4 d-flex justify-content-center">
        <a href="LogoutServlet" class="btn btn-danger">Logout</a>
    </div>

    <!-- Games Section -->
    <section class="py-5">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-lg-8">
                    <h2 class="text-center mb-5 display-6">
                        <i class="bi bi-collection-play"></i> Available Games
                    </h2>

                    <div class="row g-4">
                        <!-- Country Capital Game -->
                        <div class="col-md-6 col-lg-4">
                            <div class="card game-card shadow-sm border-0 h-100">
                                <div class="card-body text-center p-4">
                                    <div class="mb-3">
                                        <i class="bi bi-globe-americas text-primary" style="font-size: 3rem;"></i>
                                    </div>
                                    <h5 class="card-title text-primary mb-3">Country Capitals Game</h5>
                                    <p class="card-text text-muted mb-4">
                                        Test your geography knowledge! Guess the capital cities of countries from around the world.
                                    </p>
                                    <p class="card-text text-muted mb-4">By Rafael Aznar</p>
                                    <a href="../capitals/landing.jsp" class="btn btn-primary btn-lg w-100">
                                        <i class="bi bi-play-fill"></i> Play Now
                                    </a>
                                </div>
                            </div>
                        </div>

                        <!-- Trivia (API) -->
                        <div class="col-md-6 col-lg-4">
                            <div class="card game-card shadow-sm border-0 h-100">
                                <div class="card-body text-center p-4">
                                    <div class="mb-3">
                                        <i class="bi bi-question-circle text-warning" style="font-size: 3rem;"></i>
                                    </div>
                                    <h5 class="card-title text-warning mb-3">Trivia (API)</h5>
                                    <p class="card-text text-muted mb-4">
                                        Multiple-choice questions from Open Trivia DB. Can you pick the right answer?
                                    </p>
                                    <a href="<%= request.getContextPath() %>/trivia" class="btn btn-outline-warning btn-lg w-100">
                                        <i class="bi bi-play-fill"></i> Play Now
                                    </a>
                                </div>
                            </div>
                        </div>

                        <!-- Coming Soon Game 1 -->
                        <div class="col-md-6 col-lg-4">
                            <div class="card game-card shadow-sm border-0 h-100">
                                <div class="card-body text-center p-4">
                                    <div class="mb-3">
                                        <i class="bi bi-calculator text-warning" style="font-size: 3rem;"></i>
                                    </div>
                                    <h5 class="card-title text-warning mb-3">Math Challenge</h5>
                                    <p class="card-text text-muted mb-4">
                                        Sharpen your math skills with fun arithmetic challenges and number puzzles.
                                    </p>
                                    <button class="btn btn-outline-warning btn-lg w-100" disabled>
                                        <i class="bi bi-hourglass-split"></i> Coming Soon
                                    </button>
                                </div>
                            </div>
                        </div>

                        <!-- Coming Soon Game 2 -->
                        <div class="col-md-6 col-lg-4">
                            <div class="card game-card shadow-sm border-0 h-100">
                                <div class="card-body text-center p-4">
                                    <div class="mb-3">
                                        <i class="bi bi-translate text-success" style="font-size: 3rem;"></i>
                                    </div>
                                    <h5 class="card-title text-success mb-3">Language Quiz</h5>
                                    <p class="card-text text-muted mb-4">
                                        Expand your vocabulary and test your language skills across multiple languages.
                                    </p>
                                    <button class="btn btn-outline-success btn-lg w-100" disabled>
                                        <i class="bi bi-hourglass-split"></i> Coming Soon
                                    </button>
                                </div>
                            </div>
                        </div>

                        <!-- Coming Soon Game 3 -->
                        <div class="col-md-6 col-lg-4">
                            <div class="card game-card shadow-sm border-0 h-100">
                                <div class="card-body text-center p-4">
                                    <div class="mb-3">
                                        <i class="bi bi-lightning-charge text-danger" style="font-size: 3rem;"></i>
                                    </div>
                                    <h5 class="card-title text-danger mb-3">Science Trivia</h5>
                                    <p class="card-text text-muted mb-4">
                                        Discover the wonders of science through engaging trivia questions.
                                    </p>
                                    <button class="btn btn-outline-danger btn-lg w-100" disabled>
                                        <i class="bi bi-hourglass-split"></i> Coming Soon
                                    </button>
                                </div>
                            </div>
                        </div>

                        <!-- Coming Soon Game 4 -->
                        <div class="col-md-6 col-lg-4">
                            <div class="card game-card shadow-sm border-0 h-100">
                                <div class="card-body text-center p-4">
                                    <div class="mb-3">
                                        <i class="bi bi-clock-history text-info" style="font-size: 3rem;"></i>
                                    </div>
                                    <h5 class="card-title text-info mb-3">History Quest</h5>
                                    <p class="card-text text-muted mb-4">
                                        Journey through time and test your knowledge of historical events.
                                    </p>
                                    <button class="btn btn-outline-info btn-lg w-100" disabled>
                                        <i class="bi bi-hourglass-split"></i> Coming Soon
                                    </button>
                                </div>
                            </div>
                        </div>

                        <!-- Coming Soon Game 5 -->
                        <div class="col-md-6 col-lg-4">
                            <div class="card game-card shadow-sm border-0 h-100">
                                <div class="card-body text-center p-4">
                                    <div class="mb-3">
                                        <i class="bi bi-puzzle text-secondary" style="font-size: 3rem;"></i>
                                    </div>
                                    <h5 class="card-title text-secondary mb-3">Logic Puzzles</h5>
                                    <p class="card-text text-muted mb-4">
                                        Challenge your mind with brain-teasing logic puzzles and riddles.
                                    </p>
                                    <button class="btn btn-outline-secondary btn-lg w-100" disabled>
                                        <i class="bi bi-hourglass-split"></i> Coming Soon
                                    </button>
                                </div>
                            </div>
                        </div>

                    </div><!-- /.row g-4 -->
                </div>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer class="footer py-4 mt-5">
        <div class="container">
            <div class="row">
                <div class="col-md-8">
                    <p class="mb-0">
                        <i class="bi bi-c-circle"></i> 2024 torbesa. All rights reserved.
                    </p>
                </div>
                <div class="col-md-4 text-md-end">
                    <p class="mb-0">
                        <i class="bi bi-code-slash"></i> Licensed under
                        <a href="https://opensource.org/licenses/MIT" class="text-white text-decoration-none">MIT License</a>
                    </p>
                </div>
            </div>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>