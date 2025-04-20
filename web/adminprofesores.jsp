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
                                        <input type="password" class="form-control" id="contrasena">
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
            // Variable global para la tabla DataTable
            let profesoresTable;

            $(document).ready(function() {
                cargarProfesores();

                // Cuando se cierra el modal, limpiar el formulario
                $('#profesorModal').on('hidden.bs.modal', function() {
                    $('#profesorForm').trigger('reset');
                    $('#profesorId').val('');
                });
            });

            function cargarProfesores() {
                $.ajax({
                    url: '${pageContext.request.contextPath}/ProfesoresController',
                    type: 'GET',
                    success: function(data) {
                        // Destruir la tabla existente si ya está inicializada
                        if ($.fn.DataTable.isDataTable('#profesoresTable')) {
                            $('#profesoresTable').DataTable().destroy();
                        }
                        
                        // Limpiar contenido de la tabla
                        $('#profesoresTable tbody').empty();
                        
                        // Inicializar la tabla con los nuevos datos
                        profesoresTable = $('#profesoresTable').DataTable({
                            data: data,
                            columns: [
                                { data: 'id_profesor' },
                                { data: 'nombre' },
                                { data: 'correo' },
                                { data: 'telefono' },
                                { data: 'grado_academico' },
                                { data: 'especializacion' },
                                { data: 'estado' },
                                { data: 'contraseña_descodificada' }, // Mostrar contraseña protegida
                                {
                                    data: null,
                                    render: function(data, type, row) {
                                        // Asegurarse de que row.id_profesor tenga un valor válido
                                        const profesorId = row.id_profesor;
                                        if (profesorId === undefined || profesorId === null) {
                                            console.error("ID de profesor no encontrado en los datos de la fila:", row);
                                            return ''; // No renderizar botones si el ID no es válido
                                        }
                                        return `
                                            <div class="action-buttons">
                                                <button class="btn btn-sm btn-info" onclick="editarProfesor(${profesorId})" title="Editar profesor">
                                                    <i class="fas fa-edit"></i>
                                                </button>
                                                <button class="btn btn-sm btn-danger" onclick="eliminarProfesor(${profesorId})" title="Eliminar profesor">
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
                        mostrarAlerta('error', 'Error al cargar los profesores: ' + xhr.responseText);
                    }
                });
            }

            function guardarProfesor() {
                const profesor = {
                    id_profesor: $('#profesorId').val(),
                    id_usu: $('#profesorId').val() ? $('#id_usu').val() : "",
                    nombre: $('#nombre').val(),
                    correo: $('#correo').val(),
                    contrasena: $('#contrasena').val(), // Cambiado a 'contrasena' sin ñ
                    telefono: $('#telefono').val(),
                    grado_academico: $('#gradoAcademico').val(),
                    especializacion: $('#especializacion').val(),
                    fecha_contratacion: $('#fechaContratacion').val(),
                    estado: $('#estado').val(),
                    direccion: $('#direccion').val()
                };

                console.log("Datos enviados al backend:", profesor); // Log para depuración
                
                // Determinar si es una creación o actualización
                const esActualizacion = profesor.id_profesor ? true : false;
                
                $.ajax({
                    url: '${pageContext.request.contextPath}/ProfesoresController',
                    type: esActualizacion ? 'PUT' : 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(profesor),
                    success: function(response) {
                        $('#profesorModal').modal('hide');
                        mostrarAlerta('success', esActualizacion ? 'Profesor actualizado correctamente' : 'Profesor creado correctamente');
                        cargarProfesores(); // Recargar la tabla con datos actualizados
                    },
                    error: function(xhr) {
                        mostrarAlerta('error', 'Error al guardar el profesor: ' + xhr.responseText);
                    }
                });
            }

            function editarProfesor(id) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/ProfesoresController?id=' + id,
                    type: 'GET',
                    success: function(response) {
                        console.log("Datos del profesor recibidos:", response);
                        
                        // Si la respuesta es un array, tomar el profesor correcto
                        let profesor;
                        if (Array.isArray(response)) {
                            // Buscar el profesor con el ID correcto
                            profesor = response.find(p => p.id_profesor === id);
                            if (!profesor) {
                                profesor = response[0]; // Si no encontramos coincidencia, usar el primero
                                console.warn("No se encontró el profesor con ID:", id, "usando el primer profesor del array");
                            }
                        } else {
                            profesor = response;
                        }
                        
                        console.log("Profesor seleccionado para editar:", profesor);
                        
                        $('#profesorId').val(profesor.id_profesor);
                        // Campo oculto para id_usu
                        if (!$('#id_usu').length) {
                            $('#profesorForm').append('<input type="hidden" id="id_usu" value="' + profesor.id_usu + '">');
                        } else {
                            $('#id_usu').val(profesor.id_usu);
                        }
                        $('#nombre').val(profesor.nombre);
                        $('#correo').val(profesor.correo);
                        $('#contrasena').val(''); // La contraseña no se carga por seguridad
                        $('#telefono').val(profesor.telefono);
                        $('#gradoAcademico').val(profesor.grado_academico);
                        $('#especializacion').val(profesor.especializacion);
                        $('#estado').val(profesor.estado);
                        $('#direccion').val(profesor.direccion);
                        
                        // Formato de fechas - Asegurarse de que las fechas se carguen correctamente
                        if (profesor.fechaNacimiento) {
                            $('#fechaNacimiento').val(profesor.fechaNacimiento);
                        }
                        if (profesor.fechaContratacion) {
                            $('#fechaContratacion').val(profesor.fechaContratacion);
                        }
                        
                        $('#modalTitle').text('Editar Profesor');
                        $('#profesorModal').modal('show');
                    },
                    error: function(xhr) {
                        console.error("Error al cargar profesor:", xhr.responseText); // Depuración
                        mostrarAlerta('error', 'Error al cargar los datos del profesor: ' + xhr.responseText);
                    }
                });
            }

            function eliminarProfesor(id) {
                // Validación del ID
                if (!id || isNaN(id)) {
                    console.error('ID de profesor inválido:', id);
                    mostrarAlerta('error', 'ID de profesor inválido');
                    return;
                }

                if (confirm('¿Está seguro de que desea eliminar este profesor?')) {
                    console.log("Intentando eliminar profesor con ID:", id);
                    
                    $.ajax({
                        url: '${pageContext.request.contextPath}/ProfesoresController?id=' + id,
                        type: 'DELETE',
                        success: function(response) {
                            console.log("Profesor eliminado exitosamente:", response);
                            mostrarAlerta('success', 'Profesor eliminado correctamente');
                            cargarProfesores(); // Recargar la tabla después de eliminar
                        },
                        error: function(xhr, status, error) {
                            console.error("Error al eliminar profesor:", {
                                xhr: xhr,
                                status: status,
                                error: error,
                                response: xhr.responseText
                            });
                            mostrarAlerta('error', 'Error al eliminar el profesor: ' + xhr.responseText);
                        }
                    });
                }
            }
            
            // Función para mostrar alertas
            function mostrarAlerta(tipo, mensaje) {
                let alertClass = 'alert-info';
                if (tipo === 'success') alertClass = 'alert-success';
                if (tipo === 'error') alertClass = 'alert-danger';
                
                const alertHtml = `
                <div class="alert ${alertClass} alert-dismissible fade show" role="alert">
                    ${mensaje}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>`;
                
                // Insertar alerta en la parte superior
                const alertContainer = document.createElement('div');
                alertContainer.className = 'container mt-3';
                alertContainer.innerHTML = alertHtml;
                
                // Insertar antes de la tabla
                const cardElement = document.querySelector('.card');
                cardElement.parentNode.insertBefore(alertContainer, cardElement);
                
                // Ocultar automáticamente después de 3 segundos
                setTimeout(function() {
                    $(alertContainer).fadeOut('slow', function() {
                        $(this).remove();
                    });
                }, 3000);
            }
        </script>
    </body>
</html>
