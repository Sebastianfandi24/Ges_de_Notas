package Controllers;

import DAOs.CursoDAO;
import DAOs.EstudianteDAO;
import Models.Estudiante;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/profesor/cursos/asignar")
public class AsignarEstudiantesServlet extends HttpServlet {
    private CursoDAO cursoDAO;
    private EstudianteDAO estudianteDAO;
    private static final Logger logger = Logger.getLogger(AsignarEstudiantesServlet.class.getName());

    @Override
    public void init() throws ServletException {
        try {
            cursoDAO = new CursoDAO();
            estudianteDAO = new EstudianteDAO();
            logger.info("AsignarEstudiantesServlet: DAOs inicializados correctamente");
        } catch (Exception e) {
            logger.severe("AsignarEstudiantesServlet: Error al inicializar DAOs: " + e.getMessage());
            throw new ServletException("Error al inicializar DAOs", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int cursoId = Integer.parseInt(request.getParameter("cursoId"));
            List<Estudiante> disponibles = estudianteDAO.getNoAsignados(cursoId);
            // Obtener también estudiantes ya asignados
            List<Estudiante> asignados = estudianteDAO.getAsignados(cursoId);
            
            request.setAttribute("cursoId", cursoId);
            request.setAttribute("disponibles", disponibles);
            request.setAttribute("asignados", asignados);
            request.getRequestDispatcher("/asignarEstudiantes.jsp")
                  .forward(request, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                             "Error al cargar estudiantes disponibles: " + e.getMessage());
        }
    }    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int cursoId = Integer.parseInt(request.getParameter("cursoId"));
            logger.info("Procesando asignación de estudiantes para curso ID: " + cursoId);
            
            // Limpiar asignaciones previas para poder desmarcar
            if (cursoDAO.clearAssignments(cursoId)) {
                logger.info("Asignaciones previas eliminadas correctamente");
            } else {
                logger.warning("Posible problema al eliminar asignaciones previas");
            }
            
            String[] ids = request.getParameterValues("estudiantes");
            
            // Si hay estudiantes seleccionados, asignarlos
            if (ids != null && ids.length > 0) {
                List<Integer> estudiantes = new ArrayList<>();
                for (String id : ids) {
                    estudiantes.add(Integer.parseInt(id));
                }
                logger.info("Asignando " + estudiantes.size() + " estudiantes al curso");
                
                if (cursoDAO.asignarEstudiantes(cursoId, estudiantes)) {
                    logger.info("Estudiantes asignados correctamente");
                    response.sendRedirect(request.getContextPath() + "/profesor/cursos");
                    return;
                } else {
                    logger.severe("Error al asignar estudiantes al curso");
                    request.setAttribute("error", "No se pudieron asignar los estudiantes seleccionados");
                    doGet(request, response);  // Volver a mostrar el formulario con un mensaje de error
                    return;
                }
            } else {
                // No hay estudiantes seleccionados, es válido (significa desasignar a todos)
                logger.info("No hay estudiantes seleccionados, se han eliminado todas las asignaciones");
                response.sendRedirect(request.getContextPath() + "/profesor/cursos");
                return;
            }
        } catch (Exception e) {
            logger.severe("Error procesando la solicitud: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error procesando la solicitud: " + e.getMessage());
            doGet(request, response);  // Volver a mostrar el formulario con un mensaje de error
        }
    }
}
