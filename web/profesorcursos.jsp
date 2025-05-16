<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Mis Cursos</title>
  <!-- Bootstrap 5 + Bootstrap Icons -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
  <style>
    body {
      background-color: #f8f9fa;
    }
    .course-card {
      border: 1px solid #dee2e6;
      border-radius: .5rem;
      background-color: #fff;
      overflow: hidden;
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
      text-align: left;
    }
    .course-card-footer .btn + .btn {
      margin-left: .5rem;
    }
    .progress {
      height: 10px;
      margin-bottom: .75rem;
    }
    .debug-panel {
      background-color: #f8d7da;
      border: 1px solid #f5c6cb;
      border-radius: .25rem;
      padding: 1rem;
      margin-bottom: 1.5rem;
    }
  </style>
</head>
<body>
  <div class="wrapper">
  <%-- Debug del servidor --%>
  <%
    System.out.println("====================== DEBUG PROFESORCURSOS.JSP ======================");
    System.out.println("[profesorcursos.jsp] Iniciando renderizado de página");
    System.out.println("[profesorcursos.jsp] cursosInfo: " + (request.getAttribute("cursosInfo") != null ? "presente" : "ausente"));
    System.out.println("[profesorcursos.jsp] cursosInfoDirectos: " + (request.getAttribute("cursosInfoDirectos") != null ? "presente" : "ausente"));
    
    if (request.getAttribute("cursosInfo") != null) {
      java.util.List cursos = (java.util.List) request.getAttribute("cursosInfo");
      System.out.println("[profesorcursos.jsp] Número de cursos (DAO): " + cursos.size());
      for (Object obj : cursos) {
        Models.CursoInfo info = (Models.CursoInfo) obj;
        System.out.println("[profesorcursos.jsp] Curso (DAO): " + info.getCurso().getNombre() + " (ID: " + info.getCurso().getId() + ")");
      }
    }
    
    if (request.getAttribute("cursosInfoDirectos") != null) {
      java.util.List cursos = (java.util.List) request.getAttribute("cursosInfoDirectos");
      System.out.println("[profesorcursos.jsp] Número de cursos (Directo): " + cursos.size());
      for (Object obj : cursos) {
        Models.CursoInfo info = (Models.CursoInfo) obj;
        System.out.println("[profesorcursos.jsp] Curso (Directo): " + info.getCurso().getNombre() + " (ID: " + info.getCurso().getId() + ")");
      }
    }
    System.out.println("=====================================================================");
  %>
  
  <!-- Panel de depuración -->
  <div class="container-fluid py-3 debug-panel">
    <h5>Panel de Depuración</h5>
    <div>
      <strong>Estado del atributo 'cursosInfo' (DAO):</strong>
      <c:choose>
        <c:when test="${not empty cursosInfo}">
          <span class="badge bg-success">Presente (${cursosInfo.size()} cursos)</span>
        </c:when>
        <c:otherwise>
          <span class="badge bg-danger">Ausente</span>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
  
  <!-- Mensaje de error si no hay cursos -->
  <c:if test="${empty cursosInfo}">
    <div class="container-fluid">
      <div class="alert alert-warning">
        <h4 class="alert-heading">No hay cursos cargados</h4>
        <p>No se pudo cargar ningún curso desde la base de datos.</p>
        <hr>
        <p class="mb-0">Debug: Verificar ProfesorCursosServlet y CursoDAO.</p>
      </div>
    </div>
  </c:if>

  <!-- Contenido principal -->
  <div class="container-fluid py-4">
    <!-- Título + botón -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="m-0">Mis Cursos</h2>
      <a href="${pageContext.request.contextPath}/crearCurso.jsp" class="btn btn-success">
        <i class="bi bi-plus-circle me-1"></i> Crear Curso
      </a>
    </div>

    <!-- Sección de cursos desde DAO -->
    <c:if test="${not empty cursosInfo}">
      <h4 class="text-primary mb-3">Cursos</h4>
      <div class="row">
        <c:forEach var="info" items="${cursosInfo}">
          <div class="col-md-4">
            <div class="course-card">
              <div class="course-card-header">
                <c:out value="${info.curso.nombre}"/>
              </div>
              <div class="course-card-body">
                <p><strong>Código:</strong> <c:out value="${info.curso.codigo}"/></p>
                <p><strong>Descripción:</strong> <c:out value="${info.curso.descripcion}"/></p>
                <p><strong>Estudiantes inscritos:</strong> <c:out value="${info.numEstudiantes}"/></p>
                <p><strong>Promedio del curso:</strong> <c:out value="${info.promedio}"/>/10</p>
              </div>
              <div class="course-card-footer">
                <a href="<c:url value='/profesor/cursos/asignar?cursoId=${info.curso.id}'/>" class="btn btn-outline-success">
                  <i class="bi bi-people-fill me-1"></i>Ver/Asignar Estudiantes
                </a>
              </div>
            </div>
          </div>
        </c:forEach>
      </div>
    </c:if>
  </div>

  <!-- Bootstrap Bundle JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  
  <!-- Debug Script -->
  <script>
    console.log('=== Debug Info ===');
    <c:if test="${not empty cursosInfo}">
      console.log('cursosInfo (DAO): presente');
      console.log('Número de cursos (DAO): ${cursosInfo.size()}');
    </c:if>
    <c:if test="${empty cursosInfo}">
      console.log('cursosInfo (DAO): ausente');
    </c:if>
    <c:if test="${not empty cursosInfoDirectos}">
      console.log('cursosInfoDirectos (Directo): presente');
      console.log('Número de cursos (Directo): ${cursosInfoDirectos.size()}');
    </c:if>
    <c:if test="${empty cursosInfoDirectos}">
      console.log('cursosInfoDirectos (Directo): ausente');
    </c:if>
  </script>
  </div>
</body>
</body>
</html>
