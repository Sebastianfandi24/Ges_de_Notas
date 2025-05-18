package Controllers;

import DAOs.CursoDAO;
import DAOs.TareaDAO;
import Models.Tarea;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/**
 * Servlet para crear tareas de prueba
 */
@WebServlet("/test/crear-tarea")
public class CrearTareaPruebaServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(CrearTareaPruebaServlet.class.getName());
    private TareaDAO tareaDAO;
    private CursoDAO cursoDAO;
    
    @Override
    public void init() throws ServletException {
        try {
            this.tareaDAO = new TareaDAO();
            this.cursoDAO = new CursoDAO();
            logger.info("DAOs inicializados correctamente");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al inicializar DAOs: {0}", e.getMessage());
            throw new ServletException("Error al inicializar DAOs", e);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject result = new JSONObject();
        
        try {
            // Obtener ID del curso o usar un valor predeterminado
            int idCurso;
            try {
                idCurso = Integer.parseInt(request.getParameter("idCurso"));
            } catch (Exception e) {
                // Si no se proporciona un ID de curso válido, usamos el primer curso que encontremos
                List<Models.Curso> cursos = cursoDAO.readAll();
                if (cursos.isEmpty()) {
                    result.put("error", "No hay cursos disponibles para crear tareas");
                    out.print(result.toString());
                    return;
                }
                idCurso = cursos.get(0).getId();
            }
            
            // Crear una tarea de prueba con fechas actuales
            Tarea tarea = new Tarea();
            tarea.setTitulo("Tarea de Prueba - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            tarea.setDescripcion("Esta es una tarea de prueba creada automáticamente para verificar el sistema.");
            
            // Fecha de asignación: hoy
            Date fechaAsignacion = new Date();
            tarea.setFechaAsignacion(fechaAsignacion);
            
            // Fecha de entrega: 7 días después
            Date fechaEntrega = new Date(fechaAsignacion.getTime() + 7 * 24 * 60 * 60 * 1000);
            tarea.setFechaEntrega(fechaEntrega);
            
            tarea.setIdCurso(idCurso);
            
            // Guardar la tarea
            boolean creado = tareaDAO.create(tarea);
            
            if (creado) {
                result.put("success", true);
                result.put("message", "Tarea de prueba creada exitosamente");
                result.put("idCurso", idCurso);
                result.put("titulo", tarea.getTitulo());
                result.put("fechaAsignacion", new SimpleDateFormat("yyyy-MM-dd").format(fechaAsignacion));
                result.put("fechaEntrega", new SimpleDateFormat("yyyy-MM-dd").format(fechaEntrega));
            } else {
                result.put("success", false);
                result.put("message", "Error al crear la tarea de prueba");
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en CrearTareaPruebaServlet: {0}", e.getMessage());
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }
        
        out.print(result.toString());
    }
}
