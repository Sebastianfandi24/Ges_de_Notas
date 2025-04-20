<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Dashboard Estudiante</title>
  <!-- Bootstrap 5 + Bootstrap Icons -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
  <style>
    body {
      background-color: #f8f9fa;
    }
    .dashboard-card {
      background-color: #fff;
      border-radius: 12px;
      padding: 1.5rem;
      box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.08);
      transition: all 0.3s ease;
      height: 100%;
    }
    .dashboard-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 0.75rem 1.5rem rgba(0, 0, 0, 0.12);
    }
    .dashboard-card .card-title {
      color: #6c757d;
      font-size: 0.9rem;
      font-weight: 500;
    }
    .dashboard-card .card-value {
      color: #212529;
      font-size: 2rem;
      font-weight: 700;
    }
    .dashboard-card .card-icon {
      font-size: 2.5rem;
      opacity: 0.8;
      color: #0d6efd;
    }
    .table-container {
      margin-top: 2rem;
    }
    .table-container .table thead {
      background-color: #0d6efd;
      color: #fff;
    }
  </style>
</head>
<body>
  <div class="container-fluid py-4">
    <!-- Resumen superior -->
    <div class="row g-4 mb-4">
      <div class="col-md-3">
        <div class="dashboard-card d-flex justify-content-between align-items-center">
          <div>
            <div class="card-title">Mis Cursos</div>
            <div class="card-value">2</div>
          </div>
          <i class="bi bi-journal-text card-icon"></i>
        </div>
      </div>
      <div class="col-md-3">
        <div class="dashboard-card d-flex justify-content-between align-items-center">
          <div>
            <div class="card-title">Tareas Pendientes</div>
            <div class="card-value">3</div>
          </div>
          <i class="bi bi-list-task card-icon"></i>
        </div>
      </div>
      <div class="col-md-3">
        <div class="dashboard-card d-flex justify-content-between align-items-center">
          <div>
            <div class="card-title">Promedio</div>
            <div class="card-value">8.5</div>
          </div>
          <i class="bi bi-star card-icon"></i>
        </div>
      </div>
      <div class="col-md-3">
        <div class="dashboard-card d-flex justify-content-between align-items-center">
          <div>
            <div class="card-title">Actividades Próximas</div>
            <div class="card-value">1</div>
          </div>
          <i class="bi bi-calendar-event card-icon"></i>
        </div>
      </div>
    </div>

    <!-- Tabla de cursos activos -->
    <div class="table-container">
      <h5 class="mb-3 text-white bg-primary p-2 rounded">Mis Cursos Activos</h5>
      <div class="table-responsive">
        <table class="table align-middle">
          <thead>
            <tr>
              <th>Curso</th>
              <th>Profesor</th>
              <th>Progreso</th>
              <th>Ver detalles</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Matemáticas Avanzadas</td>
              <td>Prof. Juan Pérez</td>
              <td>
                <div class="progress" style="height: 10px;">
                  <div class="progress-bar bg-success" role="progressbar" style="width: 75%;"
                       aria-valuenow="75" aria-valuemin="0" aria-valuemax="100"></div>
                </div>
              </td>
              <td>
                <button class="btn btn-sm btn-primary">Ver curso</button>
              </td>
            </tr>
            <tr>
              <td>Programación Java</td>
              <td>Prof. Ana García</td>
              <td>
                <div class="progress" style="height: 10px;">
                  <div class="progress-bar bg-warning" role="progressbar" style="width: 45%;"
                       aria-valuenow="45" aria-valuemin="0" aria-valuemax="100"></div>
                </div>
              </td>
              <td>
                <button class="btn btn-sm btn-primary">Ver curso</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>

  <!-- Bootstrap Bundle JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
