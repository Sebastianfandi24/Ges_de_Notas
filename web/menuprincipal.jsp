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
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Menú Principal</title>
  <!-- Bootstrap 5 + FA6 -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
  <style>
    body {
      background-color: #eef4ff;
    }
    .sidebar {
      min-height: 100vh;
      background-color: transparent;
      padding: 2rem 1rem 4rem;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
    }
    /* Card de cabecera */
    .sidebar-header {
      background: #fff;
      border-radius: 16px;
      padding: 1rem 1.25rem;
      box-shadow: 0 4px 12px rgba(0,0,0,0.05);
      margin-bottom: 2rem;
    }
    .sidebar-header h4 {
      margin: 0;
      color: #2e6ff8;
      font-size: 1.75rem;
      font-weight: 700;
      line-height: 1.2;
    }
    .sidebar-header p {
      margin: .5rem 0 0;
      color: #555;
      font-size: 1rem;
      display: flex;
      align-items: center;
      gap: .5rem;
    }

    /* Botones de navegación */
    .sidebar .nav-link {
      display: flex;
      align-items: center;
      gap: .75rem;
      background-color: #2e6ff8;
      color: #fff !important;
      border-radius: 12px;
      padding: .75rem 1rem;
      font-size: 1.05rem;
      font-weight: 500;
      margin-bottom: .75rem;
      transition: background .2s;
    }
    .sidebar .nav-link i {
      font-size: 1.25rem;
    }
    .sidebar .nav-link:hover,
    .sidebar .nav-link.active {
      background-color: #235ac4;
      text-decoration: none;
    }

    /* Botón Cerrar Sesión */
    .btn-logout {
      background: #e9f9f5;
      color: #dc3545;
      border: 2px solid #dc3545;
      border-radius: 12px;
      font-weight: 600;
      padding: .75rem;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: .5rem;
      transition: background .2s, color .2s;
    }
    .btn-logout i {
      font-size: 1.25rem;
    }
    .btn-logout:hover {
      background-color: #dc3545;
      color: #fff;
      text-decoration: none;
    }

    /* iframe ocupa todo el espacio restante */
    iframe {
      width: 100%;
      height: calc(100vh - 2rem);
      border: none;
      background: #fff;
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
            <h4>Sistema<br>Académico</h4>
            <p><i class="fas fa-user-circle"></i>
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
        <a href="login.jsp?action=logout" class="btn btn-logout mt-4">
          <i class="fas fa-sign-out-alt"></i> Cerrar Sesión
        </a>
      </div>

      <!-- Contenido Principal -->
      <div class="col-md-9 col-lg-10 p-0">
        <iframe name="contentFrame"
          src="<%= userRol == 3 ? "adminindex.jsp"
                : userRol == 2 ? "profesorindex.jsp"
                               : "estudianteindex.jsp" %>">
        </iframe>
      </div>
    </div>
  </div>

  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>