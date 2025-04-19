<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.sql.*" %>
<%
    // Conexión a la base de datos
    String url = "jdbc:mysql://localhost:3306/sistema_academico";
    String user = "root";
    String password = ""; // Cambiar por la contraseña real
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    int totalCursos = 0;
    int totalUsuarios = 0;
    int totalProfesores = 0;
    int totalActividades = 0;

    try {
        conn = DriverManager.getConnection(url, user, password);

        // Obtener estadísticas
        stmt = conn.prepareStatement("SELECT COUNT(*) FROM curso");
        rs = stmt.executeQuery();
        if (rs.next()) totalCursos = rs.getInt(1);

        stmt = conn.prepareStatement("SELECT COUNT(*) FROM usuario");
        rs = stmt.executeQuery();
        if (rs.next()) totalUsuarios = rs.getInt(1);

        stmt = conn.prepareStatement("SELECT COUNT(*) FROM profesor WHERE estado = 'Activo'");
        rs = stmt.executeQuery();
        if (rs.next()) totalProfesores = rs.getInt(1);

        stmt = conn.prepareStatement("SELECT COUNT(*) FROM actividad");
        rs = stmt.executeQuery();
        if (rs.next()) totalActividades = rs.getInt(1);
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();
        if (conn != null) conn.close();
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Administrador | Sistema Académico</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        .dashboard-card {
            background: white;
            border-radius: 15px;
            padding: 1.5rem;
            box-shadow: 0 0 15px rgba(0,0,0,0.05);
            transition: transform 0.3s ease;
        }
        .dashboard-card:hover {
            transform: translateY(-5px);
        }
        .card-icon {
            font-size: 2rem;
            color: #0d6efd;
        }
        .card-title {
            color: #6c757d;
            font-size: 0.9rem;
            margin-bottom: 0.5rem;
        }
        .card-value {
            font-size: 2rem;
            font-weight: bold;
            color: #1b4f72;
        }
    </style>
</head>
<body class="bg-light">
    <div class="container-fluid py-4">
        <!-- Tarjetas de estadísticas -->
        <div class="row g-4 mb-4">
            <div class="col-xl-3 col-sm-6">
                <div class="dashboard-card">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <div class="card-title">Cursos Activos</div>
                            <div class="card-value"><%= totalCursos %></div>
                        </div>
                        <i class="bi bi-journal-text card-icon"></i>
                    </div>
                </div>
            </div>
            <div class="col-xl-3 col-sm-6">
                <div class="dashboard-card">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <div class="card-title">Usuarios Activos</div>
                            <div class="card-value"><%= totalUsuarios %></div>
                        </div>
                        <i class="bi bi-people card-icon"></i>
                    </div>
                </div>
            </div>
            <div class="col-xl-3 col-sm-6">
                <div class="dashboard-card">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <div class="card-title">Profesores Activos</div>
                            <div class="card-value"><%= totalProfesores %></div>
                        </div>
                        <i class="bi bi-person-badge card-icon"></i>
                    </div>
                </div>
            </div>
            <div class="col-xl-3 col-sm-6">
                <div class="dashboard-card">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <div class="card-title">Actividades Activas</div>
                            <div class="card-value"><%= totalActividades %></div>
                        </div>
                        <i class="bi bi-list-check card-icon"></i>
                    </div>
                </div>
            </div>
        </div>

        <!-- Navegación por pestañas -->
        <ul class="nav nav-tabs mb-4">
            <!-- ...existing code... -->
        </ul>
        <!-- Contenido principal -->
        <div class="row">
            <!-- Actividades Recientes -->
            <div class="col-lg-6">
                <div class="recent-activity">
                    <h5 class="mb-4">Actividades Recientes</h5>
                    <!-- Aquí puedes agregar lógica para mostrar actividades recientes -->
                </div>
            </div>

            <!-- Cursos Recientes -->
            <div class="col-lg-6">
                <div class="recent-activity">
                    <h5 class="mb-4">Cursos Recientes</h5>
                    <!-- Aquí puedes agregar lógica para mostrar cursos recientes -->
                </div>
            </div>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
