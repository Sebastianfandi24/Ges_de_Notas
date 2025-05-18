package Controllers;

import DAOs.ProfesorDAO;
import DAOs.CursoDAO;
import DAOs.TareaDAO;
import Models.Curso;
import Models.Tarea;
import Models.Conexion;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.List;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/diagnostico/tareas")
public class DiagnosticoTareasServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(DiagnosticoTareasServlet.class.getName());
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Diagnóstico de Tareas</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 20px; }");
            out.println("h1, h2 { color: #333; }");
            out.println("table { border-collapse: collapse; width: 100%; }");
            out.println("table, th, td { border: 1px solid #ddd; }");
            out.println("th, td { padding: 8px; text-align: left; }");
            out.println("tr:nth-child(even) { background-color: #f2f2f2; }");
            out.println("th { background-color: #4CAF50; color: white; }");
            out.println(".error { color: red; }");
            out.println(".success { color: green; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Diagnóstico de Tareas</h1>");
            
            // Verificar la sesión
            HttpSession session = request.getSession(false);
            
            out.println("<h2>Información de Sesión</h2>");
            if (session != null) {
                out.println("<p class='success'>Sesión activa</p>");
                out.println("<table>");
                out.println("<tr><th>Atributo</th><th>Valor</th></tr>");
                out.println("<tr><td>ID de Sesión</td><td>" + session.getId() + "</td></tr>");
                out.println("<tr><td>userId</td><td>" + session.getAttribute("userId") + "</td></tr>");
                out.println("<tr><td>userRol</td><td>" + session.getAttribute("userRol") + "</td></tr>");
                out.println("<tr><td>userNombre</td><td>" + session.getAttribute("userNombre") + "</td></tr>");
                out.println("</table>");
            } else {
                out.println("<p class='error'>No hay sesión activa</p>");
            }
            
            // ID del profesor (directamente de la base de datos)
            out.println("<h2>Información del Profesor</h2>");
            int userId = (session != null && session.getAttribute("userId") != null) ? 
                         (int) session.getAttribute("userId") : 0;
              if (userId > 0) {
                try {
                    ProfesorDAO profesorDAO = new ProfesorDAO();
                    Integer idProfesor = profesorDAO.getProfesorIdByUsuario(userId);
                    
                    if (idProfesor != null) {
                    out.println("<p class='success'>Profesor encontrado</p>");
                    out.println("<table>");
                    out.println("<tr><th>ID de Usuario</th><th>ID de Profesor</th></tr>");
                    out.println("<tr><td>" + userId + "</td><td>" + idProfesor + "</td></tr>");
                    out.println("</table>");
                    
                    // Consulta directa a la base de datos para verificar cursos
                    out.println("<h2>Cursos del Profesor (Consulta Directa)</h2>");
                    try (Connection conn = new Conexion().crearConexion()) {
                        String cursosSql = "SELECT c.id_curso, c.nombre, c.codigo " +
                                          "FROM curso c " +
                                          "WHERE c.idProfesor = " + idProfesor;
                        
                        try (Statement st = conn.createStatement();
                             ResultSet rs = st.executeQuery(cursosSql)) {
                            
                            out.println("<table>");
                            out.println("<tr><th>ID de Curso</th><th>Nombre</th><th>Código</th></tr>");
                            
                            boolean hayCursos = false;
                            while (rs.next()) {
                                hayCursos = true;
                                out.println("<tr>");
                                out.println("<td>" + rs.getInt("id_curso") + "</td>");
                                out.println("<td>" + rs.getString("nombre") + "</td>");
                                out.println("<td>" + rs.getString("codigo") + "</td>");
                                out.println("</tr>");
                            }
                            
                            out.println("</table>");
                            
                            if (!hayCursos) {
                                out.println("<p class='error'>No se encontraron cursos para este profesor</p>");
                            }
                        }
                        
                        // Consulta directa a la base de datos para verificar tareas
                        out.println("<h2>Tareas del Profesor (Consulta Directa)</h2>");
                        String tareasSql = "SELECT t.id_tarea, t.titulo, t.fecha_asignacion, t.fecha_entrega, " +
                                          "c.id_curso, c.nombre as curso_nombre " +
                                          "FROM tarea t " +
                                          "JOIN curso c ON t.id_curso = c.id_curso " +
                                          "WHERE c.idProfesor = " + idProfesor;
                        
                        try (Statement st = conn.createStatement();
                             ResultSet rs = st.executeQuery(tareasSql)) {
                            
                            out.println("<table>");
                            out.println("<tr><th>ID de Tarea</th><th>Título</th><th>Fecha Asignación</th><th>Fecha Entrega</th><th>ID Curso</th><th>Curso</th></tr>");
                            
                            boolean hayTareas = false;
                            while (rs.next()) {
                                hayTareas = true;
                                out.println("<tr>");
                                out.println("<td>" + rs.getInt("id_tarea") + "</td>");
                                out.println("<td>" + rs.getString("titulo") + "</td>");
                                out.println("<td>" + rs.getTimestamp("fecha_asignacion") + "</td>");
                                out.println("<td>" + rs.getTimestamp("fecha_entrega") + "</td>");
                                out.println("<td>" + rs.getInt("id_curso") + "</td>");
                                out.println("<td>" + rs.getString("curso_nombre") + "</td>");
                                out.println("</tr>");
                            }
                            
                            out.println("</table>");
                            
                            if (!hayTareas) {
                                out.println("<p class='error'>No se encontraron tareas para este profesor</p>");
                            } else {
                                out.println("<p class='success'>Se encontraron tareas para este profesor</p>");
                            }
                        }
                    } catch (SQLException e) {
                        out.println("<p class='error'>Error de base de datos: " + e.getMessage() + "</p>");
                    }                    // Usar DAO para obtener cursos
                    out.println("<h2>Cursos del Profesor (Usando DAO)</h2>");
                    try {
                        CursoDAO cursoDAO = new CursoDAO();
                        List<Curso> cursos = cursoDAO.getCursosPorProfesor(idProfesor);
                        
                        if (cursos != null && !cursos.isEmpty()) {
                            out.println("<table>");
                            out.println("<tr><th>ID</th><th>Nombre</th><th>Código</th></tr>");
                            
                            for (Curso curso : cursos) {
                                out.println("<tr>");
                                out.println("<td>" + curso.getId() + "</td>");
                                out.println("<td>" + curso.getNombre() + "</td>");
                                out.println("<td>" + curso.getCodigo() + "</td>");
                                out.println("</tr>");
                            }
                            
                            out.println("</table>");
                        } else {
                            out.println("<p class='error'>No se encontraron cursos para este profesor usando DAO</p>");
                        }
                    } catch (Exception e) {
                        out.println("<p class='error'>Error al instanciar CursoDAO: " + e.getMessage() + "</p>");
                        e.printStackTrace();
                    }
                      // Usar DAO para obtener tareas
                    out.println("<h2>Tareas del Profesor (Usando DAO)</h2>");
                    try {
                        TareaDAO tareaDAO = new TareaDAO();
                        List<Tarea> tareas = tareaDAO.getTareasPorProfesor(idProfesor);
                        
                        if (tareas != null && !tareas.isEmpty()) {
                            out.println("<table>");
                            out.println("<tr><th>ID</th><th>Título</th><th>Fecha Asignación</th><th>Fecha Entrega</th><th>Curso</th><th>Estado</th></tr>");
                            
                            for (Tarea tarea : tareas) {
                                out.println("<tr>");
                                out.println("<td>" + tarea.getId() + "</td>");
                                out.println("<td>" + tarea.getTitulo() + "</td>");
                                out.println("<td>" + tarea.getFechaAsignacion() + "</td>");
                                out.println("<td>" + tarea.getFechaEntrega() + "</td>");
                                out.println("<td>" + tarea.getCursoNombre() + "</td>");
                                out.println("<td>" + tarea.getEstado() + "</td>");
                                out.println("</tr>");
                            }
                            
                            out.println("</table>");
                        } else {
                            out.println("<p class='error'>No se encontraron tareas para este profesor usando DAO</p>");
                        }
                    } catch (Exception e) {
                        out.println("<p class='error'>Error al instanciar TareaDAO: " + e.getMessage() + "</p>");
                        e.printStackTrace();
                    }                } else {
                    out.println("<p class='error'>No se encontró el profesor para el usuario ID: " + userId + "</p>");
                }
                } catch (Exception e) {
                    out.println("<p class='error'>Error al instanciar ProfesorDAO: " + e.getMessage() + "</p>");
                    e.printStackTrace();
                }
            } else {
                out.println("<p class='error'>No hay ID de usuario disponible en la sesión</p>");
            }
            
            out.println("<h2>Verificar RequestDispatcher</h2>");
            out.println("<p>Context Path: " + request.getContextPath() + "</p>");
            out.println("<p>Servlet Path: " + request.getServletPath() + "</p>");
            out.println("<p>Request URI: " + request.getRequestURI() + "</p>");
            out.println("<p>Request URL: " + request.getRequestURL() + "</p>");
            
            out.println("<h2>Solución propuesta</h2>");
            out.println("<p>Prueba a hacer clic en este enlace para acceder directamente a la página de tareas:</p>");
            out.println("<a href='" + request.getContextPath() + "/profesor/tareas' target='_blank'>Ir a Gestión de Tareas</a>");
            
            out.println("</body>");
            out.println("</html>");
        }
    }
}
