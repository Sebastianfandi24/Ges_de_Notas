<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Calificar Tarea</title>

  <!-- Bootstrap 5 + Bootstrap Icons -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">

  <style>
    body {
      background-color: #f8f9fa;
    }
    .card {
      border-radius: .375rem;
      box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
    }
    .table-wrapper {
      border: 1px solid #dee2e6;
      border-radius: .375rem;
      background-color: #fff;
      overflow: hidden;
    }
    .table-wrapper .table {
      margin-bottom: 0;
    }
  </style>
</head>
<body>
  <div class="container-fluid py-4">
    <!-- Encabezado -->
    <div class="mb-4">
      <div class="d-flex justify-content-between align-items-center">
        <h2 class="m-0">Calificar Tarea</h2>
        <a href="${pageContext.request.contextPath}/profesor/tareas" class="btn btn-outline-secondary">
          <i class="bi bi-arrow-left me-1"></i>Volver a Tareas
        </a>
      </div>
    </div>

    <!-- Información de la tarea -->
    <div class="card mb-4">
      <div class="card-body">
        <h4 class="card-title">${tarea.titulo}</h4>
        <p class="card-text">${tarea.descripcion}</p>
        <div class="row">
          <div class="col-md-4">
            <p><strong>Curso:</strong> ${tarea.curso_nombre}</p>
          </div>
          <div class="col-md-4">
            <p><strong>Fecha Asignación:</strong> ${tarea.fecha_asignacion}</p>
          </div>
          <div class="col-md-4">
            <p><strong>Fecha Entrega:</strong> ${tarea.fecha_entrega}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Formulario de calificación -->
    <div class="card">
      <div class="card-body">
        <h5 class="card-title mb-3">Calificaciones de los Estudiantes</h5>
        
        <form action="${pageContext.request.contextPath}/profesor/tareas/calificar" method="post">
          <input type="hidden" name="idTarea" value="${tarea.id}">
          
          <!-- Tabla de estudiantes y calificaciones -->
          <div class="table-responsive table-wrapper">
            <table class="table align-middle">
              <thead>
                <tr>
                  <th>Estudiante</th>
                  <th>Nota</th>
                  <th>Comentario</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="estudiante" items="${estudiantes}">
                  <tr>
                    <td>${estudiante.nombre}</td>
                    <td style="width: 120px;">
                      <input type="number" class="form-control" name="nota_${estudiante.id}" 
                             min="0" max="5" step="0.1" value="${nota.nota}">
                    </td>
                    <td>
                      <textarea class="form-control" name="comentario_${estudiante.id}" 
                                rows="1">${nota.comentario}</textarea>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
          
          <div class="d-flex justify-content-end mt-4">
            <button type="submit" class="btn btn-primary">
              <i class="bi bi-save me-1"></i>Guardar Calificaciones
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>

  <!-- Bootstrap Bundle JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
