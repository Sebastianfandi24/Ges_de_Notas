<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mis Tareas - Estudiante</title>
    <!-- DataTables CSS -->
    <link href="https://cdn.datatables.net/1.11.5/css/dataTables.bootstrap5.min.css" rel="stylesheet">
    <style>
        .task-card {
            transition: transform 0.2s;
        }
        .task-card:hover {
            transform: translateY(-5px);
        }
        .status-badge {
            position: absolute;
            top: 10px;
            right: 10px;
        }
    </style>
</head>
<body class="bg-light">
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="#">Sistema Académico</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="#">Mis Cursos</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="#">Mis Tareas</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#">Mis Notas</a>
                    </li>
                </ul>
                <form class="d-flex">
                    <button class="btn btn-outline-light" type="button">Cerrar Sesión</button>
                </form>
            </div>
        </div>
    </nav>

    <div class="container-fluid py-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Mis Tareas</h2>
            <div class="d-flex gap-2">
                <select class="form-select" id="filtroEstado" style="width: auto;">
                    <option value="todas">Todas las tareas</option>
                    <option value="pendientes">Pendientes</option>
                    <option value="calificadas">Calificadas</option>
                </select>
                <select class="form-select" id="filtroCurso" style="width: auto;">
                    <option value="todos">Todos los cursos</option>
                    <!-- Los cursos se cargarán dinámicamente -->
                </select>
            </div>
        </div>

        <!-- Vista de Tareas en Cards -->
        <div class="row" id="tareasContainer">
            <!-- Las tareas se cargarán dinámicamente aquí -->
        </div>

        <!-- Modal para Ver Detalle de Tarea -->
        <div class="modal fade" id="tareaModal" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="tareaTitulo"></h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="row mb-4">
                            <div class="col-md-6">
                                <h6>Información de la Tarea</h6>
                                <p><strong>Curso:</strong> <span id="tareaCurso"></span></p>
                                <p><strong>Profesor:</strong> <span id="tareaProfesor"></span></p>
                                <p><strong>Fecha de Asignación:</strong> <span id="tareaFechaAsignacion"></span></p>
                                <p><strong>Fecha de Entrega:</strong> <span id="tareaFechaEntrega"></span></p>
                            </div>
                            <div class="col-md-6">
                                <div class="card h-100">
                                    <div class="card-body text-center">
                                        <h6 class="card-title">Calificación</h6>
                                        <div id="calificacionContainer">
                                            <!-- El contenido se cargará dinámicamente -->
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="mb-4">
                            <h6>Descripción de la Tarea</h6>
                            <p id="tareaDescripcion"></p>
                        </div>
                        <div id="comentarioContainer" style="display: none;">
                            <h6>Comentarios del Profesor</h6>
                            <p id="tareaComentario"></p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- DataTables JS -->
    <script src="https://cdn.datatables.net/1.11.5/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/1.11.5/js/dataTables.bootstrap5.min.js"></script>

    <script>
        let tareas = [];

        $(document).ready(function() {
            cargarCursos();
            cargarTareas();

            // Eventos de filtrado
            $('#filtroEstado, #filtroCurso').change(function() {
                filtrarTareas();
            });
        });

        function cargarCursos() {
            $.get('${pageContext.request.contextPath}/CursosController/estudiante/${sessionScope.userId}', function(cursos) {
                const select = $('#filtroCurso');
                cursos.forEach(curso => {
                    select.append(`<option value="${curso.id_curso}">${curso.nombre}</option>`);
                });
            });
        }

        function cargarTareas() {
            $.get('${pageContext.request.contextPath}/TareasController/estudiante/${sessionScope.userId}', function(data) {
                tareas = data;
                filtrarTareas();
            });
        }

        function tieneCalificacion(tarea) {
            return tarea.calificacion !== null && tarea.calificacion !== undefined && tarea.calificacion !== '';
        }
        
        function filtrarTareas() {
            const estado = $('#filtroEstado').val();
            const cursoId = $('#filtroCurso').val();

            let tareasFiltradas = tareas;

            if (cursoId !== 'todos') {
                tareasFiltradas = tareasFiltradas.filter(t => t.id_curso == cursoId);
            }

            if (estado !== 'todas') {
                tareasFiltradas = tareasFiltradas.filter(t => {
                    if (estado === 'pendientes') return !tieneCalificacion(t);
                    if (estado === 'calificadas') return tieneCalificacion(t);
                });
            }

            mostrarTareas(tareasFiltradas);
        }

        function mostrarTareas(tareasFiltradas) {
            const container = $('#tareasContainer');
            container.empty();

            if (tareasFiltradas.length === 0) {
                container.append(`
                    <div class="col-12 text-center">
                        <p class="text-muted">No hay tareas que mostrar</p>
                    </div>
                `);
                return;
            }

            tareasFiltradas.forEach(tarea => {
                container.append(crearTarjetaTarea(tarea));
            });
        }

        function crearTarjetaTarea(tarea) {
            const fechaEntrega = new Date(tarea.fecha_entrega).toLocaleDateString('es-ES');
            const esCalificada = tieneCalificacion(tarea);
            const estadoClass = esCalificada ? 'success' : 'warning';
            const estadoTexto = esCalificada ? 'Calificada' : 'Pendiente';

            return `
                <div class="col-md-4 mb-4">
                    <div class="card task-card h-100 position-relative">
                        <div class="card-body">
                            <span class="badge bg-${estadoClass} status-badge">${estadoTexto}</span>
                            <h5 class="card-title mb-3">${tarea.titulo}</h5>
                            <h6 class="card-subtitle mb-2 text-muted">${tarea.curso_nombre}</h6>
                            <p class="card-text">${tarea.descripcion.substring(0, 100)}...</p>
                            <div class="d-flex justify-content-between align-items-center">
                                <small class="text-muted">Entrega: ${fechaEntrega}</small>
                                <button class="btn btn-primary btn-sm" onclick="verDetalleTarea(${tarea.id_tarea})">
                                    <i class="fas fa-eye"></i> Ver Detalle
                                </button>
                            </div>
                            ${esCalificada ? `
                                <div class="text-center mt-3">
                                    <span class="badge bg-primary" style="font-size: 1rem;">
                                        Nota: ${tarea.calificacion}
                                    </span>
                                </div>
                            ` : ''}
                        </div>
                    </div>
                </div>
            `;
        }

        function verDetalleTarea(tareaId) {
            const tarea = tareas.find(t => t.id_tarea === tareaId);
            if (!tarea) return;

            $('#tareaTitulo').text(tarea.titulo);
            $('#tareaCurso').text(tarea.curso_nombre);
            $('#tareaProfesor').text(tarea.profesor_nombre);
            $('#tareaFechaAsignacion').text(new Date(tarea.fecha_asignacion).toLocaleDateString('es-ES'));
            $('#tareaFechaEntrega').text(new Date(tarea.fecha_entrega).toLocaleDateString('es-ES'));
            $('#tareaDescripcion').text(tarea.descripcion);

            const calificacionContainer = $('#calificacionContainer');
            if (tieneCalificacion(tarea)) {
                calificacionContainer.html(`
                    <h1 class="display-4 text-primary mb-0">${tarea.calificacion}</h1>
                    <p class="text-muted">Calificación Final</p>
                `);
                $('#tareaComentario').text(tarea.comentario || 'Sin comentarios');
                $('#comentarioContainer').show();
            } else {
                calificacionContainer.html(`
                    <span class="badge bg-warning" style="font-size: 1.2rem;">Pendiente de Calificación</span>
                `);
                $('#comentarioContainer').hide();
            }

            $('#tareaModal').modal('show');
        }
    </script>
</body>
</html>