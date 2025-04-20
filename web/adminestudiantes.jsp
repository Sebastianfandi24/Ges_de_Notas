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
            <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#estudianteModal" onclick="abrirModalNuevo()">
                <i class="fas fa-plus-circle me-2"></i> Nuevo Estudiante
            </button>
        </div>
        <!-- Tabla de Estudiantes -->
        <div class="card">
            <div class="card-body">
                <table id="estudiantesTable" class="table table-striped table-hover w-100">
                    <thead>
                        <tr>
                            <th>ID Est.</th>
                            <th>ID Usu.</th>
                            <th>Nombre</th>
                            <th>Correo</th>
                            <th>Teléfono</th>
                            <th>Estado</th>
                            <th>Promedio</th>
                            <th>Rol ID</th>
                            <th>F. Creación</th>
                            <th>Últ. Conexión</th>
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
                            <input type="hidden" id="usuarioId"> <!-- Campo oculto para id_usuario -->
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label class="form-label">Nombre <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="nombre" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Correo <span class="text-danger">*</span></label>
                                    <input type="email" class="form-control" id="correo" required>
                                </div>
                            </div>
                            <!-- Fila de Contraseña (visible) y Rol ID (oculto) -->
                            <div class="row mb-3">
                                <div class="col-md-6"> <!-- Contraseña VISIBLE -->
                                    <label class="form-label">Contraseña <span id="contraseñaObligatoria" class="text-danger">*</span></label>
                                    <input type="password" class="form-control" id="contraseña" placeholder="Dejar en blanco para no cambiar">
                                    <small id="contraseñaHelp" class="form-text text-muted">Requerida para nuevos estudiantes.</small>
                                </div>
                                <div class="col-md-6" style="display: none;"> <!-- Rol ID OCULTO -->
                                    <label class="form-label">Rol ID <span class="text-danger">*</span></label>
                                    <input type="number" class="form-control" id="idRol" value="1" required readonly>
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
                                    <label class="form-label">Número Identificación</label>
                                    <input type="text" class="form-control" id="numeroIdentificacion">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Estado</label>
                                    <select class="form-select" id="estado">
                                        <option value="Activo">Activo</option>
                                        <option value="Inactivo">Inactivo</option>
                                    </select>
                                </div>
                            </div>
                            <!-- Fila con Promedio Académico (oculto) y Dirección (visible) -->
                            <div class="row mb-3">
                                <div class="col-md-6" style="display: none;"> <!-- Promedio OCULTO -->
                                    <label class="form-label">Promedio Académico</label>
                                    <input type="number" step="0.01" class="form-control" id="promedioAcademico" placeholder="(Se calcula automáticamente)" readonly>
                                </div>
                                <div class="col-md-6"> <!-- Dirección VISIBLE -->
                                    <label class="form-label">Dirección</label>
                                    <textarea class="form-control" id="direccion" rows="1"></textarea>
                                </div>
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
        let tablaEstudiantes;

        $(document).ready(function() {
            inicializarTabla();
        });

        function inicializarTabla() {
            if (tablaEstudiantes) {
                tablaEstudiantes.destroy();
            }

            tablaEstudiantes = $('#estudiantesTable').DataTable({
                ajax: {
                    url: '${pageContext.request.contextPath}/EstudiantesController',
                    dataSrc: '',
                    error: function(xhr, error, thrown) {
                        console.error('Error al cargar datos:', error);
                        alert('Error al cargar los estudiantes. Por favor, recargue la página.');
                    }
                },
                columns: [
                    { data: 'id_estudiante' },
                    { data: 'id_usuario' },
                    { data: 'nombre' },
                    { data: 'correo' },
                    { data: 'telefono' },
                    { data: 'estado' },
                    { data: 'promedio_academico' },
                    { data: 'id_rol' },
                    { data: 'fecha_creacion' },
                    { data: 'ultima_conexion' },
                    {
                        data: 'id_estudiante',
                        orderable: false,
                        render: function(idEst) {
                            idEst = parseInt(idEst);
                            if (!idEst || isNaN(idEst)) {
                                return '<span class="text-danger">ID inválido</span>';
                            }
                            return '<div class="btn-group btn-group-sm" role="group">'
                                + '<button type="button" class="btn btn-info btn-edit" data-id="'+idEst+'" title="Editar"><i class="fas fa-edit"></i></button>'
                                + '<button type="button" class="btn btn-danger btn-delete" data-id="'+idEst+'" title="Eliminar"><i class="fas fa-trash-alt"></i></button>'
                                + '</div>';
                        }
                    }
                ],
                language: {
                    url: 'https://cdn.datatables.net/plug-ins/1.11.5/i18n/es-ES.json'
                },
                responsive: true,
                processing: true,
                scrollX: true
            });

            // Delegación de eventos para Editar y Eliminar
            $('#estudiantesTable tbody').off('click', '.btn-edit').on('click', '.btn-edit', function() {
                editarEstudiante($(this).data('id'));
            });
            $('#estudiantesTable tbody').off('click', '.btn-delete').on('click', '.btn-delete', function() {
                eliminarEstudiante($(this).data('id'));
            });
        }

        function cargarEstudiantes() {
            tablaEstudiantes.ajax.reload();
        }

        function abrirModalNuevo() {
            $('#estudianteForm')[0].reset(); // Limpiar formulario
            $('#estudianteId').val('');
            $('#usuarioId').val('');
            $('#modalTitle').text('Nuevo Estudiante');
            $('#contraseña').attr('placeholder', 'Contraseña (requerida)').prop('required', true);
            $('#contraseñaObligatoria').show();
            $('#contraseñaHelp').show();
            $('#idRol').val(1); // Rol fijo a 1
            $('#estado').val('Activo'); // Estado activo por defecto
        }

        function guardarEstudiante() {
            const esNuevo = !$('#estudianteId').val();
            const contraseñaInput = $('#contraseña').val();

            // Validar contraseña para nuevos estudiantes
            if (esNuevo && !contraseñaInput) {
                alert('La contraseña es obligatoria para nuevos estudiantes.');
                $('#contraseña').focus();
                return;
            }

            const estudiante = {
                id_estudiante: $('#estudianteId').val() || null,
                id_usuario: $('#usuarioId').val() || null,
                nombre: $('#nombre').val(),
                correo: $('#correo').val(),
                // contrasena: Se añadirá condicionalmente abajo
                id_rol: 1, // Fijado a 1
                fecha_nacimiento: $('#fechaNacimiento').val() || null,
                telefono: $('#telefono').val() || null,
                numero_identificacion: $('#numeroIdentificacion').val() || null,
                estado: $('#estado').val(),
                direccion: $('#direccion').val() || null
            };

            // Añadir contrasena solo si no está vacía
            if (contraseñaInput) {
                estudiante.contrasena = contraseñaInput;
            } else if (!esNuevo) {
                 // Si es edición y la contraseña está vacía, NO enviar la clave
                 // El backend interpretará esto como "no cambiar contraseña"
                 // (Asegúrate que el backend maneje esto correctamente si es necesario)
                 // Opcionalmente, podrías enviar null si tu backend lo prefiere:
                 // estudiante.contrasena = null;
            }

            // Limpiar campos nulos innecesarios para la petición
            Object.keys(estudiante).forEach(key => {
                // No eliminar contrasena si se añadió explícitamente
                if ((estudiante[key] === null || estudiante[key] === '') && key !== 'contrasena') {
                    delete estudiante[key];
                }
            });

            // Asegurarse de que id_estudiante y id_usuario no se envíen en POST
            if (esNuevo) {
                delete estudiante.id_estudiante;
                delete estudiante.id_usuario;
            }

            console.log("Enviando estudiante:", JSON.stringify(estudiante)); // Log para depuración

            $.ajax({
                url: '${pageContext.request.contextPath}/EstudiantesController' + (esNuevo ? '' : '?id=' + estudiante.id_estudiante),
                type: esNuevo ? 'POST' : 'PUT',
                contentType: 'application/json',
                data: JSON.stringify(estudiante),
                success: function(response) {
                    console.log("Respuesta servidor:", response);
                    $('#estudianteModal').modal('hide');
                    cargarEstudiantes(); // Recargar la tabla
                },
                error: function(xhr) {
                    console.error('Error al guardar el estudiante:', xhr);
                    try {
                         const errorJson = JSON.parse(xhr.responseText);
                         alert('Error al guardar: ' + errorJson.error);
                    } catch (e) {
                         alert('Error al guardar el estudiante: ' + xhr.responseText);
                    }
                }
            });
        }

        function editarEstudiante(id) {
            // Verificar que el ID sea válido
            if (!id || isNaN(id)) {
                console.error('ID de estudiante inválido:', id);
                alert('Error: ID de estudiante inválido');
                return;
            }

            console.log('Editando estudiante con ID:', id);
            
            $.ajax({
                url: '${pageContext.request.contextPath}/EstudiantesController?id=' + parseInt(id),
                type: 'GET',
                success: function(estudiante) {
                    console.log('Datos del estudiante recibidos:', estudiante);
                    $('#estudianteForm')[0].reset();
                    $('#modalTitle').text('Editar Estudiante');
                    $('#estudianteId').val(estudiante.id_estudiante);
                    $('#usuarioId').val(estudiante.id_usuario);
                    $('#nombre').val(estudiante.nombre);
                    $('#correo').val(estudiante.correo);
                    $('#contraseña').attr('placeholder', 'Dejar en blanco para no cambiar').prop('required', false);
                    $('#contraseñaObligatoria').hide();
                    $('#contraseñaHelp').hide();
                    $('#idRol').val(1);
                    $('#fechaNacimiento').val(estudiante.fecha_nacimiento || '');
                    $('#telefono').val(estudiante.telefono || '');
                    $('#numeroIdentificacion').val(estudiante.numero_identificacion || '');
                    $('#estado').val(estudiante.estado || 'Activo');
                    $('#direccion').val(estudiante.direccion || '');
                    $('#estudianteModal').modal('show');
                },
                error: function(xhr) {
                    console.error('Error al cargar datos del estudiante:', xhr.responseText);
                    let errorMsg = 'Error al cargar los datos del estudiante';
                    try {
                        const errorObj = JSON.parse(xhr.responseText);
                        if (errorObj.error) {
                            errorMsg = errorObj.error;
                        }
                    } catch (e) {
                        console.error('Error al parsear respuesta:', e);
                    }
                    alert(errorMsg);
                }
            });
        }

        function eliminarEstudiante(id) {
            // Validar que el ID sea un número válido
            if (!id || isNaN(id)) {
                console.error('Error: ID de estudiante inválido', id);
                alert('Error: No se puede eliminar el estudiante. ID inválido.');
                return;
            }
            
            console.log('Intentando eliminar estudiante con ID:', id);
            
            if (confirm('¿Está seguro de que desea eliminar este estudiante y su usuario asociado? Esta acción no se puede deshacer.')) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/EstudiantesController?id=' + id,
                    type: 'DELETE',
                    success: function() {
                        console.log('Estudiante eliminado con éxito, ID:', id);
                        cargarEstudiantes(); // Recargar la tabla
                    },
                    error: function(xhr) {
                        console.error('Error al eliminar el estudiante:', xhr);
                        let errorMsg = 'Error al eliminar el estudiante';
                        try {
                            const errorObj = JSON.parse(xhr.responseText);
                            if (errorObj.error) {
                                errorMsg = errorObj.error;
                            }
                        } catch (e) {
                            console.error('Error al parsear respuesta:', e);
                        }
                        alert(errorMsg);
                    }
                });
            }
        }
    </script>
</body>
</html>
