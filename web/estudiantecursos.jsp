<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Mis Cursos - Estudiante</title>
  <!-- Bootstrap 5 & Bootstrap Icons -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <style>
    body {
      background-color: #f8f9fa;
    }
    .course-card {
      border: 1px solid #dee2e6;
      border-radius: .5rem;
      overflow: hidden;
      background-color: #fff;
      margin-bottom: 1.5rem;
    }
    .course-card-header {
      background-color: #0d6efd;
      color: #fff;
      padding: .75rem 1.25rem;
      font-size: 1.25rem;
      font-weight: 500;
    }
    .course-card-body {
      padding: 1rem 1.25rem;
    }
    .course-card-body p {
      margin-bottom: .75rem;
      font-size: .95rem;
    }
    .course-card-footer {
      background-color: #f1f1f1;
      padding: .75rem 1.25rem;
      text-align: right;
    }
    .course-card-footer .btn + .btn {
      margin-left: .5rem;
    }
    .progress {
      height: 10px;
      margin-bottom: .75rem;
    }
  </style>
</head>
<body class="bg-light">
  <div class="container py-4">
    <h2 class="mb-1">Mis Cursos</h2>
    <p class="text-muted mb-4">Lista de cursos en los que estás matriculado</p>
    
    <c:if test="${not empty cursoSeleccionado}">
      <div class="card mb-4">
        <div class="card-header bg-primary text-white">
          <h4>${cursoSeleccionado.nombre}</h4>
        </div>
        <div class="card-body">
          <div class="row">
            <div class="col-md-6">
              <h5>Información del Curso</h5>
              <p><strong>Código:</strong> ${cursoSeleccionado.codigo}</p>
              <p><strong>Profesor:</strong> ${cursoSeleccionado.profesor != null ? cursoSeleccionado.profesor : 'Sin asignar'}</p>
              <p><strong>Descripción:</strong> ${cursoSeleccionado.descripcion}</p>
            </div>
            <div class="col-md-6">
              <h5>Tareas Asignadas</h5>
              <c:choose>
                <c:when test="${empty tareasCurso}">
                  <p>No hay tareas asignadas para este curso.</p>
                </c:when>
                <c:otherwise>
                  <ul class="list-group">
                    <c:forEach var="tarea" items="${tareasCurso}">
                      <li class="list-group-item d-flex justify-content-between align-items-center">
                        ${tarea.titulo}
                        <span class="badge ${tarea.estado == 'Pendiente' ? 'bg-warning' : 'bg-success'}">${tarea.estado}</span>
                      </li>
                    </c:forEach>
                  </ul>
                </c:otherwise>
              </c:choose>
            </div>
          </div>
        </div>
        <div class="card-footer">
          <a href="${pageContext.request.contextPath}/estudiante/tareas?curso=${cursoSeleccionado.id}" class="btn btn-primary">Ver todas las tareas</a>
          <a href="${pageContext.request.contextPath}/estudiante/cursos" class="btn btn-outline-secondary">Volver a todos los cursos</a>
        </div>
      </div>
    </c:if>
    
    <div class="row">
      <c:choose>
        <c:when test="${empty cursos}">
          <div class="col-12">
            <div class="alert alert-info">No tienes cursos asignados actualmente.</div>
          </div>
        </c:when>
        <c:otherwise>
          <c:forEach var="curso" items="${cursos}">
            <div class="col-md-6">
              <div class="course-card">
                <div class="course-card-header">
                  ${curso.nombre}
                </div>
                <div class="course-card-body">
                  <p><strong>Profesor:</strong> ${curso.profesor != null ? curso.profesor : 'Sin asignar'}</p>
                  <p><strong>Código:</strong> ${curso.codigo}</p>
                  <p><strong>Descripción:</strong> ${curso.descripcion}</p>
                </div>
                <div class="course-card-footer">
                  <a href="${pageContext.request.contextPath}/estudiante/cursos?curso=${curso.id}" class="btn btn-primary">Ver detalles</a>
                  <a href="${pageContext.request.contextPath}/estudiante/tareas?curso=${curso.id}" class="btn btn-outline-primary">Ver tareas</a>
                </div>
              </div>
            </div>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
  <!-- Bootstrap Bundle JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
