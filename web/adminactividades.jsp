<%-- 
    Document   : adminactividades
    Created on : 19 abr 2025, 1:28:24 a.m.
    Author     : pechi
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Actividades</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- DataTables CSS -->
    <link href="https://cdn.datatables.net/1.11.5/css/dataTables.bootstrap5.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        /* ============ Variables de color ============ */
        :root {
            --primary-color: #4361ee;
            --secondary-color: #3f37c9;
            --accent-color: #4895ef;
            --success-color: #4cc9f0;
            --info-color: #4361ee;
            --warning-color: #f72585;
            --danger-color: #e63946;
            --light-color: #f8f9fa;
            --dark-color: #212529;
        }

        /* ============ Tipografía y fondo ============ */
        body {
            background-color: #f0f2f5;
            font-family: 'Poppins', sans-serif;
            transition: all 0.3s ease;
        }

        .container-fluid {
            padding-top: 2rem;
            padding-bottom: 2rem;
        }

        h2 {
            color: var(--dark-color);
            font-weight: 600;
            margin-bottom: 1.5rem;
            position: relative;
            padding-bottom: 0.5rem;
        }
        h2:after {
            content: '';
            position: absolute;
            left: 0;
            bottom: 0;
            height: 3px;
            width: 60px;
            background: linear-gradient(to right, var(--primary-color), var(--accent-color));
        }

        /* ============ Cards ============ */
        .card {
            border: none;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
            margin-bottom: 2rem;
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }
        .card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
        }

        /* ============ Estilo de la tabla ============ */
        .table thead th {
            background: linear-gradient(to right, var(--primary-color), var(--accent-color));
            color: white;
            border: none;
            padding: 15px 10px;
            font-weight: 500;
            text-transform: uppercase;
            font-size: 0.85rem;
            letter-spacing: 0.5px;
        }
        .table tbody tr:hover {
            background-color: rgba(67, 97, 238, 0.05);
            transform: scale(1.01);
        }

        /* ============ Botón primario ============ */
        .btn-primary {
            background: linear-gradient(to right, var(--primary-color), var(--accent-color));
            border: none;
            box-shadow: 0 4px 15px rgba(67, 97, 238, 0.3);
            transition: all 0.3s ease;
        }
        .btn-primary:hover {
            background: linear-gradient(to right, var(--accent-color), var(--primary-color));
            box-shadow: 0 6px 20px rgba(67, 97, 238, 0.4);
            transform: translateY(-2px);
        }

        /* ============ Modal ============ */
        .modal-header {
            background: linear-gradient(to right, var(--primary-color), var(--accent-color));
            color: white;
            border: none;
            padding: 1.5rem;
        }
        .modal-title {
            color: white;
            font-weight: 600;
        }

        /* ============ Inputs enfocados ============ */
        .form-control:focus,
        .form-select:focus {
            border-color: var(--primary-color);
            box-shadow: 0 0 0 0.25rem rgba(67, 97, 238, 0.25);
            background-color: white;
        }

    </style>
</head>
<body>
    <div class="container-fluid py-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Gestión de Actividades</h2>
            <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#actividadModal">
                <i class="fas fa-plus-circle me-2"></i> Nueva Actividad
            </button>
        </div>
        <!-- Tabla de Actividades -->
        <div class="card">
            <div class="card-body">
                <table id="actividadesTable" class="table table-striped table-hover">
                    <thead>
                        <tr>
                            <th>ID Actividad</th>
                            <th>Nombre</th>
                            <th>Enlace</th>
                            <th>Roles</th> <!-- Nueva columna para roles -->
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <!-- Los datos se cargarán dinámicamente -->
                    </tbody>
                </table>
            </div>
        </div>
        <!-- Modal para Crear/Editar Actividad -->
        <div class="modal fade" id="actividadModal" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="modalTitle">Nueva Actividad</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="actividadForm">
                            <input type="hidden" id="actividadId">
                            <div class="mb-3">
                                <label class="form-label">Nombre</label>
                                <input type="text" class="form-control" id="nombre" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Enlace</label>
                                <input type="text" class="form-control" id="enlace" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Roles</label>
                                <select multiple class="form-control" id="roles">
                                    <!-- Opciones de roles se cargarán dinámicamente -->
                                </select>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn btn-primary" onclick="guardarActividad()">Guardar</button>
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
            cargarActividades();
        });

        function cargarActividades() {
            $.ajax({
                url: '${pageContext.request.contextPath}/ActividadesController',
                type: 'GET',
                success: function(data) {
                    const table = $('#actividadesTable').DataTable({
                        data: data,
                        columns: [
                            { data: 'id_actividad' },
                            { data: 'nombre' },
                            { data: 'enlace' },
                            {
                                data: 'roles',
                                render: function(data) {
                                    return data.map(role => role.nombre).join(', ');
                                }
                            },
                            {
                                data: null,
                                render: function(data, type, row) {
                                    return `
                                        <div class="action-buttons">
                                            <button class="btn btn-sm btn-info" onclick="editarActividad(${row.id_actividad})" title="Editar actividad">
                                                <i class="fas fa-edit"></i>
                                            </button>
                                            <button class="btn btn-sm btn-danger" onclick="eliminarActividad(${row.id_actividad})" title="Eliminar actividad">
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
                    alert('Error al cargar las actividades: ' + xhr.responseText);
                }
            });
        }

        function guardarActividad() {
            const actividad = {
                id_actividad: $('#actividadId').val(),
                nombre: $('#nombre').val(),
                enlace: $('#enlace').val(),
                roles: $('#roles').val()
            };

            $.ajax({
                url: '${pageContext.request.contextPath}/ActividadesController',
                type: actividad.id_actividad ? 'PUT' : 'POST',
                contentType: 'application/json',
                data: JSON.stringify(actividad),
                success: function() {
                    $('#actividadModal').modal('hide');
                    $('#actividadesTable').DataTable().ajax.reload();
                },
                error: function(xhr) {
                    alert('Error al guardar la actividad: ' + xhr.responseText);
                }
            });
        }

        function editarActividad(id) {
            $.ajax({
                url: '${pageContext.request.contextPath}/ActividadesController?id=' + id,
                type: 'GET',
                success: function(actividad) {
                    $('#actividadId').val(actividad.id_actividad);
                    $('#nombre').val(actividad.nombre);
                    $('#enlace').val(actividad.enlace);
                    $('#roles').val(actividad.roles.map(role => role.id_rol));
                    $('#actividadModal').modal('show');
                },
                error: function(xhr) {
                    alert('Error al cargar los datos de la actividad: ' + xhr.responseText);
                }
            });
        }

        function eliminarActividad(id) {
            if (confirm('¿Está seguro de que desea eliminar esta actividad?')) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/ActividadesController?id=' + id,
                    type: 'DELETE',
                    success: function() {
                        $('#actividadesTable').DataTable().ajax.reload();
                    },
                    error: function(xhr) {
                        alert('Error al eliminar la actividad: ' + xhr.responseText);
                    }
                });
            }
        }
    </script>
</body>
</html>
