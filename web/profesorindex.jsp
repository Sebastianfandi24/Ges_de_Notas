<%-- 
    Document   : profesorindex
    Created on : 19 abr 2025, 1:28:41 a.m.
    Author     : pechi
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Profesor</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <div class="container-fluid">
        <div class="row g-0">
            <!-- Sidebar -->
            <div class="col-md-3 col-lg-2 px-3 sidebar">
                <div class="d-flex flex-column h-100">
                    <div class="user-info mt-4">
                        <div class="system-title">Sistema Académico</div>
                        <p class="mb-0 text-muted">
                            <i class="bi bi-person-circle me-2"></i>
                            Rol: Profesor
                        </p>
                    </div>
                    <nav class="nav flex-column flex-grow-1">
                        <a class="nav-link active" href="profesorindex.jsp">Inicio</a>
                        <a class="nav-link" href="profesortareas.jsp">Gestión de Tareas</a>
                        <a class="nav-link" href="profesornotas.jsp">Gestión de Notas</a>
                    </nav>
                    <div class="mt-auto mb-4">
                        <a href="login.jsp?action=logout" class="btn btn-outline-danger w-100">
                            <i class="bi bi-box-arrow-right me-2"></i>Cerrar Sesión
                        </a>
                    </div>
                </div>
            </div>
            <!-- Contenido principal -->
            <div class="col-md-9 col-lg-10 main-content">
                <div class="content-container">
                    <h2>Bienvenido, Profesor</h2>
                    <p>Accede a tus cursos, tareas y notas desde el menú principal.</p>
                </div>
            </div>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
