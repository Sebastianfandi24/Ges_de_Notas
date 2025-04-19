<%-- 
    Document   : adminprofesores
    Created on : 19 abr 2025, 1:27:26 a.m.
    Author     : pechi
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Gestión de Profesores</title>
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- DataTables CSS -->
        <link href="https://cdn.datatables.net/1.11.5/css/dataTables.bootstrap5.min.css" rel="stylesheet">
        <!-- Font Awesome -->
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
        <style>
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
            .form-control:focus, .form-select:focus {
                border-color: var(--primary-color);
                box-shadow: 0 0 0 0.25rem rgba(67, 97, 238, 0.25);
                background-color: white;
            }
        </style>
    </head>
    <body>
        <div class="container-fluid py-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2>Gestión de Profesores</h2>
                <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#profesorModal">
                    <i class="fas fa-plus-circle me-2"></i> Nuevo Profesor
                </button>
            </div>
            <!-- Tabla de Profesores -->
            <div class="card">
                <div class="card-body">
                    <table id="profesoresTable" class="table table-striped table-hover">
                        <thead>
                            <tr>
                                <th>ID Profesor</th>
                                <th>Nombre</th>
                                <th>Correo</th>
                                <th>Teléfono</th>
                                <th>Grado Académico</th>
                                <th>Especialización</th>
                                <th>Estado</th>
                                <th>Contraseña</th> <!-- Mostrar contraseña -->
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <!-- Los datos se cargarán dinámicamente -->
                        </tbody>
                    </table>
                </div>
            </div>
            <!-- Modal para Crear/Editar Profesor -->
            <div class="modal fade" id="profesorModal" tabindex="-1">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="modalTitle">Nuevo Profesor</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <form id="profesorForm">
                                <input type="hidden" id="profesorId">
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
                                        <label class="form-label">Grado Académico</label>
                                        <select class="form-select" id="gradoAcademico">
                                            <option value="Licenciatura">Licenciatura</option>
                                            <option value="Maestría">Maestría</option>
                                            <option value="Doctorado">Doctorado</option>
                                        </select>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">Especialización</label>
                                        <input type="text" class="form-control" id="especializacion">
                                    </div>
                                </div>
                                <div class="row mb-3">
                                    <div class="col-md-6">
                                        <label class="form-label">Fecha de Contratación</label>
                                        <input type="date" class="form-control" id="fechaContratacion">
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">Estado</label>
                                        <select class="form-select" id="estado">
                                            <option value="Activo">Activo</option>
                                            <option value="Baja">Baja</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="row mb-3">
                                    <div class="col-md-6">
                                        <label class="form-label">Contraseña</label>
                                        <input type="password" class="form-control" id="contraseña">
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
                            <button type="button" class="btn btn-primary" onclick="guardarProfesor()">Guardar</button>
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
                cargarProfesores();
            });

            function cargarProfesores() {
                $.ajax({
                    url: '${pageContext.request.contextPath}/ProfesoresController',
                    type: 'GET',
                    success: function(data) {
                        const table = $('#profesoresTable').DataTable({
                            data: data,
                            columns: [
                                { data: 'id_profesor' },
                                { data: 'nombre' },
                                { data: 'correo' },
                                { data: 'telefono' },
                                { data: 'grado_academico' },
                                { data: 'especializacion' },
                                { data: 'estado' },
                                { data: 'contraseña_descodificada' }, // Mostrar contraseña descodificada
                                {
                                    data: null,
                                    render: function(data, type, row) {
                                        return `
                                            <div class="action-buttons">
                                                <button class="btn btn-sm btn-info" onclick="editarProfesor(${row.id_profesor})" title="Editar profesor">
                                                    <i class="fas fa-edit"></i>
                                                </button>
                                                <button class="btn btn-sm btn-danger" onclick="eliminarProfesor(${row.id_profesor})" title="Eliminar profesor">
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
                        alert('Error al cargar los profesores: ' + xhr.responseText);
                    }
                });
            }

            function guardarProfesor() {
                const profesor = {
                    id_profesor: $('#profesorId').val(),
                    nombre: $('#nombre').val(),
                    correo: $('#correo').val(),
                    contraseña: $('#contraseña').val(), // Asegurarse de enviar la contraseña
                    telefono: $('#telefono').val(),
                    grado_academico: $('#gradoAcademico').val(),
                    especializacion: $('#especializacion').val(),
                    fecha_contratacion: $('#fechaContratacion').val(),
                    estado: $('#estado').val(),
                    direccion: $('#direccion').val()
                };

                console.log("Datos enviados al backend:", profesor); // Log para depuración

                $.ajax({
                    url: '${pageContext.request.contextPath}/ProfesoresController',
                    type: profesor.id_profesor ? 'PUT' : 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(profesor),
                    success: function() {
                        $('#profesorModal').modal('hide');
                        $('#profesoresTable').DataTable().ajax.reload();
                    },
                    error: function(xhr) {
                        alert('Error al guardar el profesor: ' + xhr.responseText);
                    }
                });
            }

            function editarProfesor(id) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/ProfesoresController?id=' + id,
                    type: 'GET',
                    success: function(profesor) {
                        $('#profesorId').val(profesor.id_profesor);
                        $('#nombre').val(profesor.nombre);
                        $('#correo').val(profesor.correo);
                        $('#contraseña').val(profesor.contraseña); // Cargar contraseña
                        $('#fechaNacimiento').val(profesor.fechaNacimiento);
                        $('#telefono').val(profesor.telefono);
                        $('#gradoAcademico').val(profesor.gradoAcademico);
                        $('#especializacion').val(profesor.especializacion);
                        $('#fechaContratacion').val(profesor.fechaContratacion);
                        $('#estado').val(profesor.estado);
                        $('#direccion').val(profesor.direccion);
                        $('#profesorModal').modal('show');
                    },
                    error: function(xhr) {
                        alert('Error al cargar los datos del profesor: ' + xhr.responseText);
                    }
                });
            }

            function eliminarProfesor(id) {
                if (confirm('¿Está seguro de que desea eliminar este profesor?')) {
                    $.ajax({
                        url: '${pageContext.request.contextPath}/ProfesoresController?id=' + id,
                        type: 'DELETE',
                        success: function() {
                            $('#profesoresTable').DataTable().ajax.reload();
                        },
                        error: function(xhr) {
                            alert('Error al eliminar el profesor: ' + xhr.responseText);
                        }
                    });
                }
            }
        </script>
    </body>
</html>
