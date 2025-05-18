package Controllers;

import DAOs.CursoDAO;
import DAOs.TareaDAO;
import Models.Conexion;
import Models.Curso;
import Models.Tarea;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet para realizar diagnósticos del sistema
 */
@WebServlet("/diagnostico")
public class DiagnosticoServlet extends HttpServlet {    private static final Logger logger = Logger.getLogger(DiagnosticoServlet.class.getName());
    private CursoDAO cursoDAO;
    private TareaDAO tareaDAO;
      @Override
    public void init() throws ServletException {
        try {
            this.cursoDAO = new CursoDAO();
            this.tareaDAO = new TareaDAO();
            logger.info("DAOs inicializados correctamente en DiagnosticoServlet");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al inicializar DAOs: {0}", e.getMessage());
            throw new ServletException("Error al inicializar DAOs", e);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        
        // Si la acción es "rutas", muestra el diagnóstico de rutas en HTML
        if (accion != null && accion.equals("rutas")) {
            try {
                mostrarDiagnosticoRutas(request, response);
                return;
            } catch (ServletException | IOException e) {
                logger.severe("Error en mostrarDiagnosticoRutas: " + e.getMessage());
                throw e;
            }
        }
        
        // Por defecto, muestra el diagnóstico JSON        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            try {
                JSONObject diagnostico = new JSONObject();
                
                // Verificar cursos
                List<Curso> cursos = cursoDAO.readAll();
                diagnostico.put("totalCursos", cursos.size());
                
                JSONArray cursosArray = new JSONArray();
                for (Curso c : cursos) {
                    JSONObject curso = new JSONObject();
                    curso.put("id", c.getId());
                    curso.put("nombre", c.getNombre());
                    curso.put("codigo", c.getCodigo());
                    curso.put("idProfesor", c.getIdProfesor());
                    
                    // Verificar tareas de este curso
                    try (Connection conn = new Conexion().crearConexion();
                         Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM tarea WHERE id_curso = " + c.getId())) {
                        if (rs.next()) {
                            curso.put("numTareas", rs.getInt("total"));
                        }
                    } catch (SQLException e) {
                        curso.put("error", "Error al contar tareas: " + e.getMessage());
                    }
                    
                    cursosArray.put(curso);
                }
                diagnostico.put("cursos", cursosArray);
                
                // Verificar profesores con cursos y tareas
                int profesorId = request.getParameter("profesorId") != null ? 
                               Integer.parseInt(request.getParameter("profesorId")) : 0;
                
                if (profesorId > 0) {
                    JSONObject profesorInfo = new JSONObject();
                    profesorInfo.put("id", profesorId);
                    
                    // Obtener cursos del profesor
                    List<Curso> cursosProfessor = cursoDAO.getCursosPorProfesor(profesorId);
                    profesorInfo.put("numCursos", cursosProfessor.size());
                    
                    JSONArray cursosProfArray = new JSONArray();
                    for (Curso c : cursosProfessor) {
                        JSONObject curso = new JSONObject();
                        curso.put("id", c.getId());
                        curso.put("nombre", c.getNombre());
                        cursosProfArray.put(curso);
                    }
                    profesorInfo.put("cursos", cursosProfArray);
                    
                    // Obtener tareas del profesor
                    List<Tarea> tareasProfesor = tareaDAO.getTareasPorProfesor(profesorId);
                    profesorInfo.put("numTareas", tareasProfesor.size());
                    
                    JSONArray tareasArray = new JSONArray();
                    for (Tarea t : tareasProfesor) {
                        JSONObject tarea = new JSONObject();
                        tarea.put("id", t.getId());
                        tarea.put("titulo", t.getTitulo());
                        tarea.put("curso_id", t.getIdCurso());                        tarea.put("fecha_asignacion", t.getFechaAsignacion() != null ? 
                                 t.getFechaAsignacion().toString() : "No asignada");
                        tarea.put("fecha_entrega", t.getFechaEntrega() != null ? 
                                 t.getFechaEntrega().toString() : "Sin fecha límite");
                        tareasArray.put(tarea);
                    }
                    profesorInfo.put("tareas", tareasArray);
                    
                    diagnostico.put("profesor", profesorInfo);
                }
                
                // Verificar conexión directa a la base de datos
                try (Connection conn = new Conexion().crearConexion()) {
                    diagnostico.put("conexion", conn != null ? "OK" : "ERROR");
                    
                    if (conn != null) {
                        // Consulta directa para verificar tareas
                        try (Statement stmt = conn.createStatement();
                             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM tarea")) {
                            if (rs.next()) {
                                diagnostico.put("totalTareas", rs.getInt("total"));
                            }
                        }
                        
                        // Consulta directa para verificar la consulta de tareas por profesor
                        if (profesorId > 0) {
                            try (Statement stmt = conn.createStatement();
                                 ResultSet rs = stmt.executeQuery(
                                     "SELECT t.*, c.nombre as curso_nombre " +
                                     "FROM tarea t " +
                                     "JOIN curso c ON t.id_curso = c.id_curso " +
                                     "WHERE c.idProfesor = " + profesorId)) {
                                
                                int count = 0;
                                JSONArray directTareasArray = new JSONArray();
                                while (rs.next()) {
                                    count++;
                                    JSONObject tarea = new JSONObject();
                                    tarea.put("id", rs.getInt("id_tarea"));
                                    tarea.put("titulo", rs.getString("titulo"));
                                    tarea.put("curso_nombre", rs.getString("curso_nombre"));
                                    directTareasArray.put(tarea);
                                }
                                
                                diagnostico.put("directQueryTareas", directTareasArray);
                                diagnostico.put("directQueryCount", count);
                            }
                        }
                    }
                } catch (SQLException e) {
                    diagnostico.put("conexionError", e.getMessage());
                }
                out.print(diagnostico.toString(2));
                
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error en DiagnosticoServlet: {0}", e.getMessage());
                try {
                    out.print(new JSONObject().put("error", e.getMessage()).toString());
                } catch (Exception ex) {
                    logger.severe("Error al generar respuesta de error: " + ex.getMessage());
                }
            }
        }
    }
    
    /**
     * Muestra un diagnóstico de rutas y configuración del sistema en formato HTML
     */    private void mostrarDiagnosticoRutas(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        // Verificar si es una acción especial
        String accion = request.getParameter("specialAction");
        if (accion != null) {
            if ("fixContextDuplicate".equals(accion)) {
                // Esta redirección está diseñada para corregir el problema de contexto duplicado
                String path = request.getContextPath();
                if (path.contains("/Ges_de_Notas/Ges_de_Notas")) {
                    // Corregir el contexto duplicado
                    path = path.replace("/Ges_de_Notas/Ges_de_Notas", "/Ges_de_Notas");
                }
                response.sendRedirect(path + "/profesor/tareas");
                return;
            }
        }
        
        // Registrar información en el log
        logger.info("********* DIAGNÓSTICO DE RUTAS *********");
        logger.info("ContextPath: " + request.getContextPath());
        logger.info("ServletPath: " + request.getServletPath());
        logger.info("PathInfo: " + request.getPathInfo());
        logger.info("RequestURI: " + request.getRequestURI());
        logger.info("RequestURL: " + request.getRequestURL());
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Diagnóstico Servlet</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; padding: 20px; line-height: 1.6; }");
            out.println(".container { max-width: 800px; margin: 0 auto; }");
            out.println(".link-box { background-color: #f5f5f5; border: 1px solid #ddd; padding: 15px; margin: 10px 0; border-radius: 5px; }");
            out.println(".link-box a { display: block; margin: 5px 0; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");
            out.println("<h1>Diagnóstico de Rutas</h1>");
            out.println("<p>Esta página ayuda a diagnosticar problemas con las rutas y URLs del sistema.</p>");
            
            out.println("<h2>Información de la solicitud actual</h2>");
            out.println("<ul>");
            out.println("<li><strong>ContextPath:</strong> " + request.getContextPath() + "</li>");
            out.println("<li><strong>ServletPath:</strong> " + request.getServletPath() + "</li>");
            out.println("<li><strong>PathInfo:</strong> " + request.getPathInfo() + "</li>");
            out.println("<li><strong>RequestURI:</strong> " + request.getRequestURI() + "</li>");
            out.println("<li><strong>RequestURL:</strong> " + request.getRequestURL() + "</li>");
            out.println("</ul>");
            
            out.println("<h2>Enlaces directos a tareas (probar para diagnóstico)</h2>");            out.println("<div class='link-box'>");
            // Primera opción: enlace con context path
            out.println("<a href='" + request.getContextPath() + "/profesor/tareas'>1. /profesor/tareas (con contexto)</a>");
            
            // Segunda opción: enlace absoluto desde la raíz
            out.println("<a href='/profesor/tareas'>2. /profesor/tareas (absoluto)</a>");
            
            // Tercera opción: enlace relativo
            out.println("<a href='profesor/tareas'>3. profesor/tareas (relativo)</a>");
            
            // Cuarta opción: directamente al JSP
            out.println("<a href='" + request.getContextPath() + "/profesortareas.jsp'>4. /profesortareas.jsp (directo al JSP)</a>");
            
            // Opción para corregir el problema de contexto duplicado
            out.println("<a href='" + request.getContextPath() + "/diagnostico?accion=rutas&specialAction=fixContextDuplicate' style='color: green; font-weight: bold;'>5. CORREGIR PROBLEMA DE CONTEXTO DUPLICADO</a>");
            
            out.println("</div>");
            
            // Prueba de enlaces desde index
            out.println("<h2>Volver a páginas principales</h2>");
            out.println("<div class='link-box'>");
            out.println("<a href='" + request.getContextPath() + "/index.jsp'>Página de inicio</a>");
            out.println("<a href='" + request.getContextPath() + "/profesorindex.jsp'>Panel del profesor</a>");
            out.println("</div>");
            
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}
