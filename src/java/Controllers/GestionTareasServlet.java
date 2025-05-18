package Controllers;

import DAOs.CursoDAO;
import DAOs.EstudianteDAO;
import DAOs.TareaDAO;
import Models.Curso;
import Models.Estudiante;
import Models.Tarea;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/profesor/tareas_api/*") // Cambiado para evitar conflicto
public class GestionTareasServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(GestionTareasServlet.class.getName());
    private TareaDAO tareaDAO;
    private CursoDAO cursoDAO;
    private EstudianteDAO estudianteDAO;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      @Override
    public void init() throws ServletException {
        try {
            this.tareaDAO = new TareaDAO();
            this.cursoDAO = new CursoDAO();
            this.estudianteDAO = new EstudianteDAO();
            
            logger.info("GestionTareasServlet: DAOs inicializados correctamente");
            
            // Verificar que se puede establecer conexión con la base de datos
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
        String pathInfo = request.getPathInfo();
        
        logger.info("GestionTareasServlet: Petición GET recibida, pathInfo: " + pathInfo);
        
        // Manejar la petición para obtener tareas por curso
        if (pathInfo != null && pathInfo.equals("/por-curso")) {
            try {
                // Obtener el parámetro id_curso
                String idCursoStr = request.getParameter("id_curso");
                logger.info("GestionTareasServlet: Petición de tareas por curso, id_curso=" + idCursoStr);
                
                if (idCursoStr == null || idCursoStr.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Se requiere el parámetro id_curso");
                    return;
                }
                
                int idCurso = Integer.parseInt(idCursoStr);
                List<Tarea> tareas = tareaDAO.getTareasPorCurso(idCurso);
                
                // Preparar respuesta JSON
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                
                List<Map<String, Object>> tareasJson = new ArrayList<>();
                for (Tarea tarea : tareas) {
                    Map<String, Object> tareaMap = new HashMap<>();
                    tareaMap.put("id", tarea.getId());
                    tareaMap.put("titulo", tarea.getTitulo());
                    tareaMap.put("descripcion", tarea.getDescripcion());
                    tareaMap.put("id_curso", tarea.getIdCurso());
                    tareaMap.put("fecha_asignacion", dateFormat.format(tarea.getFechaAsignacion()));
                    tareaMap.put("fecha_entrega", tarea.getFechaEntrega() != null ? 
                                 dateFormat.format(tarea.getFechaEntrega()) : null);
                    tareasJson.add(tareaMap);
                }
                
                Gson gson = new Gson();
                PrintWriter out = response.getWriter();
                out.print(gson.toJson(tareasJson));
                out.flush();
                logger.info("GestionTareasServlet: Enviadas " + tareas.size() + " tareas en formato JSON");
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Error de formato en id_curso: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("El formato del id_curso no es válido");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error al procesar tareas por curso: " + e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Error al procesar la solicitud: " + e.getMessage());
            }
        }
        // Si pathInfo no es nulo, estamos buscando una tarea específica
        else if (pathInfo != null && !pathInfo.equals("/")) {
            try {
                int tareaId = Integer.parseInt(pathInfo.substring(1));
                Tarea tarea = tareaDAO.read(tareaId);
                
                if (tarea != null) {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    
                    // Convertir a JSON
                    Map<String, Object> tareaMap = new HashMap<>();
                    tareaMap.put("id_tarea", tarea.getId());
                    tareaMap.put("titulo", tarea.getTitulo());
                    tareaMap.put("descripcion", tarea.getDescripcion());
                    tareaMap.put("id_curso", tarea.getIdCurso());
                    tareaMap.put("curso_nombre", tarea.getCursoNombre());
                    tareaMap.put("fecha_asignacion", tarea.getFechaAsignacion());
                    tareaMap.put("fecha_entrega", tarea.getFechaEntrega());
                    tareaMap.put("estado", tarea.getEstado());                    // Agregar estudiantes si se solicitan
                    if (request.getParameter("includeEstudiantes") != null) {
                        int idCurso = tarea.getIdCurso();
                        
                        // 1. Obtener la lista de estudiantes del curso
                        List<Estudiante> estudiantes = cursoDAO.getEstudiantesPorCurso(idCurso);
                        
                        // 2. Obtener las notas existentes para esta tarea
                        DAOs.NotaDAO notaDAO = new DAOs.NotaDAO();
                        List<Models.Nota> notas = notaDAO.getNotasPorTarea(tareaId);
                        
                        // 3. Crear una lista de mapas con información combinada
                        List<Map<String, Object>> estudiantesConNotas = new ArrayList<>();
                          for (Estudiante est : estudiantes) {
                            Map<String, Object> estudianteInfo = new HashMap<>();
                            estudianteInfo.put("id", est.getId());
                            estudianteInfo.put("idEstudiante", est.getId());  // Compatibilidad con diferentes nombres de campo
                            estudianteInfo.put("nombre", est.getNombre());
                            estudianteInfo.put("nota", null);  // Inicializado explícitamente a null
                            estudianteInfo.put("comentario", ""); // Inicializado a cadena vacía
                            
                            // Buscar la nota correspondiente a este estudiante
                            boolean notaEncontrada = false;
                            for (Models.Nota nota : notas) {
                                if (nota.getIdEstudiante() == est.getId()) {
                                    notaEncontrada = true;
                                    try {
                                        // Manejar explícitamente valores nulos
                                        Double valorNota = nota.getNota();
                                        estudianteInfo.put("nota", valorNota);
                                        estudianteInfo.put("comentario", nota.getComentario() != null ? nota.getComentario() : "");
                                    } catch (Exception e) {
                                        // En caso de error, mantener los valores predeterminados
                                        logger.log(Level.WARNING, 
                                                "Error procesando nota para estudiante ID: " + est.getId() + " - " + e.getMessage());
                                    }
                                    break;
                                }
                            }
                            
                            // Asegurar que los valores son correctos si no se encontró nota
                            if (!notaEncontrada) {
                                estudianteInfo.put("nota", null);
                                estudianteInfo.put("comentario", "");
                            }
                            
                            estudiantesConNotas.add(estudianteInfo);
                        }
                        
                        tareaMap.put("estudiantes", estudiantesConNotas);
                    }
                    
                    Gson gson = new Gson();
                    PrintWriter out = response.getWriter();
                    out.print(gson.toJson(tareaMap));
                    out.flush();
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("Tarea no encontrada");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("ID de tarea inválido");
            }
        } else {
            // Si no hay pathInfo, redirigir al servlet principal
            response.sendRedirect(request.getContextPath() + "/profesor/tareas");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Actualizar una tarea existente
            if (request.getParameter("idTarea") != null) {
                int idTarea = Integer.parseInt(request.getParameter("idTarea"));
                Tarea tarea = tareaDAO.read(idTarea);
                
                if (tarea == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("Tarea no encontrada");
                    return;
                }
                
                tarea.setTitulo(request.getParameter("titulo"));
                tarea.setDescripcion(request.getParameter("descripcion"));
                tarea.setIdCurso(Integer.parseInt(request.getParameter("idCurso")));
                
                if (request.getParameter("fechaEntrega") != null && !request.getParameter("fechaEntrega").isEmpty()) {
                    try {
                        tarea.setFechaEntrega(dateFormat.parse(request.getParameter("fechaEntrega")));
                    } catch (ParseException e) {
                        logger.log(Level.WARNING, "Formato de fecha inválido: " + e.getMessage());
                    }
                }
                
                boolean updated = tareaDAO.update(tarea);
                
                if (updated) {
                    response.sendRedirect(request.getContextPath() + "/profesor/tareas");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("Error al actualizar la tarea");
                }
            }
            // Crear una nueva tarea
            else {
                String titulo = request.getParameter("titulo");
                String descripcion = request.getParameter("descripcion");
                int idCurso = Integer.parseInt(request.getParameter("idCurso"));
                
                Tarea nuevaTarea = new Tarea();
                nuevaTarea.setTitulo(titulo);
                nuevaTarea.setDescripcion(descripcion);
                nuevaTarea.setIdCurso(idCurso);
                nuevaTarea.setFechaAsignacion(new java.util.Date());
                
                if (request.getParameter("fechaEntrega") != null && !request.getParameter("fechaEntrega").isEmpty()) {
                    try {
                        nuevaTarea.setFechaEntrega(dateFormat.parse(request.getParameter("fechaEntrega")));
                    } catch (ParseException e) {
                        logger.log(Level.WARNING, "Formato de fecha inválido: " + e.getMessage());
                    }
                }
                
                boolean created = tareaDAO.create(nuevaTarea);
                
                if (created) {
                    response.sendRedirect(request.getContextPath() + "/profesor/tareas");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("Error al crear la tarea");
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error: " + e.getMessage());
            logger.log(Level.SEVERE, "Error en doPost: " + e.getMessage(), e);
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Podemos implementar más adelante si es necesario
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null && !pathInfo.equals("/")) {
            try {
                int tareaId = Integer.parseInt(pathInfo.substring(1));
                boolean deleted = tareaDAO.delete(tareaId);
                
                if (deleted) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Tarea eliminada correctamente");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("No se pudo eliminar la tarea");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("ID de tarea inválido");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Se requiere un ID de tarea para eliminar");
        }
    }
}
