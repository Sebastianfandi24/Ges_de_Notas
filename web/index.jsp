<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Registro Académico</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css">
    <style>
        body {
            font-family: Arial, sans-serif;
            scroll-behavior: smooth;
            background-color: #f8f9fa;
        }
        .navbar {
            background-color: white;
            box-shadow: 0px 2px 10px rgba(0, 0, 0, 0.1);
        }
        .navbar-brand {
            font-weight: bold;
            display: flex;
            align-items: center;
        }
        .navbar-brand i {
            font-size: 24px;
            margin-right: 8px;
            color: #0d6efd;
        }
        .btn-primary {
            background-color: #1b4f72;
            border: none;
        }
        .btn-primary:hover {
            background-color: #154360;
        }
        .btn-outline-dark:hover {
            background-color: #0d6efd;
            color: white;
        }
        .feature-box, .benefit-box, .testimonial-box {
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            background: #fff;
            margin-bottom: 20px;
        }
        .feature-icon, .benefit-icon {
            font-size: 30px;
            margin-bottom: 10px;
            color: #0d6efd;
        }
    </style>
</head>
<body>

    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-light fixed-top">
        <div class="container">
            <a class="navbar-brand" href="#">
                <i class="bi bi-mortarboard"></i> Registro Académico
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse justify-content-center" id="navbarNav">
                <ul class="navbar-nav">
                    <li class="nav-item"><a class="nav-link fw-semibold" href="#caracteristicas">Características</a></li>
                    <li class="nav-item"><a class="nav-link fw-semibold" href="#beneficios">Beneficios</a></li>
                    <li class="nav-item"><a class="nav-link fw-semibold" href="#testimonios">Testimonios</a></li>
                </ul>
            </div>
            <div>
                <a class="btn btn-outline-dark me-2" href="login.jsp"><i class="bi bi-box-arrow-in-right"></i> Iniciar Sesión</a>
            </div>
        </div>
    </nav>

    <!-- Hero Section -->
    <div class="hero bg-light text-center py-5" style="margin-top: 80px;">
        <h1 class="fw-bold text-primary">Sistema de Registro de Actividades Académicas</h1>
        <p class="text-secondary">Gestiona cursos, actividades y calificaciones de manera eficiente con nuestra plataforma educativa integral.</p>
        <a href="login.jsp" class="btn btn-outline-dark"><i class="bi bi-box-arrow-in-right"></i> Iniciar Sesión</a>
    </div>

    <!-- Características -->
    <div id="caracteristicas" class="container my-5">
        <h2 class="text-center mb-4 fw-bold text-primary">Características Principales</h2>
        <div class="row">
            <div class="col-md-4">
                <div class="feature-box text-center">
                    <i class="bi bi-book feature-icon"></i>
                    <h4>Gestión de Cursos</h4>
                    <p>Crea y administra cursos con facilidad, asignando profesores y estudiantes.</p>
                </div>
            </div>
            <div class="col-md-4">
                <div class="feature-box text-center">
                    <i class="bi bi-clipboard-check feature-icon"></i>
                    <h4>Actividades Académicas</h4>
                    <p>Programa y gestiona diferentes tipos de actividades para cada curso.</p>
                </div>
            </div>
            <div class="col-md-4">
                <div class="feature-box text-center">
                    <i class="bi bi-bar-chart feature-icon"></i>
                    <h4>Calificaciones</h4>
                    <p>Registro de calificaciones por actividad y cálculo automático de promedios.</p>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Beneficios -->
    <div id="beneficios" class="container my-5">
        <h2 class="text-center mb-4 fw-bold text-primary">Beneficios</h2>
        <div class="row">
            <div class="col-md-6">
                <div class="benefit-box text-center">
                    <i class="bi bi-clock benefit-icon"></i>
                    <h4>Ahorro de Tiempo</h4>
                    <p>Automatiza procesos administrativos y reduce el tiempo dedicado a la gestión académica.</p>
                </div>
            </div>
            <div class="col-md-6">
                <div class="benefit-box text-center">
                    <i class="bi bi-graph-up benefit-icon"></i>
                    <h4>Mejora del Rendimiento</h4>
                    <p>Facilita el seguimiento del progreso académico con datos detallados.</p>
                </div>
            </div>
        </div>
    </div>
    
     <!-- Testimonios -->
    <div id="testimonios" class="container my-5">
        <h2 class="text-center mb-4 fw-bold text-primary">Testimonios</h2>
        <div class="row">
            <div class="col-md-4">
                <div class="testimonial-box text-center p-3">
                    <h5>Maria Rodríguez</h5>
                    <p>"El sistema de gestión nos ahorra mucho tiempo y nos permite enfocarnos más en la enseñanza."</p>
                </div>
            </div>
            <div class="col-md-4">
                <div class="testimonial-box text-center p-3">
                    <h5>Juan Pérez</h5>
                    <p>"Es una herramienta intuitiva y fácil de usar, ha mejorado nuestra forma de organizar actividades académicas."</p>
                </div>
            </div>
            <div class="col-md-4">
                <div class="testimonial-box text-center p-3">
                    <h5>Carlos Gómez</h5>
                    <p>"Hemos visto una mejora significativa en la eficiencia de nuestros procesos académicos."</p>
                </div>
            </div>
        </div>
    </div>  

    <!-- Footer -->
    <footer class="bg-light text-center p-3">
        <p>© 2024 Registro Académico. Todos los derechos reservados.</p>
    </footer>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
