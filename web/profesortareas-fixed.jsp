<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Gestión de Tareas</title>

  <!-- Bootstrap 5 + Bootstrap Icons -->
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
      overflow: hidden;
    }
    .table-wrapper .table {
      margin-bottom: 0;
    }
    .badge {
      font-size: .85rem;
      padding: .4em .75em;
    }
    .debug-info {
      background: #f8f9fa;
      border: 1px solid #dee2e6;
      border-radius: .375rem;
      padding: 1rem;
      margin: 1rem 0;
      font-family: monospace;
    }
  </style>
</head>
<body>
  <c:if test="${param.debug == 'true'}">
    <div class="container-fluid mb-4">
      <div class="debug-info">
        <h5>Información de Depuración:</h5>
        <p>Session ID: ${pageContext.session.id}</p>
        <p>Usuario ID: ${sessionScope.userId}</p>
        <p>Número de Cursos: ${fn:length(cursos)}</p>
        <p>Número de Tareas: ${fn:length(tareas)}</p>
        <p>Request URI: ${pageContext.request.requestURI}</p>
        <p>Context Path: ${pageContext.request.contextPath}</p>
        <p>Servlet Path: ${pageContext.request.servletPath}</p>
        <c:if test="${not empty cursos}">
          <h6>Cursos:</h6>
          <ul>
            <c:forEach var="curso" items="${cursos}">
              <li>ID: ${curso.id}, Nombre: ${curso.nombre}</li>
            </c:forEach>
          </ul>
        </c:if>
        <c:if test="${not empty tareas}">
          <h6>Tareas:</h6>
          <ul>
            <c:forEach var="tarea" items="${tareas}">
              <li>ID: ${tarea.id}, Título: ${tarea.titulo}, Curso: ${tarea.cursoNombre}</li>
            </c:forEach>
          </ul>
        </c:if>
      </div>
    </div>
  </c:if>
  <div class="container-fluid py-4">
    <!-- Encabezado con filtros y botón Nueva tarea -->
    <div class="d-flex justify-content-between align-items-center mb-4">      <h2 class="m-0">Gestión de Tareas</h2>
      <div>
        <c:choose>
          <c:when test="${empty cursos}">
            <button class="btn btn-primary" disabled title="Debe crear un curso primero">
              <i class="bi bi-plus-circle me-1"></i>Nueva Tarea
            </button>
            <div class="alert alert-warning mt-2" role="alert">
              <small><i class="bi bi-info-circle me-1"></i>Para crear tareas, primero debe tener al menos un curso asignado.</small>
            </div>
          </c:when>
          <c:otherwise>
            <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#newTaskModal">
              <i class="bi bi-plus-circle me-1"></i>Nueva Tarea
            </button>
            <a href="${pageContext.request.contextPath}/diagnostico/tareas" 
               class="btn btn-outline-secondary ms-2" target="_blank" title="Herramienta de diagnóstico">
              <i class="bi bi-wrench me-1"></i>Diagnóstico
            </a>
            <a href="${pageContext.request.contextPath}/profesor/tareas?testview=true" 
               class="btn btn-outline-info ms-2" title="Probar vista con tarea de prueba">
              <i class="bi bi-eyeglasses me-1"></i>Probar Vista
            </a>
          </c:otherwise>
        </c:choose>
      </div>
    </div>

    <!-- Tabla principal -->
    <div class="table-responsive table-wrapper">
      <table class="table align-middle mb-0">
        <thead>
          <tr>
            <th>Título</th>
            <th>Curso</th>
            <th>Fecha Asignación</th>
            <th>Fecha Entrega</th>
            <th>Estado</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <c:choose>
            <c:when test="${empty tareas}">
              <tr>
                <td colspan="6" class="text-center py-4">
                  <div class="alert alert-info mb-0" role="alert">
                    <i class="bi bi-info-circle me-2"></i>
                    No hay tareas disponibles
                    <c:if test="${not empty cursos}">
                      <br>
                      <small>Puede crear una nueva tarea usando el botón "Nueva Tarea"</small>
                    </c:if>
                  </div>
                </td>
              </tr>
            </c:when>
            <c:otherwise>
              <c:forEach var="tarea" items="${tareas}">
                <tr>                  <td>${fn:escapeXml(tarea.titulo)}</td>
                  <td>${fn:escapeXml(tarea.cursoNombre)}</td>
                  <td><fmt:formatDate value="${tarea.fechaAsignacion}" pattern="dd/MM/yyyy"/></td>
                  <td><fmt:formatDate value="${tarea.fechaEntrega}" pattern="dd/MM/yyyy"/></td>
                  <td>
                    <c:choose>
                      <c:when test="${tarea.estado eq 'Vencida'}">
                        <span class="badge bg-danger">Vencida</span>
                      </c:when>
                      <c:when test="${tarea.estado eq 'Activa'}">
                        <span class="badge bg-success">Activa</span>
                      </c:when>
                      <c:otherwise>
                        <span class="badge bg-secondary">Pendiente</span>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td>
                    <div class="btn-group btn-group-sm">
                      <button type="button" class="btn btn-outline-primary btn-grade"
                              data-bs-toggle="modal" data-bs-target="#gradeModal"
                              data-tarea-id="${tarea.id}"
                              data-tarea-titulo="${fn:escapeXml(tarea.titulo)}">
                        <i class="bi bi-check2-square"></i>
                      </button>
                      <button type="button" class="btn btn-outline-secondary btn-edit"
                              data-bs-toggle="modal" data-bs-target="#editTaskModal"
                              data-tarea-id="${tarea.id}">
                        <i class="bi bi-pencil"></i>
                      </button>
                      <button type="button" class="btn btn-outline-danger btn-delete"
                              data-tarea-id="${tarea.id}">
                        <i class="bi bi-trash"></i>
                      </button>
                    </div>
                  </td>
                </tr>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </tbody>
      </table>
    </div>
  </div>

  <!-- Modal Nueva Tarea -->
  <div class="modal fade" id="newTaskModal" tabindex="-1" aria-labelledby="newTaskModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="newTaskModalLabel">Nueva Tarea</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <form id="newTaskForm" action="${pageContext.request.contextPath}/profesor/tareas" method="post">
            <div class="mb-3">
              <label for="taskTitle" class="form-label">Título de la tarea</label>
              <input type="text" class="form-control" id="taskTitle" name="titulo" required>
            </div>
            <div class="mb-3">
              <label for="taskDesc" class="form-label">Descripción</label>
              <textarea class="form-control" id="taskDesc" name="descripcion" rows="3"></textarea>
            </div>
            <div class="mb-3">
              <label for="taskCourse" class="form-label">Curso</label>
              <select class="form-select" id="taskCourse" name="idCurso" required>
                <option value="">Seleccione un curso...</option>
                <c:forEach var="curso" items="${cursos}">
                  <option value="${curso.id}">${fn:escapeXml(curso.nombre)}</option>
                </c:forEach>
              </select>
            </div>
            <div class="mb-3">
              <label for="taskDueDate" class="form-label">Fecha de entrega</label>
              <input type="date" class="form-control" id="taskDueDate" name="fechaEntrega" required>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
          <button type="submit" form="newTaskForm" class="btn btn-primary">Crear Tarea</button>
        </div>
      </div>
    </div>
  </div>

  <!-- Modal Calificar Entregas -->
  <div class="modal fade" id="gradeModal" tabindex="-1" aria-labelledby="gradeModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="gradeModalLabel">Calificar Entregas</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <form id="gradeForm" action="${pageContext.request.contextPath}/profesor/tareas/calificar" method="post">
            <input type="hidden" id="gradeTareaId" name="idTarea">
            <h6 id="gradeTareaTitle" class="mb-3 fw-bold">Detalles de la tarea</h6>
            <div class="table-responsive">
              <table class="table">
                <thead>
                  <tr>
                    <th>Estudiante</th>
                    <th>Nota</th>
                    <th>Comentario</th>
                  </tr>
                </thead>
                <tbody id="gradeTableBody">
                  <!-- Se cargará dinámicamente -->
                </tbody>
              </table>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
          <button type="submit" form="gradeForm" class="btn btn-primary">Guardar Calificaciones</button>
        </div>
      </div>
    </div>
  </div>

  <!-- Modal Editar Tarea -->
  <div class="modal fade" id="editTaskModal" tabindex="-1" aria-labelledby="editTaskModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="editTaskModalLabel">Editar Tarea</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <form id="editTaskForm" action="${pageContext.request.contextPath}/profesor/tareas" method="post">
            <input type="hidden" id="editTaskId" name="idTarea">
            <div class="mb-3">
              <label for="editTaskTitle" class="form-label">Título de la tarea</label>
              <input type="text" class="form-control" id="editTaskTitle" name="titulo" required>
            </div>
            <div class="mb-3">
              <label for="editTaskDesc" class="form-label">Descripción</label>
              <textarea class="form-control" id="editTaskDesc" name="descripcion" rows="3"></textarea>
            </div>
            <div class="mb-3">
              <label for="editTaskCourse" class="form-label">Curso</label>
              <select class="form-select" id="editTaskCourse" name="idCurso" required>
                <option value="">Seleccione un curso...</option>
                <c:forEach var="curso" items="${cursos}">
                  <option value="${curso.id}">${fn:escapeXml(curso.nombre)}</option>
                </c:forEach>
              </select>
            </div>
            <div class="mb-3">
              <label for="editTaskDueDate" class="form-label">Fecha de entrega</label>
              <input type="date" class="form-control" id="editTaskDueDate" name="fechaEntrega" required>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
          <button type="submit" form="editTaskForm" class="btn btn-primary">Guardar Cambios</button>
        </div>
      </div>
    </div>
  </div>

  <!-- Scripts -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  <script>
    // Definir variable de contexto para ser usada en tareas-profesor.js
    window.contextPath = "${pageContext.request.contextPath}";
  </script>
  <script src="${pageContext.request.contextPath}/js/tareas-profesor.js"></script>
</body>
</html>
