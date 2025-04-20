<%-- 
    Document   : admincursos
    Created on : 19 abr 2025, 1:27:58 a.m.
    Author     : pechi
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Cursos</title>
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
            <h2>Gestión de Cursos</h2>
            <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#cursoModal">
                <i class="fas fa-plus-circle me-2"></i> Nuevo Curso
            </button>
        </div>
        <!-- Tabla de Cursos -->
        <div class="card">
            <div class="card-body">
                <table id="cursosTable" class="table table-striped table-hover">
                    <thead>
                        <tr>
                            <th>ID Curso</th>
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
        <!-- Modal para Crear/Editar Curso -->
        <div class="modal fade" id="cursoModal" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="modalTitle">Nuevo Curso</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="cursoForm">
                            <input type="hidden" id="cursoId">
                            <div class="mb-3">
                                <label class="form-label">Nombre</label>
                                <input type="text" class="form-control" id="nombre" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Código</label>
                                <input type="text" class="form-control" id="codigo" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Descripción</label>
                                <textarea class="form-control" id="descripcion" rows="3"></textarea>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Profesor</label>
                                <select class="form-select" id="idProfesor">
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
            cargarCursos();
            cargarProfesores();
        });

        function cargarCursos() {
            if ($.fn.DataTable.isDataTable('#cursosTable')) {
                $('#cursosTable').DataTable().destroy();
            }
            
            $('#cursosTable').DataTable({
                ajax: {
                    url: '${pageContext.request.contextPath}/CursosController',
                    type: 'GET',
                    dataSrc: ''
                },
                columns: [
                    { data: 'id_curso' },
                    { data: 'nombre' },
                    { data: 'codigo' },
                    { data: 'descripcion' },
                    { data: 'profesor_nombre' },
                    {
                        data: null,
                        render: function(data, type, row) {
                            return `
                                <div class="action-buttons">
                                    <button type="button" class="btn btn-sm btn-info" onclick="editarCurso(${row.id_curso})" title="Editar curso">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                    <button type="button" class="btn btn-sm btn-danger" onclick="eliminarCurso(${row.id_curso})" title="Eliminar curso">
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
        }

        function cargarProfesores() {
            $.ajax({
                url: '${pageContext.request.contextPath}/ProfesoresController',
                type: 'GET',
                success: function(data) {
                    const select = $('#idProfesor');
                    select.empty();
                    data.forEach(profesor => {
                        select.append(`<option value="${profesor.id_profesor}">${profesor.nombre}</option>`);
                    });
                },
                error: function(xhr) {
                    alert('Error al cargar los profesores: ' + xhr.responseText);
                }
            });
        }

        function guardarCurso() {
            var cursoData = {
                id_curso: $('#cursoId').val() || null,
                nombre: $('#nombre').val(),
                codigo: $('#codigo').val(),
                descripcion: $('#descripcion').val(),
                idProfesor: parseInt($('#idProfesor').val())
            };

            var method = cursoData.id_curso ? 'PUT' : 'POST';
            var url = cursoData.id_curso 
                ? '${pageContext.request.contextPath}/CursosController?id=' + cursoData.id_curso
                : '${pageContext.request.contextPath}/CursosController';

            $.ajax({
                url: url,
                type: method,
                contentType: 'application/json',
                data: JSON.stringify(cursoData),
                success: function(response) {
                    $('#cursoModal').modal('hide');
                    cargarCursos();
                    limpiarFormulario();
                },
                error: function(xhr, status, error) {
                    console.error('Error:', error);
                    alert('Error al guardar el curso. Por favor, intente nuevamente.');
                }
            });
        }

        function editarCurso(id) {
            if (!id) {
                alert('ID de curso no válido');
                return;
            }
            
            $.ajax({
                url: '${pageContext.request.contextPath}/CursosController?id=' + id,
                type: 'GET',
                dataType: 'json',
                success: function(curso) {
                    if (curso) {
                        $('#cursoId').val(curso.id_curso);
                        $('#nombre').val(curso.nombre);
                        $('#codigo').val(curso.codigo);
                        $('#descripcion').val(curso.descripcion);
                        $('#idProfesor').val(curso.idProfesor);
                        $('#modalTitle').text('Editar Curso');
                        $('#cursoModal').modal('show');
                    } else {
                        alert('No se encontró el curso');
                    }
                },
                error: function(xhr, status, error) {
                    console.error('Error:', error);
                    alert('Error al cargar los datos del curso. Por favor, intente nuevamente.');
                }
            });
        }

        function eliminarCurso(id) {
            if (!id) {
                alert('ID de curso no válido');
                return;
            }
            
            if (confirm('¿Está seguro de que desea eliminar este curso?')) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/CursosController?id=' + id,
                    type: 'DELETE',
                    success: function(response) {
                        cargarCursos();
                    },
                    error: function(xhr, status, error) {
                        console.error('Error:', error);
                        alert('Error al eliminar el curso. Por favor, intente nuevamente.');
                    }
                });
            }
        }

        function limpiarFormulario() {
            $('#cursoId').val('');
            $('#nombre').val('');
            $('#codigo').val('');
            $('#descripcion').val('');
            $('#idProfesor').val('');
            $('#modalTitle').text('Nuevo Curso');
        }

        $('#cursoModal').on('hidden.bs.modal', function () {
            limpiarFormulario();
        });
    </script>
</body>
</html>
