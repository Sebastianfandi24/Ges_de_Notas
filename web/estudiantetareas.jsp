<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Mis Tareas - Estudiante</title>
  <!-- Bootstrap 5 & Bootstrap Icons -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
  <style>
    body {
      background-color: #f8f9fa;
    }
    .table-wrapper {
      border: 1px solid #dee2e6;
      border-radius: .375rem;
      background-color: #fff;
    }
    .table-wrapper table {
      margin-bottom: 0;
    }
    .badge-status {
      border-radius: 10rem;
      padding: .35em .75em;
      font-size: .85em;
    }
  </style>
</head>
<body class="bg-light">
  <div class="container-fluid py-4">
    <!-- Encabezado con título + filtros -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="m-0">Mis Tareas</h2>
      <div class="d-flex gap-2">
        <select class="form-select form-select-sm" style="width:auto;">
          <option selected>Todas las tareas</option>
          <option>Pendientes</option>
          <option>Entregadas</option>
        </select>
        <select class="form-select form-select-sm" style="width:auto;">
          <option selected>Todos los cursos</option>
          <option>Matemáticas Avanzadas</option>
          <option>Programación Java</option>
        </select>
      </div>
    </div>

    <!-- Tabla estática -->
    <div class="table-responsive table-wrapper">
      <table class="table align-middle mb-0">
        <thead>
          <tr>
            <th>Título</th>
            <th>Curso</th>
            <th>Fecha entrega</th>
            <th>Estado</th>
            <th>Calificación</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <!-- Pendientes -->
          <tr>
            <td>Ecuaciones Diferenciales</td>
            <td>Matemáticas Avanzadas</td>
            <td>25/04/2025</td>
            <td>
              <span class="badge bg-warning text-white badge-status">Pendiente</span>
            </td>
            <td>-</td>
            <td>
              <button class="btn btn-sm btn-primary">Entregar</button>
            </td>
          </tr>
          <tr>
            <td>Algoritmos de ordenamiento</td>
            <td>Programación Java</td>
            <td>27/04/2025</td>
            <td>
              <span class="badge bg-warning text-white badge-status">Pendiente</span>
            </td>
            <td>-</td>
            <td>
              <button class="btn btn-sm btn-primary">Entregar</button>
            </td>
          </tr>

          <!-- Entregadas -->
          <tr>
            <td>Matrices y determinantes</td>
            <td>Matemáticas Avanzadas</td>
            <td>10/04/2025</td>
            <td>
              <span class="badge bg-success text-white badge-status">Entregado</span>
            </td>
            <td>8.5</td>
            <td>
              <button class="btn btn-sm btn-secondary">Ver detalles</button>
            </td>
          </tr>
          <tr>
            <td>Clases y objetos</td>
            <td>Programación Java</td>
            <td>05/04/2025</td>
            <td>
              <span class="badge bg-success text-white badge-status">Entregado</span>
            </td>
            <td>9.0</td>
            <td>
              <button class="btn btn-sm btn-secondary">Ver detalles</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>

  <!-- Bootstrap Bundle JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
