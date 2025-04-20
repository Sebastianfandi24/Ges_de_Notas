<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Dashboard Profesor</title>

  <!-- Bootstrap 5 + Bootstrap Icons -->
  <link
    href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
    rel="stylesheet"
  />
  <link
    href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css"
    rel="stylesheet"
  />

  <style>
    body {
      background-color: #f8f9fa;
    }
    .dashboard-card {
      background-color: #fff;
      border-radius: 12px;
      padding: 1.5rem;
      box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.08);
      transition: transform 0.3s ease, box-shadow 0.3s ease;
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
      color: #0d6efd;
      opacity: 0.8;
    }
    .table-section {
      margin-top: 2rem;
    }
    .table-section .table thead th {
      background-color: #0d6efd;
      color: #fff;
    }
    .badge-circle {
      border-radius: 50%;
      padding: 0.5em 0.75em;
    }
  </style>
</head>

<body>
  <div class="container-fluid py-4">
    <!-- Métricas superiores -->
    <div class="row g-4 mb-4">
      <div class="col-md-3">
        <div class="dashboard-card d-flex justify-content-between align-items-center">
          <div>
            <div class="card-title">Mis Cursos</div>
            <div class="card-value">3</div>
          </div>
          <i class="bi bi-journal-text card-icon"></i>
        </div>
      </div>
      <div class="col-md-3">
        <div class="dashboard-card d-flex justify-content-between align-items-center">
          <div>
            <div class="card-title">Estudiantes</div>
            <div class="card-value">45</div>
          </div>
          <i class="bi bi-people card-icon"></i>
        </div>
      </div>
      <div class="col-md-3">
        <div class="dashboard-card d-flex justify-content-between align-items-center">
          <div>
            <div class="card-title">Tareas Pendientes</div>
            <div class="card-value">12</div>
          </div>
          <i class="bi bi-list-task card-icon"></i>
        </div>
      </div>
      <div class="col-md-3">
        <div class="dashboard-card d-flex justify-content-between align-items-center">
          <div>
            <div class="card-title">Promedio General</div>
            <div class="card-value">7.8</div>
          </div>
          <i class="bi bi-star card-icon"></i>
        </div>
      </div>
    </div>

    <!-- Tablas secundarias -->
    <div class="row table-section">
      <!-- Cursos Activos -->
      <div class="col-lg-7 mb-4">
        <div class="table-responsive">
          <table class="table align-middle mb-0">
            <thead>
              <tr>
                <th>Mis Cursos Activos</th>
                <th>Estudiantes</th>
                <th>Progreso</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Matemáticas Avanzadas</td>
                <td>15</td>
                <td style="min-width:150px;">
                  <div class="progress" style="height:10px;">
                    <div
                      class="progress-bar bg-success"
                      role="progressbar"
                      style="width:75%;"
                      aria-valuenow="75"
                      aria-valuemin="0"
                      aria-valuemax="100"
                    ></div>
                  </div>
                </td>
                <td>
                  <button class="btn btn-sm btn-primary">Ver curso</button>
                </td>
              </tr>
              <tr>
                <td>Álgebra Lineal</td>
                <td>12</td>
                <td>
                  <div class="progress" style="height:10px;">
                    <div
                      class="progress-bar bg-warning"
                      role="progressbar"
                      style="width:45%;"
                      aria-valuenow="45"
                      aria-valuemin="0"
                      aria-valuemax="100"
                    ></div>
                  </div>
                </td>
                <td>
                  <button class="btn btn-sm btn-primary">Ver curso</button>
                </td>
              </tr>
              <tr>
                <td>Cálculo Diferencial</td>
                <td>18</td>
                <td>
                  <div class="progress" style="height:10px;">
                    <div
                      class="progress-bar"
                      style="background-color:#17a2b8; width:60%;"
                      role="progressbar"
                      aria-valuenow="60"
                      aria-valuemin="0"
                      aria-valuemax="100"
                    ></div>
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

      <!-- Tareas pendientes por calificar -->
      <div class="col-lg-5 mb-4">
        <div class="table-responsive">
          <table class="table align-middle mb-0">
            <thead>
              <tr>
                <th>Tareas pendientes por calificar</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Ecuaciones Diferenciales</td>
                <td class="text-end">
                  <span class="badge bg-primary text-white badge-circle">8</span>
                </td>
              </tr>
              <tr>
                <td>Matrices y Determinantes</td>
                <td class="text-end">
                  <span class="badge bg-primary text-white badge-circle">4</span>
                </td>
              </tr>
              <tr>
                <td>Examen parcial de Cálculo</td>
                <td class="text-end">
                  <span class="badge bg-primary text-white badge-circle">12</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="p-3 bg-white rounded-bottom shadow-sm">
          <button class="btn btn-primary">Ver todas las tareas</button>
        </div>
      </div>
    </div>
  </div>

  <!-- Bootstrap Bundle JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
