<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Mis Cursos - Estudiante</title>
  <!-- Bootstrap 5 & Bootstrap Icons -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <style>
    body {
      background-color: #f8f9fa;
    }
    .course-card {
      border: 1px solid #dee2e6;
      border-radius: .5rem;
      overflow: hidden;
      background-color: #fff;
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
      text-align: right;
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
<body class="bg-light">
  <div class="container py-4">
    <h2 class="mb-1">Mis Cursos</h2>
    <p class="text-muted mb-4">Lista de cursos en los que estás matriculado</p>

    <div class="row">
      <!-- Card Curso 1 -->
      <div class="col-md-6">
        <div class="course-card">
          <div class="course-card-header">
            Matemáticas Avanzadas
          </div>
          <div class="course-card-body">
            <p><strong>Profesor:</strong> Juan Pérez</p>
            <p><strong>Código:</strong> MAT101</p>
            <p><strong>Progreso:</strong></p>
            <div class="progress">
              <div class="progress-bar bg-success" role="progressbar"
                   style="width: 75%;" aria-valuenow="75"
                   aria-valuemin="0" aria-valuemax="100"></div>
            </div>
            <p><strong>Calificación actual:</strong> 8.5/10</p>
          </div>
          <div class="course-card-footer">
            <button class="btn btn-primary">Ver detalles</button>
            <button class="btn btn-outline-primary">Ver tareas</button>
          </div>
        </div>
      </div>

      <!-- Card Curso 2 -->
      <div class="col-md-6">
        <div class="course-card">
          <div class="course-card-header">
            Programación Java
          </div>
          <div class="course-card-body">
            <p><strong>Profesor:</strong> Ana García</p>
            <p><strong>Código:</strong> PRG202</p>
            <p><strong>Progreso:</strong></p>
            <div class="progress">
              <div class="progress-bar bg-warning" role="progressbar"
                   style="width: 45%;" aria-valuenow="45"
                   aria-valuemin="0" aria-valuemax="100"></div>
            </div>
            <p><strong>Calificación actual:</strong> 7.8/10</p>
          </div>
          <div class="course-card-footer">
            <button class="btn btn-primary">Ver detalles</button>
            <button class="btn btn-outline-primary">Ver tareas</button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Bootstrap Bundle JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
