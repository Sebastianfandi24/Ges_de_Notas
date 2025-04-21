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
            <button id="btnNuevaActividad" class="btn btn-primary" onclick="abrirNuevaActividad()">
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
                                <select class="form-control" id="enlace" required>
                                    <!-- Opciones de JSP nuevos se cargarán dinámicamente -->
                                </select>
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
        var tableActividades;
        $(document).ready(function() {
            initActividadesTable();
            cargarRolesActividades(); // cargar roles disponibles
            cargarJspsNuevos(); // cargar JSPs nuevos disponibles
        });

        function cargarRolesActividades() {
            $.ajax({
                url: '${pageContext.request.contextPath}/ActividadesController?getRoles=1',
                type: 'GET',
                success: function(data) {
                    var $rolesSelect = $('#roles');
                    $rolesSelect.empty();
                    if (typeof data === "string") { data = JSON.parse(data); }
                    $.each(data, function(i, role) {
                        $rolesSelect.append('<option value="'+role.id_rol+'">'+role.nombre+'</option>');
                    });
                },
                error: function(xhr) {
                    mostrarAlerta('error', "Error al cargar roles: " + xhr.responseText);
                }
            });
        }

        function cargarJspsNuevos() {
            $.ajax({
                url: '${pageContext.request.contextPath}/ActividadesController?getNewJsps=1',
                type: 'GET',
                success: function(data) {
                    var $select = $('#enlace');
                    $select.empty();
                    if (typeof data === 'string') data = JSON.parse(data);
                    if (!data.length) {
                        $select.append('<option disabled>No hay nuevos enlaces</option>');
                    } else {
                        $.each(data, function(i, jsp) {
                            $select.append('<option value="'+jsp+'">'+jsp+'</option>');
                        });
                    }
                },
                error: function(xhr) {
                    mostrarAlerta('error', 'Error al cargar enlaces: ' + xhr.responseText);
                }
            });
        }

        function initActividadesTable() {
            tableActividades = $('#actividadesTable').DataTable({
                ajax: {
                    url: '${pageContext.request.contextPath}/ActividadesController',
                    dataSrc: ''
                },
                columns: [
                    { data: 'id_actividad' },
                    { data: 'nombre' },
                    { data: 'enlace' },
                    { data: 'roles', render: function(roles) {
                        return roles.map(function(r) { return r.nombre; }).join(', ');
                    }},
                    { data: null, orderable: false, defaultContent:
                        '<div class="action-buttons">'
                        + '<button class="btn btn-sm btn-info edit-btn" title="Editar actividad">'
                            + '<i class="fas fa-edit"></i>'
                        + '</button>'
                        + '<button class="btn btn-sm btn-danger delete-btn" title="Eliminar actividad">'
                            + '<i class="fas fa-trash-alt"></i>'
                        + '</button>'
                        + '</div>'
                    }
                ],
                language: {
                    url: 'https://cdn.datatables.net/plug-ins/1.11.5/i18n/es-ES.json'
                }
            });
            $('#actividadesTable tbody').on('click', '.edit-btn', function() {
                var data = tableActividades.row($(this).closest('tr')).data();
                editarActividad(data.id_actividad);
            });
            $('#actividadesTable tbody').on('click', '.delete-btn', function() {
                var data = tableActividades.row($(this).closest('tr')).data();
                eliminarActividad(data.id_actividad);
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
                    tableActividades.ajax.reload();
                },
                error: function(xhr) {
                    mostrarAlerta('error', 'Error al guardar la actividad: ' + xhr.responseText);
                }
            });
        }

        function editarActividad(id) {
            $.ajax({
                url: '${pageContext.request.contextPath}/ActividadesController?id=' + id,
                type: 'GET',
                dataType: 'json',
                success: function(actividad) {
                    $('#actividadId').val(actividad.id_actividad);
                    $('#nombre').val(actividad.nombre);
                    $('#enlace').val(actividad.enlace);
                    $('#roles').val(actividad.roles.map(function(r) { return r.id_rol; }));
                    $('#actividadModal').modal('show');
                },
                error: function(xhr) {
                    var msg;
                    try { msg = JSON.parse(xhr.responseText).error; } catch(e) { msg = xhr.statusText; }
                    mostrarAlerta('error', 'Error al cargar la actividad: ' + msg);
                }
            });
        }

        function eliminarActividad(id) {
            console.log("Eliminar actividad llamado con ID:", id); // Nuevo log para depuración
            if (!id || isNaN(id)) {
                mostrarAlerta('error', 'ID de actividad inválido');
                return;
            }
            if (confirm('¿Está seguro de que desea eliminar esta actividad?')) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/ActividadesController',
                    type: 'POST', // usar POST en vez de DELETE
                    data: { id: id, _method: 'DELETE' },  // enviar _method=DELETE
                    dataType: 'json',
                    success: function(response) {
                        mostrarAlerta('success', 'Actividad eliminada correctamente');
                        tableActividades.ajax.reload();
                    },
                    error: function(xhr) {
                        var msg;
                        try { 
                            msg = JSON.parse(xhr.responseText).error; 
                        } catch(e) { 
                            msg = xhr.statusText; 
                        }
                        mostrarAlerta('error', 'Error al eliminar la actividad: ' + msg);
                    }
                });
            }
        }

        // Limpia el formulario al crear nueva actividad
        function abrirNuevaActividad() {
            $('#actividadId').val('');
            $('#nombre').val('');
            $('#roles').val([]);
            cargarJspsNuevos(); // refrescar lista de JSPs en cada nuevo formulario
            $('#modalTitle').text('Nueva Actividad');
            $('#actividadModal').modal('show');
        }

        function mostrarAlerta(tipo, mensaje) {
            let alertClass = 'alert-info';
            if (tipo === 'success') alertClass = 'alert-success';
            if (tipo === 'error') alertClass = 'alert-danger';
            
            const alertHtml = `<div class="alert ${alertClass} alert-dismissible fade show" role="alert">
                ${mensaje}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>`;
            
            if ($('#alertContainer').length === 0) {
                $('body').prepend('<div id="alertContainer" class="container mt-3"></div>');
            }
            $('#alertContainer').html(alertHtml);
            
            setTimeout(function() {
                $('.alert').fadeOut('slow', function() {
                    $(this).remove();
                });
            }, 3000);
        }
    </script>
</body>
</html>