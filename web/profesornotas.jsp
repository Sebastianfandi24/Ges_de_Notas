<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Gestión de Calificaciones</title>
  <!-- Bootstrap 5 & Bootstrap Icons -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
  <style>
    body {
      background-color: #f8f9fa;
    }
    .card-header.bg-primary {
      font-weight: 500;
    }
    .table thead th {
      vertical-align: middle;
    }
    /* Centrar todas las celdas numéricas */
    .table thead th:nth-child(n+2),
    .table tbody td:nth-child(n+2),
    .table tfoot th:nth-child(n+2) {
      text-align: center;
    }
    .list-group-item {
      border: none;
      padding-left: 0;
      padding-right: 0;
    }
    .list-group-item + .list-group-item {
      border-top: 1px solid #dee2e6;
    }
    .progress {
      height: 20px;
    }
    .progress-bar {
      line-height: 20px;
    }
  </style>
</head>
<body>
  <div class="container-fluid py-4">
    <!-- Título y selector de curso -->
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h2 class="m-0">Gestión de Calificaciones</h2>
      <select class="form-select w-auto">
        <option selected>Seleccionar curso</option>
        <!-- Más opciones de curso -->
      </select>
    </div>

    <!-- Tarjeta principal de calificaciones -->
    <div class="card mb-4 shadow-sm">
      <div class="card-header bg-primary text-white">
        <i class="bi bi-table"></i> Matemáticas Avanzadas – Calificaciones
      </div>
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table mb-0">
            <thead>
              <tr>
                <th>Estudiante</th>
                <th class="text-center" colspan="3">Actividades</th>
                <th>Promedio</th>
              </tr>
              <tr>
                <th></th>
                <th>Matrices y determinantes</th>
                <th>Ecuaciones diferenciales</th>
                <th>Examen parcial</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Ana Martínez</td>
                <td>8.5</td>
                <td>9.0</td>
                <td>9.5</td>
                <td>9.0</td>
              </tr>
              <tr>
                <td>Carlos Sánchez</td>
                <td>7.5</td>
                <td>8.0</td>
                <td>7.0</td>
                <td>7.5</td>
              </tr>
              <tr>
                <td>Laura Gómez</td>
                <td>9.0</td>
                <td>8.5</td>
                <td>8.0</td>
                <td>8.5</td>
              </tr>
              <tr>
                <td>Roberto Fernández</td>
                <td>6.5</td>
                <td>7.0</td>
                <td>7.5</td>
                <td>7.0</td>
              </tr>
              <tr>
                <td>Elena Pérez</td>
                <td>8.0</td>
                <td>8.5</td>
                <td>9.0</td>
                <td>8.5</td>
              </tr>
            </tbody>
            <tfoot class="table-light">
              <tr>
                <th>Promedio actividad</th>
                <th>7.9</th>
                <th>8.2</th>
                <th>8.2</th>
                <th>8.1</th>
              </tr>
            </tfoot>
          </table>
        </div>
        <div class="p-3 bg-white border-top d-flex">
          <button class="btn btn-outline-primary me-2">
            <i class="bi bi-file-earmark-excel"></i> Exportar a Excel
          </button>
          <button class="btn btn-outline-info">
            <i class="bi bi-bar-chart-line"></i> Ver estadísticas
          </button>
        </div>
      </div>
    </div>

    <!-- Estadísticas y Distribución -->
    <div class="row g-4">
      <!-- Estadísticas del curso -->
      <div class="col-md-6">
        <div class="card shadow-sm">
          <div class="card-header bg-primary text-white">
            Estadísticas del curso
          </div>
          <ul class="list-group list-group-flush">
            <li class="list-group-item d-flex justify-content-between">
              <span>Promedio general:</span>
              <strong>8.1 / 10</strong>
            </li>
            <li class="list-group-item d-flex justify-content-between">
              <span>Calificación más alta:</span>
              <strong>9.5 / 10</strong>
            </li>
            <li class="list-group-item d-flex justify-content-between">
              <span>Calificación más baja:</span>
              <strong>6.5 / 10</strong>
            </li>
            <li class="list-group-item d-flex justify-content-between">
              <span>Estudiantes aprobados:</span>
              <strong>15 / 15 (100%)</strong>
            </li>
          </ul>
        </div>
      </div>

      <!-- Distribución de calificaciones -->
      <div class="col-md-6">
        <div class="card shadow-sm">
          <div class="card-header bg-primary text-white">
            Distribución de calificaciones
          </div>
          <div class="card-body">
            <!-- 9–10 -->
            <div class="mb-3">
              <div class="d-flex justify-content-between">
                <small>9–10</small>
                <small>30%</small>
              </div>
              <div class="progress">
                <div class="progress-bar bg-success" role="progressbar"
                     style="width: 30%;" aria-valuenow="30" aria-valuemin="0" aria-valuemax="100">
                </div>
              </div>
            </div>
            <!-- 8–9 -->
            <div class="mb-3">
              <div class="d-flex justify-content-between">
                <small>8–9</small>
                <small>40%</small>
              </div>
              <div class="progress">
                <div class="progress-bar bg-primary" role="progressbar"
                     style="width: 40%;" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100">
                </div>
              </div>
            </div>
            <!-- 7–8 -->
            <div class="mb-3">
              <div class="d-flex justify-content-between">
                <small>7–8</small>
                <small>20%</small>
              </div>
              <div class="progress">
                <div class="progress-bar bg-info" role="progressbar"
                     style="width: 20%;" aria-valuenow="20" aria-valuemin="0" aria-valuemax="100">
                </div>
              </div>
            </div>
            <!-- 6–7 -->
            <div class="mb-0">
              <div class="d-flex justify-content-between">
                <small>6–7</small>
                <small>10%</small>
              </div>
              <div class="progress">
                <div class="progress-bar bg-warning" role="progressbar"
                     style="width: 10%;" aria-valuenow="10" aria-valuemin="0" aria-valuemax="100">
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Bootstrap Bundle JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
