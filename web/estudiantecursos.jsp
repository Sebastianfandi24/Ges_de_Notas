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
        // Buscar id_estudiante por userId en la base de datos
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
                session.setAttribute("id_estudiante", idEstudiante); // Guardar en sesión para siguientes usos
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception e) {}
            if (ps != null) try { ps.close(); } catch (Exception e) {}
            if (conn != null) try { conn.close(); } catch (Exception e) {}
        }
    }

    // Obtener cursos del estudiante
    java.util.List<java.util.Map<String, Object>> cursos = new java.util.ArrayList<>();
    if (idEstudiante != null) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sistema_academico", "root", "");
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
      <% if (cursos.isEmpty()) { %>
        <div class="col-12">
          <div class="alert alert-info">No tienes cursos asignados actualmente.</div>
        </div>
      <% } else {
        for (java.util.Map<String, Object> curso : cursos) { %>
        <div class="col-md-6">
          <div class="course-card">
            <div class="course-card-header">
              <%= curso.get("nombre") %>
            </div>
            <div class="course-card-body">
              <p><strong>Profesor:</strong> <%= curso.get("profesor") != null ? curso.get("profesor") : "Sin asignar" %></p>
              <p><strong>Código:</strong> <%= curso.get("codigo") %></p>
              <p><strong>Descripción:</strong> <%= curso.get("descripcion") %></p>
            </div>
            <div class="course-card-footer">
              <a href="estudiantecursos.jsp?curso=<%= curso.get("codigo") %>" class="btn btn-primary">Ver detalles</a>
              <a href="estudiantetareas.jsp?curso=<%= curso.get("codigo") %>" class="btn btn-outline-primary">Ver tareas</a>
            </div>
          </div>
        </div>
      <% } } %>
    </div>
  </div>
  <!-- Bootstrap Bundle JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
