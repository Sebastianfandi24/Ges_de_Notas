<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Estudiante</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="#">Sistema Académico</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link active" href="#" onclick="cargarMisCursos()">Mis Cursos</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#" onclick="cargarMisTareas()">Mis Tareas</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#" onclick="cargarMisNotas()">Mis Notas</a>
                    </li>
                </ul>
                <form class="d-flex" action="/logout" method="post">
                    <button class="btn btn-outline-light" type="submit">Cerrar Sesión</button>
                </form>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <div id="contenido"></div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        const idEstudiante = <%= session.getAttribute("idUsuario") != null ? session.getAttribute("idUsuario") : "null" %>;
        console.log('ID de estudiante cargado:', idEstudiante);
        
        if (idEstudiante === "null") {
            window.location.href = "/login.jsp";
        }

        function cargarMisCursos() {
            fetch(`/estudiante/cursos/mis-cursos?id_estudiante=${idEstudiante}`)
                .then(response => response.json())
                .then(cursos => {
                    let html = '<h2>Mis Cursos</h2><div class="row">';
                    cursos.forEach(curso => {
                        html += `
                            <div class="col-md-4 mb-4">
                                <div class="card">
                                    <div class="card-body">
                                        <h5 class="card-title">${curso.nombre}</h5>
                                        <h6 class="card-subtitle mb-2 text-muted">Código: ${curso.codigo}</h6>
                                        <p class="card-text">${curso.descripcion}</p>
                                        <p class="card-text"><small class="text-muted">Profesor: ${curso.profesor_nombre}</small></p>
                                    </div>
                                </div>
                            </div>`;
                    });
                    html += '</div>';
                    document.getElementById('contenido').innerHTML = html;
                })
                .catch(error => console.error('Error:', error));
        }

        function formatearFecha(fechaStr) {
            if (!fechaStr) return 'No disponible';
            try {
                const fecha = new Date(fechaStr);
                if (isNaN(fecha.getTime())) return 'Fecha inválida';
                return fecha.toLocaleDateString('es-ES', {
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric'
                });
            } catch (error) {
                return 'Error en fecha';
            }
        }

        function cargarMisTareas() {
            fetch(`/estudiante/tareas/mis-tareas?id_estudiante=${idEstudiante}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Error en la respuesta del servidor');
                    }
                    return response.json();
                })
                .then(tareas => {
                    if (!Array.isArray(tareas)) {
                        throw new Error('Formato de datos inválido');
                    }
                    let html = '<h2>Mis Tareas</h2><div class="table-responsive"><table class="table">';
                    html += `
                        <thead>
                            <tr>
                                <th>Título</th>
                                <th>Curso</th>
                                <th>Fecha Entrega</th>
                                <th>Estado</th>
                                <th>Nota</th>
                            </tr>
                        </thead>
                        <tbody>`;
                    tareas.forEach(tarea => {
                        let estado = 'Sin fecha';
                        let badgeClass = 'bg-secondary';
                        
                        if (tarea.fecha_entrega) {
                            const fechaEntrega = new Date(tarea.fecha_entrega);
                            const hoy = new Date();
                            
                            if (!isNaN(fechaEntrega.getTime())) {
                                if (fechaEntrega > hoy) {
                                    estado = 'Pendiente';
                                    badgeClass = 'bg-warning';
                                } else {
                                    estado = 'Vencida';
                                    badgeClass = 'bg-danger';
                                }
                            }                            
                        }
                        html += `
                            <tr>
                                <td>${tarea.titulo || 'Sin título'}</td>
                                <td>${tarea.curso_nombre || 'Sin curso'}</td>
                                <td>${formatearFecha(tarea.fecha_entrega)}</td>
                                <td><span class="badge ${badgeClass}">${estado}</span></td>
                                <td>${tarea.nota !== null && tarea.nota !== undefined ? tarea.nota : 'Sin calificar'}</td>
                            </tr>`;
                    });
                    html += '</tbody></table></div>';
                    document.getElementById('contenido').innerHTML = html;
                })
                .catch(error => {
                    console.error('Error:', error);
                    document.getElementById('contenido').innerHTML = `
                        <div class="alert alert-danger" role="alert">
                            Error al cargar las tareas: ${error.message}
                        </div>`;
                });
        }

        function cargarMisNotas() {
            fetch(`/estudiante/tareas/notas?id_estudiante=${idEstudiante}`)
                .then(response => response.json())
                .then(notas => {
                    let html = '<h2>Mis Notas</h2><div class="table-responsive"><table class="table">';
                    html += `
                        <thead>
                            <tr>
                                <th>Tarea</th>
                                <th>Curso</th>
                                <th>Nota</th>
                                <th>Fecha Evaluación</th>
                                <th>Comentario</th>
                            </tr>
                        </thead>
                        <tbody>`;
                    notas.forEach(nota => {
                        html += `
                            <tr>
                                <td>${nota.tarea_titulo || 'Sin título'}</td>
                                <td>${nota.curso_nombre || 'Sin curso'}</td>
                                <td>${nota.nota || '-'}</td>
                                <td>${formatearFecha(nota.fecha_evaluacion)}</td>
                                <td>${nota.comentario || '-'}</td>
                            </tr>`;
                    });
                    html += '</tbody></table></div>';
                    document.getElementById('contenido').innerHTML = html;
                })
                .catch(error => console.error('Error:', error));
        }

        // Cargar mis cursos al iniciar
        cargarMisCursos();
    </script>
</body>
</html>