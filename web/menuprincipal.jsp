<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    if (session == null || session.getAttribute("userRol") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<c:set var="userRol" value="${sessionScope.userRol}" />
<c:set var="userNombre" value="${sessionScope.userNombre}" />
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
      <!-- Sidebar -->      <div class="col-md-3 col-lg-2 sidebar">
        <div>
          <div class="sidebar-header">
            <h4>Sistema<br>Académico</h4>
            <p><i class="fas fa-user"></i> ${userNombre}</p>
            <p><i class="fas fa-user-circle"></i>
              Rol: 
              <c:choose>
                <c:when test="${userRol == 3}">Administrador</c:when>
                <c:when test="${userRol == 2}">Profesor</c:when>
                <c:when test="${userRol == 1}">Estudiante</c:when>
                <c:otherwise>Desconocido</c:otherwise>
              </c:choose>
            </p>
          </div>          <nav class="nav flex-column">
            <a class="nav-link" href="${pageContext.request.contextPath}/perfil" target="contentFrame">
              <i class="fas fa-user-edit"></i> Mi Perfil
            </a>
            <c:forEach var="act" items="${actividades}">
              <c:choose>
                <c:when test="${userRol == 2 and fn:endsWith(act.enlace,'profesorcursos.jsp')}">
                  <a class="nav-link" href="${pageContext.request.contextPath}/profesor/cursos" target="contentFrame">
                    <i class="fas fa-circle"></i> ${act.nombre}
                  </a>
                </c:when>            <c:when test="${userRol == 2 and fn:endsWith(act.enlace,'profesortareas.jsp')}">
              <a class="nav-link" href="${pageContext.request.contextPath}/profesor/tareas" target="contentFrame">
                <i class="fas fa-circle"></i> ${act.nombre}
              </a>
            </c:when>
                <c:when test="${userRol == 1 and fn:endsWith(act.enlace,'estudiantecursos.jsp')}">
                  <a class="nav-link" href="${pageContext.request.contextPath}/estudiante/mis-cursos" target="contentFrame">
                    <i class="fas fa-circle"></i> ${act.nombre}
                  </a>
                </c:when>
                <c:otherwise>
                  <a class="nav-link" href="${act.enlace}" target="contentFrame">
                    <i class="fas fa-circle"></i> ${act.nombre}
                  </a>
                </c:otherwise>
              </c:choose>
            </c:forEach>
          </nav>
        </div>
        <a href="${pageContext.request.contextPath}/login?action=logout" class="btn btn-logout mt-4">
          <i class="fas fa-sign-out-alt"></i> Cerrar Sesión
        </a>
      </div>

      <!-- Contenido Principal -->
      <c:choose>
        <c:when test="${empty actividades}">
          <div class="col-md-9 col-lg-10 d-flex align-items-center justify-content-center">
            <h3>No tienes ninguna actividad asignada</h3>
          </div>
        </c:when>
        <c:otherwise>
          <div class="col-md-9 col-lg-10 p-0">
            <iframe name="contentFrame" src="${defaultEnlace}" title="Contenido del menú"></iframe>
          </div>
        </c:otherwise>
      </c:choose>
    </div>
  </div>

  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>