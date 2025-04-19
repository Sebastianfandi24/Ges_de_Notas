<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    if (session == null || session.getAttribute("userRol") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    int userRol = (int) session.getAttribute("userRol");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Menú Principal</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .sidebar {
            min-height: 100vh;
            background-color: #f1f5ff;
            padding: 1.5rem 1rem 4rem 1rem;
            border-right: 1px solid #ddd;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            position: relative;
        }
        .sidebar-header {
            text-align: center;
            margin-bottom: 2rem;
        }
        .sidebar-header h4 {
            color: #0d6efd;
            font-weight: bold;
            font-size: 1.5rem;
            margin-bottom: 0.25rem;
        }
        .sidebar-header p {
            color: #6c757d;
            font-size: 0.95rem;
        }
        .sidebar .nav-link {
            color: #fff;
            background-color: #0d6efd;
            font-weight: 500;
            margin-bottom: 0.5rem;
            border-radius: 5px;
            text-align: left;
            padding: 0.75rem 1rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        .sidebar .nav-link:hover {
            background-color: #084298;
        }
        .btn-logout {
            position: absolute;
            bottom: 1rem;
            left: 1rem;
            right: 1rem;
            color: #dc3545;
            border: 1px solid #dc3545;
            font-weight: 500;
            text-align: center;
            padding: 0.75rem 1rem;
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 0.5rem;
            border-radius: 5px;
        }
        .btn-logout:hover {
            background-color: #dc3545;
            color: #fff;
        }
        iframe {
            width: 100%;
            height: calc(100vh - 4rem);
            border: none;
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-3 col-lg-2 sidebar">
                <div>
                    <div class="sidebar-header">
                        <h4>Sistema Académico</h4>
                        <p>
                            <i class="fas fa-user-circle me-2"></i>
                            Rol: 
                            <c:choose>
                                <c:when test="${userRol == 3}">Administrador</c:when>
                                <c:when test="${userRol == 2}">Profesor</c:when>
                                <c:when test="${userRol == 1}">Estudiante</c:when>
                                <c:otherwise>Desconocido</c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                    <nav class="nav flex-column">
                        <% if (userRol == 3) { %>
                            <a class="nav-link" href="adminindex.jsp" target="contentFrame">
                                <i class="fas fa-tachometer-alt"></i> Dashboard
                            </a>
                            <a class="nav-link" href="adminprofesores.jsp" target="contentFrame">
                                <i class="fas fa-id-card"></i> Gestión de Profesores
                            </a>
                            <a class="nav-link" href="adminestudiantes.jsp" target="contentFrame">
                                <i class="fas fa-users"></i> Gestión de Estudiantes
                            </a>
                            <a class="nav-link" href="admincursos.jsp" target="contentFrame">
                                <i class="fas fa-book"></i> Gestión de Cursos
                            </a>
                            <a class="nav-link" href="adminactividades.jsp" target="contentFrame">
                                <i class="fas fa-list"></i> Gestión de Actividades
                            </a>
                        <% } else if (userRol == 2) { %>
                            <a class="nav-link" href="profesorindex.jsp" target="contentFrame">
                                <i class="fas fa-tachometer-alt"></i> Dashboard
                            </a>
                            <a class="nav-link" href="profesorcursos.jsp" target="contentFrame">
                                <i class="fas fa-book"></i> Mis Cursos
                            </a>
                            <a class="nav-link" href="profesortareas.jsp" target="contentFrame">
                                <i class="fas fa-tasks"></i> Gestión de Tareas
                            </a>
                            <a class="nav-link" href="profesornotas.jsp" target="contentFrame">
                                <i class="fas fa-clipboard"></i> Gestión de Notas
                            </a>
                        <% } else if (userRol == 1) { %>
                            <a class="nav-link" href="estudianteindex.jsp" target="contentFrame">
                                <i class="fas fa-tachometer-alt"></i> Dashboard
                            </a>
                            <a class="nav-link" href="estudiantecursos.jsp" target="contentFrame">
                                <i class="fas fa-book"></i> Mis Cursos
                            </a>
                            <a class="nav-link" href="estudiantetareas.jsp" target="contentFrame">
                                <i class="fas fa-tasks"></i> Mis Tareas
                            </a>
                        <% } %>
                    </nav>
                </div>
                <a href="login.jsp?action=logout" class="btn btn-logout">
                    <i class="fas fa-sign-out-alt"></i> Cerrar Sesión
                </a>
            </div>
            <!-- Contenido principal -->
            <div class="col-md-9 col-lg-10">
                <iframe name="contentFrame" src="<%= userRol == 3 ? "adminindex.jsp" : userRol == 2 ? "profesorindex.jsp" : "estudianteindex.jsp" %>"></iframe>
            </div>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>