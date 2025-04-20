<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Mis Cursos</title>
  <!-- Bootstrap 5 + Bootstrap Icons -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
  <style>
    body {
      background-color: #f8f9fa;
    }
    .course-card {
      border: 1px solid #dee2e6;
      border-radius: .5rem;
      background-color: #fff;
      overflow: hidden;
      margin-bottom: 1.5rem;
    }
    .course-card-header {
      background-color: #0d6efd;
      color: #fff;
      padding: .75rem 1.25rem;
      font-size: 1.25rem;
      font-weight: 500;
    }
    .course-card-body {
      padding: 1rem 1.25rem;
    }
    .course-card-body p {
      margin-bottom: .75rem;
      font-size: .95rem;
    }
    .course-card-footer {
      background-color: #f1f1f1;
      padding: .75rem 1.25rem;
      text-align: left;
    }
    .course-card-footer .btn + .btn {
      margin-left: .5rem;
    }
    .progress {
      height: 10px;
      margin-bottom: .75rem;
    }
  </style>
</head>
<body>
  <div class="container-fluid py-4">
    <!-- Título + botón -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="m-0">Mis Cursos</h2>
      <button class="btn btn-primary">
        <i class="bi bi-plus-circle me-1"></i> Nueva Actividad
      </button>
    </div>

    <!-- Tarjetas de cursos -->
    <div class="row">
      <!-- Curso 1 -->
      <div class="col-md-4">
        <div class="course-card">
          <div class="course-card-header">Matemáticas Avanzadas</div>
          <div class="course-card-body">
            <p><strong>Código:</strong> MAT101</p>
            <p><strong>Estudiantes:</strong> 15</p>
            <p><strong>Progreso:</strong></p>
            <div class="progress">
              <div class="progress-bar bg-success" role="progressbar"
                   style="width:75%" aria-valuenow="75" aria-valuemin="0" aria-valuemax="100"></div>
            </div>
            <p><strong>Promedio del curso:</strong> 8.2/10</p>
          </div>
          <div class="course-card-footer">
            <button class="btn btn-outline-primary">Ver estudiantes</button>
            <button class="btn btn-outline-primary">Ver actividades</button>
            <button class="btn btn-outline-primary">Calificaciones</button>
          </div>
        </div>
      </div>

      <!-- Curso 2 -->
      <div class="col-md-4">
        <div class="course-card">
          <div class="course-card-header">Álgebra Lineal</div>
          <div class="course-card-body">
            <p><strong>Código:</strong> MAT202</p>
            <p><strong>Estudiantes:</strong> 12</p>
            <p><strong>Progreso:</strong></p>
            <div class="progress">
              <div class="progress-bar bg-warning" role="progressbar"
                   style="width:45%" aria-valuenow="45" aria-valuemin="0" aria-valuemax="100"></div>
            </div>
            <p><strong>Promedio del curso:</strong> 7.5/10</p>
          </div>
          <div class="course-card-footer">
            <button class="btn btn-outline-primary">Ver estudiantes</button>
            <button class="btn btn-outline-primary">Ver actividades</button>
            <button class="btn btn-outline-primary">Calificaciones</button>
          </div>
        </div>
      </div>

      <!-- Curso 3 -->
      <div class="col-md-4">
        <div class="course-card">
          <div class="course-card-header">Cálculo Diferencial</div>
          <div class="course-card-body">
            <p><strong>Código:</strong> MAT303</p>
            <p><strong>Estudiantes:</strong> 18</p>
            <p><strong>Progreso:</strong></p>
            <div class="progress">
              <div class="progress-bar" role="progressbar"
                   style="background-color:#17a2b8; width:60%" aria-valuenow="60"
                   aria-valuemin="0" aria-valuemax="100"></div>
            </div>
            <p><strong>Promedio del curso:</strong> 7.9/10</p>
          </div>
          <div class="course-card-footer">
            <button class="btn btn-outline-primary">Ver estudiantes</button>
            <button class="btn btn-outline-primary">Ver actividades</button>
            <button class="btn btn-outline-primary">Calificaciones</button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Bootstrap Bundle JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
