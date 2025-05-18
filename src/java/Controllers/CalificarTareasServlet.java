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
            
            logger.info("CalificarTareasServlet inicializado correctamente: DAOs cargados");
            
            // Verificar que se pueden establecer conexiones con la base de datos
            if (!Models.Conexion.testConnection()) {
                logger.severe("No se puede establecer conexión con la base de datos durante la inicialización");
                throw new ServletException("Error de conexión con la base de datos");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al inicializar DAOs: " + e.getMessage(), e);
            throw new ServletException("Error al inicializar DAOs: " + e.getMessage(), e);
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
            // Verificar parámetros requeridos
            String idTareaParam = request.getParameter("idTarea");
            if (idTareaParam == null || idTareaParam.trim().isEmpty()) {
                throw new ServletException("El parámetro idTarea es requerido");
            }
            
            int idTarea = Integer.parseInt(idTareaParam);
            logger.info("Procesando calificaciones para tarea ID: " + idTarea);
            
            // Obtener tarea para validar
            Tarea tarea = tareaDAO.read(idTarea);
            if (tarea == null) {
                logger.warning("No se encontró la tarea con ID: " + idTarea);
                throw new ServletException("No se encontró la tarea con ID: " + idTarea);
            }
            
            // Obtener estudiantes del curso para procesar sus notas
            List<Estudiante> estudiantes = cursoDAO.getEstudiantesPorCurso(tarea.getIdCurso());
            logger.info("Número de estudiantes en el curso: " + estudiantes.size());
            
            // Contador de cuántas notas se procesaron correctamente
            int notasProcesadas = 0;
            
            // Procesar cada estudiante
            for (Estudiante estudiante : estudiantes) {
                String notaParam = request.getParameter("nota_" + estudiante.getId());
                String comentarioParam = request.getParameter("comentario_" + estudiante.getId());
                
                logger.fine("Procesando estudiante ID: " + estudiante.getId() + 
                          ", Nota: " + notaParam + ", Comentario: " + 
                          (comentarioParam != null ? comentarioParam.substring(0, Math.min(20, comentarioParam.length())) + "..." : "null"));
                
                // Solo procesar si se proporcionó una nota
                if (notaParam != null && !notaParam.trim().isEmpty()) {
                    try {
                        double valorNota = Double.parseDouble(notaParam);
                        
                        // Validar el rango de la nota
                        if (valorNota < 0 || valorNota > 100) {
                            logger.warning("Valor de nota fuera de rango: " + valorNota + " para estudiante ID: " + estudiante.getId());
                            // Continuar con el siguiente estudiante en lugar de fallar
                            continue;
                        }
                        
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
                            notasProcesadas++;
                            // Actualizar el promedio del estudiante
                            try {
                                double promedio = estudianteDAO.calcularPromedio(estudiante.getId());
                                estudianteDAO.updatePromedioAcademico(estudiante.getId(), promedio);
                                logger.info("Actualizado promedio de estudiante ID: " + estudiante.getId() + " a " + promedio);
                            } catch (Exception ex) {
                                logger.log(Level.WARNING, "Error al actualizar promedio del estudiante ID: {0} - {1}", 
                                        new Object[]{estudiante.getId(), ex.getMessage()});
                                // Continuamos a pesar del error en el cálculo del promedio
                            }
                        } else {
                            logger.warning("No se pudo guardar la nota para el estudiante ID: " + estudiante.getId());
                        }
                    } catch (NumberFormatException ex) {
                        logger.warning("Formato de nota inválido para estudiante ID: " + estudiante.getId() + " - " + notaParam);
                        // Continuar con el siguiente estudiante en lugar de fallar
                    }
                }
            }
            
            logger.info("Calificación completada. Notas procesadas: " + notasProcesadas);
            
            // Añadir mensaje de éxito a la sesión
            request.getSession().setAttribute("mensaje", "Calificaciones guardadas correctamente (" + notasProcesadas + " estudiantes).");
            
            // Redirigir de vuelta a la lista de tareas
            response.sendRedirect(request.getContextPath() + "/profesor/tareas");
            
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Error en parámetros numéricos: {0}", e.getMessage());
            // En lugar de lanzar ServletException, redirigir a la página de error
            request.setAttribute("error", "Error en los datos del formulario: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al calificar la tarea: {0}", e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
            request.setAttribute("errorDetails", e.getClass().getName());
            if (e.getStackTrace().length > 0) {
                request.setAttribute("errorStackTrace", e.getStackTrace()[0].toString());
            }
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}
