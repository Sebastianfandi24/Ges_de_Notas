<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Cursos</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.datatables.net/1.11.5/css/dataTables.bootstrap5.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        .action-buttons .btn {
            margin-right: 5px;
        }
    </style>
</head>
<body>
    <div class="container-fluid py-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Gestión de Cursos</h2>
            <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#cursoModal">
                <i class="fas fa-plus-circle me-2"></i> Nuevo Curso
            </button>
        </div>

        <div class="card">
            <div class="card-body">
                <div class="table-responsive">
                    <table id="cursosTable" class="table table-striped table-hover">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nombre</th>
                                <th>Código</th>
                                <th>Descripción</th>
                                <th>Profesor</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <!-- Los datos se cargarán dinámicamente -->
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Modal para Crear/Editar Curso -->
        <div class="modal fade" id="cursoModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="modalTitle">Nuevo Curso</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="cursoForm">
                            <input type="hidden" id="cursoId">
                            <div class="mb-3">
                                <label for="nombre" class="form-label">Nombre</label>
                                <input type="text" class="form-control" id="nombre" required>
                            </div>
                            <div class="mb-3">
                                <label for="codigo" class="form-label">Código</label>
                                <input type="text" class="form-control" id="codigo" required>
                            </div>
                            <div class="mb-3">
                                <label for="descripcion" class="form-label">Descripción</label>
                                <textarea class="form-control" id="descripcion" rows="3"></textarea>
                            </div>
                            <div class="mb-3">
                                <label for="idProfesor" class="form-label">Profesor</label>
                                <select class="form-select" id="idProfesor" required>
                                    <!-- Opciones cargadas dinámicamente -->
                                </select>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn btn-primary" onclick="guardarCurso()">Guardar</button>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Área para mostrar alertas -->
        <div id="alertContainer"></div>
    </div>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.datatables.net/1.11.5/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/1.11.5/js/dataTables.bootstrap5.min.js"></script>
    
    <script>
        let cursosTable;
        
        $(document).ready(function() {
            cargarProfesores().then(cargarCursos).catch(function() {
                mostrarAlerta('error', 'No se pudo cargar profesores');
            });

            // Delegación de eventos para editar/eliminar usando data-id
            $('#cursosTable').on('click', '.btn-editar', function() {
                const id = $(this).data('id');
                editarCurso(id);
            });
            $('#cursosTable').on('click', '.btn-eliminar', function() {
                const id = $(this).data('id');
                eliminarCurso(id);
            });

            // Cuando se cierra el modal, limpiar el formulario
            $('#cursoModal').on('hidden.bs.modal', function() {
                $('#cursoForm').trigger('reset');
                $('#cursoId').val('');
                $('#modalTitle').text('Nuevo Curso');
            });
            // Cada vez que se muestra el modal, recargar profesores
            $('#cursoModal').on('shown.bs.modal', function() {
                cargarProfesores();
            });
        });
        
        function cargarCursos() {
            console.log("Cargando cursos...");
            $.ajax({
                url: '${pageContext.request.contextPath}/CursosController',
                type: 'GET',
                success: function(data) {
                    console.log("Datos recibidos:", data);
                    
                    if ($.fn.DataTable.isDataTable('#cursosTable')) {
                        $('#cursosTable').DataTable().destroy();
                    }
                    
                    $('#cursosTable tbody').empty();
                    
                    cursosTable = $('#cursosTable').DataTable({
                        data: data,
                        columns: [
                            { data: 'id_curso' },
                            { data: 'nombre' },
                            { data: 'codigo' },
                            { data: 'descripcion' },
                            { data: 'profesor_nombre' },
                            {
                                data: null,
                                render: function(d) {
                                    var cursoId = d.id_curso;
                                    return '<div class="action-buttons">'
                                        + '<button class="btn btn-sm btn-info btn-editar" data-id="' + cursoId + '" title="Editar curso">'
                                        + '<i class="fas fa-edit"></i>'
                                        + '</button>'
                                        + '<button class="btn btn-sm btn-danger btn-eliminar" data-id="' + cursoId + '" title="Eliminar curso">'
                                        + '<i class="fas fa-trash-alt"></i>'
                                        + '</button>'
                                        + '</div>';
                                }
                            }
                        ],
                        language: {
                            url: 'https://cdn.datatables.net/plug-ins/1.11.5/i18n/es-ES.json'
                        }
                    });
                },
                error: function(xhr) {
                    console.error("Error al cargar cursos:", xhr.responseText);
                    mostrarAlerta('error', 'Error al cargar los cursos');
                }
            });
        }
        
        function cargarProfesores() {
            return $.ajax({
                url: '${pageContext.request.contextPath}/ProfesoresController',
                type: 'GET',
                dataType: 'json',  // Asegurar parsing a JSON
                success: function(data) {
                    console.log("Profesores recibidos:", data);
                    // Manejar array u objeto único
                    var lista = Array.isArray(data) ? data : [data];
                    const select = $('#idProfesor');
                    select.empty();
                    select.append('<option value="">Seleccione un profesor</option>');
                    
                    lista.forEach(function(profesor) {
                        select.append('<option value="'+profesor.id_profesor+'">'+profesor.nombre+'</option>');
                    });
                },
                error: function(xhr) {
                    console.error("Error al cargar profesores:", xhr.responseText);
                    mostrarAlerta('error', 'Error al cargar la lista de profesores');
                }
            });
        }
        
        // Editar curso: recargar profesores antes de cargar datos
        function editarCurso(id) {
            if (id === undefined || id === null) {
                console.error("ID de curso inválido:", id);
                mostrarAlerta('error', 'ID de curso inválido');
                return;
            }
            // Recargar profesores y luego obtener datos del curso
            cargarProfesores().then(function() {
                return $.ajax({
                    url: '${pageContext.request.contextPath}/CursosController?id=' + id,
                    type: 'GET'
                });
            }).then(function(response) {
                console.log("Datos del curso recibidos:", response);
                $('#cursoId').val(response.id_curso);
                $('#nombre').val(response.nombre);
                $('#codigo').val(response.codigo);
                $('#descripcion').val(response.descripcion);
                $('#idProfesor').val(response.idProfesor);
                $('#modalTitle').text('Editar Curso');
                $('#cursoModal').modal('show');
            }).catch(function(xhr) {
                console.error("Error al cargar curso o profesores:", xhr.responseText || xhr);
                mostrarAlerta('error', 'Error al cargar los datos del curso o la lista de profesores');
            });
        }
        
        function eliminarCurso(id) {
            console.log("Eliminando curso con ID:", id);
            
            if (id === undefined || id === null) {
                console.error("ID de curso inválido:", id);
                mostrarAlerta('error', 'ID de curso inválido');
                return;
            }
            
            if (confirm('¿Está seguro de que desea eliminar este curso?')) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/CursosController?id=' + id,
                    type: 'DELETE',
                    success: function(response) {
                        console.log("Curso eliminado exitosamente:", response);
                        mostrarAlerta('success', 'Curso eliminado correctamente');
                        cargarCursos();
                    },
                    error: function(xhr) {
                        console.error("Error al eliminar curso:", xhr.responseText);
                        mostrarAlerta('error', 'Error al eliminar el curso');
                    }
                });
            }
        }
        
        function guardarCurso() {
            const curso = {
                id_curso: $('#cursoId').val() || null,
                nombre: $('#nombre').val().trim(),
                codigo: $('#codigo').val().trim(),
                descripcion: $('#descripcion').val().trim(),
                idProfesor: parseInt($('#idProfesor').val())
            };
            
            console.log("Datos del curso a guardar:", curso);
            
            if (!validarFormulario(curso)) {
                return;
            }

            const esEdicion = curso.id_curso !== null;
            const metodo = esEdicion ? 'PUT' : 'POST';
            const url = esEdicion 
                ? '${pageContext.request.contextPath}/CursosController?id=' + curso.id_curso
                : '${pageContext.request.contextPath}/CursosController';
            
            $.ajax({
                url: url,
                type: metodo,
                contentType: 'application/json',
                data: JSON.stringify(curso),
                success: function(response) {
                    console.log("Curso guardado exitosamente:", response);
                    $('#cursoModal').modal('hide');
                    mostrarAlerta('success', esEdicion ? 'Curso actualizado correctamente' : 'Curso creado correctamente');
                    cargarCursos();
                },
                error: function(xhr) {
                    console.error("Error al guardar curso:", xhr.responseText);
                    mostrarAlerta('error', 'Error al guardar el curso');
                }
            });
        }
        
        function validarFormulario(curso) {
            if (!curso.nombre) {
                mostrarAlerta('error', 'El nombre es requerido');
                return false;
            }
            if (!curso.codigo) {
                mostrarAlerta('error', 'El código es requerido');
                return false;
            }
            if (!curso.idProfesor) {
                mostrarAlerta('error', 'Debe seleccionar un profesor');
                return false;
            }
            return true;
        }
        
        function mostrarAlerta(tipo, mensaje) {
            let alertClass = 'alert-info';
            if (tipo === 'success') alertClass = 'alert-success';
            if (tipo === 'error') alertClass = 'alert-danger';
            
            const alertHtml = `
            <div class="alert ${alertClass} alert-dismissible fade show" role="alert">
                ${mensaje}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>`;
            
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
