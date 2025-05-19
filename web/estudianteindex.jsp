<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.sql.*" %>
<%
    if (session == null || session.getAttribute("userId") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    Object idEstudianteObj = session.getAttribute("id_estudiante");
    Object userIdObj = session.getAttribute("userId");
    Integer idEstudiante = null;
    if (idEstudianteObj != null) {
        idEstudiante = Integer.parseInt(idEstudianteObj.toString());
    } else if (userIdObj != null) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sistema_academico", "root", "");
            ps = conn.prepareStatement("SELECT id_estudiante FROM estudiante WHERE idUsuario = ?");
            ps.setInt(1, Integer.parseInt(userIdObj.toString()));
            rs = ps.executeQuery();
            if (rs.next()) {
                idEstudiante = rs.getInt("id_estudiante");
                session.setAttribute("id_estudiante", idEstudiante);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception e) {}
            if (ps != null) try { ps.close(); } catch (Exception e) {}
            if (conn != null) try { conn.close(); } catch (Exception e) {}
        }
    }

    // Obtener cursos activos del estudiante
    java.util.List<java.util.Map<String, Object>> cursos = new java.util.ArrayList<>();
    int tareasPendientes = 0;
    double promedio = 0.0;
    int totalNotas = 0;
    if (idEstudiante != null) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sistema_academico", "root", "");
            // Cursos activos
            String sql = "SELECT c.*, u.nombre as profesor_nombre FROM curso c " +
                         "INNER JOIN curso_estudiante ce ON c.id_curso = ce.id_curso " +
                         "LEFT JOIN profesor p ON c.idProfesor = p.id_profesor " +
                         "LEFT JOIN usuario u ON p.idUsuario = u.id_usu " +
                         "WHERE ce.id_estudiante = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idEstudiante);
            rs = ps.executeQuery();
            while (rs.next()) {
                java.util.Map<String, Object> curso = new java.util.HashMap<>();
                curso.put("nombre", rs.getString("nombre"));
                curso.put("codigo", rs.getString("codigo"));
                curso.put("profesor", rs.getString("profesor_nombre"));
                curso.put("descripcion", rs.getString("descripcion"));
                cursos.add(curso);
            }
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            // Tareas pendientes y promedio
            String sqlTareas = "SELECT nt.nota FROM tarea t " +
                               "INNER JOIN curso_estudiante ce ON t.id_curso = ce.id_curso " +
                               "LEFT JOIN nota_tarea nt ON nt.id_tarea = t.id_tarea AND nt.id_estudiante = ? " +
                               "WHERE ce.id_estudiante = ?";
            ps = conn.prepareStatement(sqlTareas);
            ps.setInt(1, idEstudiante);
            ps.setInt(2, idEstudiante);
            rs = ps.executeQuery();
            while (rs.next()) {
                Double nota = (Double) rs.getObject("nota");
                if (nota == null) {
                    tareasPendientes++;
                } else {
                    promedio += nota;
                    totalNotas++;
                }
            }
            if (totalNotas > 0) promedio = promedio / totalNotas;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception e) {}
            if (ps != null) try { ps.close(); } catch (Exception e) {}
            if (conn != null) try { conn.close(); } catch (Exception e) {}
        }
    }
%>
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
            <div class="card-value"><%= cursos.size() %></div>
          </div>
          <i class="bi bi-journal-text card-icon"></i>
        </div>
      </div>
      <div class="col-md-3">
        <div class="dashboard-card d-flex justify-content-between align-items-center">
          <div>
            <div class="card-title">Tareas Pendientes</div>
            <div class="card-value"><%= tareasPendientes %></div>
          </div>
          <i class="bi bi-list-task card-icon"></i>
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
            <% if (cursos.isEmpty()) { %>
              <tr>
                <td colspan="4" class="text-center">No tienes cursos activos.</td>
              </tr>
            <% } else {
              for (java.util.Map<String, Object> curso : cursos) { %>
              <tr>
                <td><%= curso.get("nombre") %></td>
                <td><%= curso.get("profesor") != null ? curso.get("profesor") : "Sin asignar" %></td>
                <td>
                  <div class="progress" style="height: 10px;">
                    <div class="progress-bar bg-success" role="progressbar" style="width: 100%;"
                         aria-valuenow="100" aria-valuemin="0" aria-valuemax="100"></div>
                  </div>
                </td>
                <td>
                  <a href="estudiantetareas.jsp?curso=<%= curso.get("codigo") %>" class="btn btn-sm btn-primary">Ver curso</a>
                </td>
              </tr>
            <% } } %>
          </tbody>
        </table>
      </div>
    </div>
  </div>

  <!-- Bootstrap Bundle JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
