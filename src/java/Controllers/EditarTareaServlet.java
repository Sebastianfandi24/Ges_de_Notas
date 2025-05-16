package Controllers;

import DAOs.CursoDAO;
import DAOs.TareaDAO;
import Models.Curso;
import Models.Tarea;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/profesor/tareas/editar")
public class EditarTareaServlet extends HttpServlet {    private static final Logger logger = Logger.getLogger(EditarTareaServlet.class.getName());
    private TareaDAO tareaDAO;
    
    @Override
    public void init() throws ServletException {
        try {
            this.tareaDAO = new TareaDAO();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al inicializar TareaDAO: " + e.getMessage(), e);
            throw new ServletException("Error al inicializar TareaDAO", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Obtener parámetros de la solicitud
            int idTarea = Integer.parseInt(request.getParameter("idTarea"));
            String titulo = request.getParameter("titulo");
            String descripcion = request.getParameter("descripcion");
            int idCurso = Integer.parseInt(request.getParameter("idCurso"));
            
            // Obtener la tarea existente
            Tarea tarea = tareaDAO.read(idTarea);
            if (tarea == null) {
                throw new ServletException("No se encontró la tarea con ID: " + idTarea);
            }
            
            // Actualizar los datos de la tarea
            tarea.setTitulo(titulo);
            tarea.setDescripcion(descripcion);
            tarea.setId_curso(idCurso);
            
            // Procesar la fecha si fue proporcionada
            String fechaEntregaStr = request.getParameter("fechaEntrega");
            if (fechaEntregaStr != null && !fechaEntregaStr.isEmpty()) {
                try {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date fechaEntrega = sdf.parse(fechaEntregaStr);
                    tarea.setFecha_entrega(fechaEntrega);
                } catch (java.text.ParseException e) {
                    logger.log(Level.WARNING, "Formato de fecha inválido: {0}", fechaEntregaStr);
                }
            }
            
            // Actualizar la tarea en la base de datos
            boolean actualizado = tareaDAO.update(tarea);
            if (!actualizado) {
                throw new ServletException("Error al actualizar la tarea");
            }
            
            // Redirigir de vuelta a la lista de tareas
            response.sendRedirect(request.getContextPath() + "/profesor/tareas");
            
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Error en parámetros numéricos: {0}", e.getMessage());
            throw new ServletException("Error en los datos del formulario", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al editar la tarea: {0}", e.getMessage());
            throw new ServletException("Error al procesar la solicitud", e);
        }
    }
}
