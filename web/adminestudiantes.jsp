<%-- 
    Document   : adminestudiantes
    Created on : 19 abr 2025, 1:27:40 a.m.
    Author     : pechi
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Estudiantes</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- DataTables CSS -->
    <link href="https://cdn.datatables.net/1.11.5/css/dataTables.bootstrap5.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
</head>
<body>
    <div class="container-fluid py-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Gestión de Estudiantes</h2>
            <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#estudianteModal">
                <i class="fas fa-plus-circle me-2"></i> Nuevo Estudiante
            </button>
        </div>
        <!-- Tabla de Estudiantes -->
        <div class="card">
            <div class="card-body">
                <table id="estudiantesTable" class="table table-striped table-hover">
                    <thead>
                        <tr>
                            <th>ID Estudiante</th>
                            <th>Nombre</th>
                            <th>Correo</th>
                            <th>Teléfono</th>
                            <th>Estado</th>
                            <th>Promedio Académico</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <!-- Los datos se cargarán dinámicamente -->
                    </tbody>
                </table>
            </div>
        </div>
        <!-- Modal para Crear/Editar Estudiante -->
        <div class="modal fade" id="estudianteModal" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="modalTitle">Nuevo Estudiante</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="estudianteForm">
                            <input type="hidden" id="estudianteId">
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label class="form-label">Nombre</label>
                                    <input type="text" class="form-control" id="nombre" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Correo</label>
                                    <input type="email" class="form-control" id="correo" required>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label class="form-label">Fecha de Nacimiento</label>
                                    <input type="date" class="form-control" id="fechaNacimiento">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Teléfono</label>
                                    <input type="tel" class="form-control" id="telefono">
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label class="form-label">Estado</label>
                                    <select class="form-select" id="estado">
                                        <option value="Activo">Activo</option>
                                        <option value="Inactivo">Inactivo</option>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Promedio Académico</label>
                                    <input type="number" step="0.01" class="form-control" id="promedioAcademico">
                                </div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Dirección</label>
                                <textarea class="form-control" id="direccion" rows="2"></textarea>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn btn-primary" onclick="guardarEstudiante()">Guardar</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- Bootstrap Bundle with Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <!-- DataTables JS -->
    <script src="https://cdn.datatables.net/1.11.5/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/1.11.5/js/dataTables.bootstrap5.min.js"></script>
    <script>
        $(document).ready(function() {
            cargarEstudiantes();
        });

        function cargarEstudiantes() {
            $.ajax({
                url: '${pageContext.request.contextPath}/EstudiantesController',
                type: 'GET',
                success: function(data) {
                    const table = $('#estudiantesTable').DataTable({
                        data: data,
                        columns: [
                            { data: 'id_estudiante' },
                            { data: 'nombre' },
                            { data: 'correo' },
                            { data: 'telefono' },
                            { data: 'estado' },
                            { data: 'promedio_academico' },
                            {
                                data: null,
                                render: function(data, type, row) {
                                    return `
                                        <div class="action-buttons">
                                            <button class="btn btn-sm btn-info" onclick="editarEstudiante(${row.id_estudiante})" title="Editar estudiante">
                                                <i class="fas fa-edit"></i>
                                            </button>
                                            <button class="btn btn-sm btn-danger" onclick="eliminarEstudiante(${row.id_estudiante})" title="Eliminar estudiante">
                                                <i class="fas fa-trash-alt"></i>
                                            </button>
                                        </div>`;
                                }
                            }
                        ],
                        language: {
                            url: 'https://cdn.datatables.net/plug-ins/1.11.5/i18n/es-ES.json'
                        }
                    });
                },
                error: function(xhr) {
                    alert('Error al cargar los estudiantes: ' + xhr.responseText);
                }
            });
        }

        function guardarEstudiante() {
            const estudiante = {
                id_estudiante: $('#estudianteId').val(),
                nombre: $('#nombre').val(),
                correo: $('#correo').val(),
                telefono: $('#telefono').val(),
                estado: $('#estado').val(),
                promedio_academico: $('#promedioAcademico').val(),
                direccion: $('#direccion').val()
            };

            $.ajax({
                url: '${pageContext.request.contextPath}/EstudiantesController',
                type: estudiante.id_estudiante ? 'PUT' : 'POST',
                contentType: 'application/json',
                data: JSON.stringify(estudiante),
                success: function() {
                    $('#estudianteModal').modal('hide');
                    $('#estudiantesTable').DataTable().ajax.reload();
                },
                error: function(xhr) {
                    alert('Error al guardar el estudiante: ' + xhr.responseText);
                }
            });
        }

        function editarEstudiante(id) {
            $.ajax({
                url: '${pageContext.request.contextPath}/EstudiantesController?id=' + id,
                type: 'GET',
                success: function(estudiante) {
                    $('#estudianteId').val(estudiante.id_estudiante);
                    $('#nombre').val(estudiante.nombre);
                    $('#correo').val(estudiante.correo);
                    $('#telefono').val(estudiante.telefono);
                    $('#estado').val(estudiante.estado);
                    $('#promedioAcademico').val(estudiante.promedio_academico);
                    $('#direccion').val(estudiante.direccion);
                    $('#estudianteModal').modal('show');
                },
                error: function(xhr) {
                    alert('Error al cargar los datos del estudiante: ' + xhr.responseText);
                }
            });
        }

        function eliminarEstudiante(id) {
            if (confirm('¿Está seguro de que desea eliminar este estudiante?')) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/EstudiantesController?id=' + id,
                    type: 'DELETE',
                    success: function() {
                        $('#estudiantesTable').DataTable().ajax.reload();
                    },
                    error: function(xhr) {
                        alert('Error al eliminar el estudiante: ' + xhr.responseText);
                    }
                });
            }
        }
    </script>
</body>
</html>
