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

    // Obtener tareas asignadas al estudiante
    java.util.List<java.util.Map<String, Object>> tareas = new java.util.ArrayList<>();
    if (idEstudiante != null) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sistema_academico", "root", "");
            String sql = "SELECT t.titulo, t.descripcion, t.fecha_entrega, c.nombre as curso_nombre, " +
                         "nt.nota, nt.comentario, nt.fecha_evaluacion " +
                         "FROM tarea t " +
                         "INNER JOIN curso c ON t.id_curso = c.id_curso " +
                         "INNER JOIN curso_estudiante ce ON ce.id_curso = c.id_curso " +
                         "LEFT JOIN nota_tarea nt ON nt.id_tarea = t.id_tarea AND nt.id_estudiante = ? " +
                         "WHERE ce.id_estudiante = ? " +
                         "ORDER BY t.fecha_entrega DESC";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idEstudiante);
            ps.setInt(2, idEstudiante);
            rs = ps.executeQuery();
            while (rs.next()) {
                java.util.Map<String, Object> tarea = new java.util.HashMap<>();
                tarea.put("titulo", rs.getString("titulo"));
                tarea.put("curso", rs.getString("curso_nombre"));
                tarea.put("fecha_entrega", rs.getDate("fecha_entrega"));
                tarea.put("nota", rs.getObject("nota"));
                tarea.put("comentario", rs.getString("comentario"));
                tarea.put("fecha_evaluacion", rs.getDate("fecha_evaluacion"));
                // Estado: entregada si tiene nota, pendiente si no
                tarea.put("estado", rs.getObject("nota") != null ? "Entregado" : "Pendiente");
                tareas.add(tarea);
            }
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

    <!-- Tabla dinámica -->
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
          <% if (tareas.isEmpty()) { %>
            <tr>
              <td colspan="6" class="text-center">No tienes tareas asignadas.</td>
            </tr>
          <% } else {
            for (java.util.Map<String, Object> tarea : tareas) { %>
              <tr>
                <td><%= tarea.get("titulo") %></td>
                <td><%= tarea.get("curso") %></td>
                <td><%= tarea.get("fecha_entrega") %></td>
                <td>
                  <% if ("Pendiente".equals(tarea.get("estado"))) { %>
                    <span class="badge bg-warning text-white badge-status">Pendiente</span>
                  <% } else { %>
                    <span class="badge bg-success text-white badge-status">Entregado</span>
                  <% } %>
                </td>
                <td>
                  <%= tarea.get("nota") != null ? tarea.get("nota") : "-" %>
                </td>
                <td>
                  <% if ("Pendiente".equals(tarea.get("estado"))) { %>
                    <button class="btn btn-sm btn-primary">Entregar</button>
                  <% } else { %>
                    <button class="btn btn-sm btn-secondary">Ver detalles</button>
                  <% } %>
                </td>
              </tr>
          <% } } %>
        </tbody>
      </table>
    </div>
  </div>

  <!-- Bootstrap Bundle JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
