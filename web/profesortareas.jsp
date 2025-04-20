<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Gestión de Tareas</title>

  <!-- Bootstrap 5 + Bootstrap Icons -->
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
      overflow: hidden;
    }
    .table-wrapper .table {
      margin-bottom: 0;
    }
    .badge {
      font-size: .85rem;
      padding: .4em .75em;
    }
  </style>
</head>
<body>
  <div class="container-fluid py-4">
    <!-- Encabezado con filtros -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="m-0">Gestión de Tareas</h2>
      <div class="d-flex gap-2">
        <select class="form-select form-select-sm" style="width:auto;">
          <option selected>Todos los cursos</option>
          <option>Matemáticas Avanzadas</option>
          <option>Álgebra Lineal</option>
          <option>Cálculo Diferencial</option>
        </select>
        <select class="form-select form-select-sm" style="width:auto;">
          <option selected>Todos los estados</option>
          <option>Activa</option>
          <option>Por calificar</option>
          <option>Calificada</option>
        </select>
      </div>
    </div>

    <!-- Tabla principal -->
    <div class="table-responsive table-wrapper">
      <table class="table align-middle mb-0">
        <thead>
          <tr>
            <th>Título</th>
            <th>Curso</th>
            <th>Fecha entrega</th>
            <th>Entregas</th>
            <th>Estado</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <!-- Tarea Activa -->
          <tr>
            <td>Ecuaciones Diferenciales</td>
            <td>Matemáticas Avanzadas</td>
            <td>25/04/2025</td>
            <td>8/15</td>
            <td>
              <span class="badge bg-info text-white">Activa</span>
            </td>
            <td>
              <button class="btn btn-outline-primary btn-sm">Ver entregas</button>
              <button class="btn btn-outline-warning btn-sm">Editar</button>
            </td>
          </tr>
          <!-- Tarea por calificar -->
          <tr>
            <td>Matrices y Determinantes</td>
            <td>Álgebra Lineal</td>
            <td>15/04/2025</td>
            <td>10/12</td>
            <td>
              <span class="badge bg-warning text-white">Por calificar</span>
            </td>
            <td>
              <button class="btn btn-warning btn-sm">Calificar</button>
              <button class="btn btn-outline-warning btn-sm">Editar</button>
            </td>
          </tr>
          <!-- Tarea Calificada -->
          <tr>
            <td>Examen parcial</td>
            <td>Cálculo Diferencial</td>
            <td>10/04/2025</td>
            <td>18/18</td>
            <td>
              <span class="badge bg-success text-white">Calificada</span>
            </td>
            <td>
              <button class="btn btn-outline-primary btn-sm">Ver calificaciones</button>
              <button class="btn btn-outline-info btn-sm">Estadísticas</button>
            </td>
          </tr>
          <tr>
            <td>Límites y continuidad</td>
            <td>Cálculo Diferencial</td>
            <td>01/04/2025</td>
            <td>15/18</td>
            <td>
              <span class="badge bg-success text-white">Calificada</span>
            </td>
            <td>
              <button class="btn btn-outline-primary btn-sm">Ver calificaciones</button>
              <button class="btn btn-outline-info btn-sm">Estadísticas</button>
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
