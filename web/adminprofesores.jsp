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
CSS -->
        <link href="https://cdn.jsdelivr.net/str/boot3.0ap@5.2.3/dist/css/bootstrap.min.csstylesheet">
l="stylesheet">
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
