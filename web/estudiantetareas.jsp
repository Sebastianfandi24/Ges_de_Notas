<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Mis Tareas - Estudiante</title>
  <!-- Bootstrap 5 & Bootstrap Icons -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
  <style>
    body {
      background-color: #f8f9fa;
    }
    .table-wrapper {
      border: 1px solid #dee2e6;
      border-radius: .375rem;
      background-color: #fff;
    }
    .table-wrapper table {
      margin-bottom: 0;
    }
    .badge-status {
      border-radius: 10rem;
      padding: .35em .75em;
      font-size: .85em;
    }
  </style>
</head>
<body class="bg-light">
  <div class="container-fluid py-4">
    <!-- Encabezado con título + filtros -->
    <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4">
      <h2 class="mb-3 mb-md-0">Mis Tareas</h2>
      <form action="${pageContext.request.contextPath}/estudiante/tareas" method="GET" class="d-flex flex-column flex-md-row gap-2">        <select class="form-select form-select-sm" style="width:auto;" name="estado" id="estadoFilter">
          <option value="Todos" ${estadoFiltro == 'Todos' ? 'selected' : ''}>Todas las tareas</option>
          <option value="Pendiente" ${estadoFiltro == 'Pendiente' ? 'selected' : ''}>Pendientes</option>
          <option value="En revisión" ${estadoFiltro == 'En revisión' ? 'selected' : ''}>En revisión</option>
          <option value="Calificado" ${estadoFiltro == 'Calificado' ? 'selected' : ''}>Calificadas</option>
        </select>
        <select class="form-select form-select-sm" style="width:auto;" name="curso" id="cursoFilter">          <option value="todos" ${cursoFiltro == 'todos' || empty cursoFiltro ? 'selected' : ''}>Todos los cursos</option>
          <c:forEach var="curso" items="${cursos}">
            <option value="${curso.id}" ${cursoFiltro eq curso.id.toString() ? 'selected' : ''}>${curso.nombre}</option>
          </c:forEach>
        </select>
        <button type="submit" class="btn btn-sm btn-primary">Filtrar</button>
      </form>
    </div>

    <!-- Tabla dinámica -->
    <div class="table-responsive table-wrapper">
      <table class="table align-middle mb-0">
        <thead>
          <tr>
            <th>Título</th>
            <th>Curso</th>
            <th>Fecha entrega</th>
            <th>Estado</th>
            <th>Calificación</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <c:choose>
            <c:when test="${empty tareas}">
              <tr>
                <td colspan="6" class="text-center">No tienes tareas asignadas.</td>
              </tr>
            </c:when>
            <c:otherwise>
              <c:forEach var="tarea" items="${tareas}">
                <tr>
                  <td>${tarea.titulo}</td>
                  <td>${tarea.curso}</td>
                  <td><fmt:formatDate value="${tarea.fecha_entrega}" pattern="dd/MM/yyyy" /></td>                  <td>
                    <c:choose>
                      <c:when test="${tarea.estado == 'Pendiente'}">
                        <span class="badge bg-warning text-white badge-status">Pendiente</span>
                      </c:when>
                      <c:when test="${tarea.estado == 'En revisión'}">
                        <span class="badge bg-info text-white badge-status">En revisión</span>
                      </c:when>
                      <c:otherwise>
                        <span class="badge bg-success text-white badge-status">Calificado</span>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td>
                    ${tarea.nota != null ? tarea.nota : "-"}
                  </td>
                  <td>
                    <c:choose>
                      <c:when test="${tarea.estado == 'Pendiente'}">
                        <button class="btn btn-sm btn-primary" onclick="mostrarModalEntrega('${tarea.id}', '${tarea.titulo}')">Entregar</button>
                      </c:when>
                      <c:when test="${tarea.estado == 'En revisión'}">
                        <button class="btn btn-sm btn-warning" disabled>En revisión</button>
                      </c:when>
                      <c:otherwise>
                        <button class="btn btn-sm btn-secondary" onclick="mostrarDetalles('${tarea.id}', '${tarea.titulo}', '${tarea.nota}', '${tarea.comentario}')">Ver detalles</button>
                      </c:otherwise>
                    </c:choose>
                  </td>
                </tr>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </tbody>
      </table>
    </div>
  </div>

  <!-- Modal para entrega de tarea -->
  <div class="modal fade" id="modalEntrega" tabindex="-1" aria-labelledby="modalEntregaLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="modalEntregaLabel">Entregar Tarea</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <form id="formEntrega">
            <input type="hidden" id="tareaId" name="tareaId">
            <div class="mb-3">
              <label for="tareaTitulo" class="form-label">Tarea a entregar:</label>
              <span id="tareaTitulo" class="fw-bold"></span>
            </div>            <div class="mb-3">
              <label for="comentarios" class="form-label">Comentarios</label>
              <textarea class="form-control" id="comentarios" name="comentarios" rows="3"></textarea>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
          <button type="button" class="btn btn-primary" onclick="entregarTarea()">Entregar</button>
        </div>
      </div>
    </div>
  </div>

  <!-- Modal para ver detalles de tarea entregada -->
  <div class="modal fade" id="modalDetalles" tabindex="-1" aria-labelledby="modalDetallesLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="modalDetallesLabel">Detalles de la Tarea</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <h6 id="detalleTitulo" class="fw-bold mb-3">Título de la tarea</h6>
          <div class="mb-3">
            <div class="fw-bold">Calificación:</div>
            <span id="detalleNota"></span>
          </div>
          <div class="mb-3">
            <div class="fw-bold">Comentarios del profesor:</div>
            <p id="detalleComentario" class="border rounded p-2 bg-light"></p>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
        </div>
      </div>
    </div>
  </div>

  <!-- Bootstrap Bundle JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  
  <script>
    // Inicializar modales
    let modalEntrega = null;
    let modalDetalles = null;
    
    document.addEventListener('DOMContentLoaded', function() {
      modalEntrega = new bootstrap.Modal(document.getElementById('modalEntrega'));
      modalDetalles = new bootstrap.Modal(document.getElementById('modalDetalles'));
      
      // Habilitar filtrado AJAX si fuera necesario
      document.querySelectorAll('#estadoFilter, #cursoFilter').forEach(select => {
        select.addEventListener('change', function() {
          // Si se desea filtrar con AJAX en vez de recargar la página
          // document.querySelector('form').submit();
        });
      });
    });
    
    function mostrarModalEntrega(tareaId, titulo) {
      document.getElementById('tareaId').value = tareaId;
      document.getElementById('tareaTitulo').textContent = titulo;
      modalEntrega.show();
    }
    
    function mostrarDetalles(tareaId, titulo, nota, comentario) {
      document.getElementById('detalleTitulo').textContent = titulo;
      document.getElementById('detalleNota').textContent = nota;
      document.getElementById('detalleComentario').textContent = comentario || 'Sin comentarios';
      modalDetalles.show();
    }    function entregarTarea() {
      const tareaId = document.getElementById('tareaId').value;
      // Simplificando - no requerimos comentarios obligatorios
      const comentarios = "Entregado";
      
      console.log("Enviando entrega: tareaId=" + tareaId + ", comentarios=" + comentarios);
      
      // Crear objeto FormData para enviar datos
      const formData = new FormData();
      formData.append('tareaId', tareaId);
      formData.append('comentarios', comentarios);
      
      // Enviar la entrega al servidor usando URLSearchParams en lugar de FormData
      // para garantizar compatibilidad con todos los servidores
      fetch('${pageContext.request.contextPath}/estudiante/api/entregarTarea', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
          'tareaId': tareaId,
          'comentarios': comentarios
        })
      })
      .then(response => response.json())
      .then(data => {
        if (data.success) {
          modalEntrega.hide();
          // Mostrar mensaje de éxito y actualizar la página
          alert('Tarea entregada correctamente');
          window.location.reload();
        } else {
          alert('Error: ' + (data.error || 'No se pudo entregar la tarea'));
          console.error("Error en respuesta:", data);
        }
      })
      .catch(error => {
        console.error('Error al entregar tarea:', error);
        alert('Error al comunicarse con el servidor');
      });
    }
  </script>
</body>
</html>
