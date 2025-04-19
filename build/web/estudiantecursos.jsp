<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mis Cursos - Estudiante</title>
    <style>
        .course-card {
            transition: transform 0.2s;
        }
        .course-card:hover {
            transform: translateY(-5px);
        }
        .task-list {
            max-height: 200px;
            overflow-y: auto;
        }
        .grade-badge {
            font-size: 1.2rem;
        }
    </style>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
    <div class="container-fluid py-4">
        <h2 class="mb-4">Mis Cursos</h2>

        <!-- Vista de Cursos en Cards -->
        <div class="row">
            <div class="col-md-4 mb-4">
                <div class="card course-card h-100">
                    <div class="card-body">
                        <h5 class="card-title">Introducción a la Programación</h5>
                        <h6 class="card-subtitle mb-2 text-muted">Código: CS101</h6>
                        <p class="card-text">Curso introductorio de programación.</p>
                        <div class="d-flex justify-content-between align-items-center">
                            <small class="text-muted">Profesor: Profesor User</small>
                            <button class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#cursoDetalleModal">
                                <i class="fas fa-info-circle"></i> Ver Detalles
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-4 mb-4">
                <div class="card course-card h-100">
                    <div class="card-body">
                        <h5 class="card-title">Bases de Datos</h5>
                        <h6 class="card-subtitle mb-2 text-muted">Código: CS102</h6>
                        <p class="card-text">Curso sobre diseño y gestión de bases de datos.</p>
                        <div class="d-flex justify-content-between align-items-center">
                            <small class="text-muted">Profesor: Profesor User</small>
                            <button class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#cursoDetalleModal">
                                <i class="fas fa-info-circle"></i> Ver Detalles
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal para Ver Detalles del Curso -->
        <div class="modal fade" id="cursoDetalleModal" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Introducción a la Programación</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-6">
                                <h6>Información del Curso</h6>
                                <p><strong>Código:</strong> CS101</p>
                                <p><strong>Profesor:</strong> Profesor User</p>
                                <p><strong>Descripción:</strong></p>
                                <p>Curso introductorio de programación.</p>
                            </div>
                            <div class="col-md-6">
                                <h6>Mi Progreso</h6>
                                <div class="text-center mb-3">
                                    <span class="badge bg-primary grade-badge">85%</span>
                                    <p class="text-muted">Promedio Actual</p>
                                </div>
                            </div>
                        </div>
                        <hr>
                        <h6>Tareas Pendientes</h6>
                        <div class="list-group task-list">
                            <a href="#" class="list-group-item list-group-item-action">
                                <div class="d-flex w-100 justify-content-between">
                                    <h6 class="mb-1">Tarea 1</h6>
                                    <small class="text-danger">Entrega: 15/04/2023</small>
                                </div>
                                <small class="text-muted">Resolver ejercicios de lógica.</small>
                            </a>
                            <a href="#" class="list-group-item list-group-item-action">
                                <div class="d-flex w-100 justify-content-between">
                                    <h6 class="mb-1">Tarea 2</h6>
                                    <small class="text-danger">Entrega: 20/04/2023</small>
                                </div>
                                <small class="text-muted">Diseñar una base de datos simple.</small>
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>