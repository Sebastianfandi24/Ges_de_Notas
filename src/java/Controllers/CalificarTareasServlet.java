package Controllers;

import DAOs.CursoDAO;
import DAOs.EstudianteDAO;
import DAOs.NotaDAO;
import DAOs.TareaDAO;
import Models.Estudiante;
import Models.Nota;
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
import java.util.Date;

@WebServlet("/profesor/tareas/calificar")
public class CalificarTareasServlet extends HttpServlet {    private static final Logger logger = Logger.getLogger(CalificarTareasServlet.class.getName());
    private TareaDAO tareaDAO;
    private NotaDAO notaDAO;
    private EstudianteDAO estudianteDAO;
    private CursoDAO cursoDAO;
    
    @Override
    public void init() throws ServletException {
        try {
            this.tareaDAO = new TareaDAO();
            this.notaDAO = new NotaDAO();
            this.estudianteDAO = new EstudianteDAO();
            this.cursoDAO = new CursoDAO();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al inicializar DAOs: " + e.getMessage(), e);
            throw new ServletException("Error al inicializar DAOs", e);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int idTarea = Integer.parseInt(request.getParameter("idTarea"));
            Tarea tarea = tareaDAO.read(idTarea);
            
            if (tarea == null) {
                throw new ServletException("No se encontró la tarea con ID: " + idTarea);
            }
            
            // Obtener estudiantes del curso
            List<Estudiante> estudiantes = cursoDAO.getEstudiantesPorCurso(tarea.getIdCurso());
            
            // Obtener notas existentes
            List<Nota> notas = notaDAO.getNotasPorTarea(idTarea);
            
            request.setAttribute("tarea", tarea);
            request.setAttribute("estudiantes", estudiantes);
            request.setAttribute("notas", notas);
            
            // Redirigir a la vista de calificación
            request.getRequestDispatcher("/calificarTarea.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Error en parámetro numérico: {0}", e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de tarea inválido");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al cargar formulario de calificación: {0}", e.getMessage());
            throw new ServletException("Error al procesar la solicitud", e);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int idTarea = Integer.parseInt(request.getParameter("idTarea"));
            
            // Obtener tarea para validar
            Tarea tarea = tareaDAO.read(idTarea);
            if (tarea == null) {
                throw new ServletException("No se encontró la tarea con ID: " + idTarea);
            }
            
            // Obtener estudiantes del curso para procesar sus notas
            List<Estudiante> estudiantes = cursoDAO.getEstudiantesPorCurso(tarea.getIdCurso());
            
            // Procesar cada estudiante
            for (Estudiante estudiante : estudiantes) {
                String notaParam = request.getParameter("nota_" + estudiante.getId());
                String comentarioParam = request.getParameter("comentario_" + estudiante.getId());
                
                // Solo procesar si se proporcionó una nota
                if (notaParam != null && !notaParam.trim().isEmpty()) {
                    double valorNota = Double.parseDouble(notaParam);
                    String comentario = (comentarioParam != null) ? comentarioParam : "";
                    
                    // Crear o actualizar la nota
                    Nota nota = new Nota();
                    nota.setIdTarea(idTarea);
                    nota.setIdEstudiante(estudiante.getId());
                    nota.setNota(valorNota);
                    nota.setComentario(comentario);
                    nota.setFechaEvaluacion(new Date());
                    
                    boolean resultado = notaDAO.createOrUpdate(nota);
                    
                    if (resultado) {
                        // Actualizar el promedio del estudiante
                        try {
                            double promedio = estudianteDAO.calcularPromedio(estudiante.getId());
                            estudianteDAO.updatePromedioAcademico(estudiante.getId(), promedio);
                            logger.info("Actualizado promedio de estudiante ID: " + estudiante.getId() + " a " + promedio);
                        } catch (Exception ex) {
                            logger.log(Level.WARNING, "Error al actualizar promedio del estudiante ID: {0} - {1}", 
                                    new Object[]{estudiante.getId(), ex.getMessage()});
                        }
                    }
                }
            }
            
            // Redirigir de vuelta a la lista de tareas
            response.sendRedirect(request.getContextPath() + "/profesor/tareas");
            
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Error en parámetros numéricos: {0}", e.getMessage());
            throw new ServletException("Error en los datos del formulario", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al calificar la tarea: {0}", e.getMessage());
            throw new ServletException("Error al procesar la solicitud", e);
        }
    }
}
