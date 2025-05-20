package Controllers;

import DAOs.EstudianteDAO;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import Models.Conexion;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpSession;
import java.util.logging.Logger;

@WebServlet(name = "EstudianteControllerServlet", urlPatterns = {"/estudiante/*"})
public class EstudianteControllerServlet extends HttpServlet {
    
    private EstudianteDAO estudianteDAO;
    private final Gson gson;
    private final Conexion conexion;
    private static final Logger logger = Logger.getLogger(EstudianteControllerServlet.class.getName());
    
    private static final String USER_ID_ATTR = "userId";
    private static final String ID_ESTUDIANTE_ATTR = "id_estudiante";
    private static final String CURSOS_ATTR = "cursos";
    
    public EstudianteControllerServlet() {
        this.gson = new Gson();
        this.conexion = new Conexion();
    }
    
    @Override
    public void init() throws ServletException {
        try {
            this.estudianteDAO = new EstudianteDAO();
            logger.info("EstudianteControllerServlet: DAOs inicializados correctamente");
        } catch (Exception e) {
            logger.severe("EstudianteControllerServlet: Error al inicializar DAOs: " + e.getMessage());
            throw new ServletException("Error al inicializar DAOs", e);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        logger.info(() -> "EstudianteControllerServlet: Recibida petición GET - Path: " + pathInfo);
        
        // Verificar sesión
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(USER_ID_ATTR) == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Obtener id del estudiante
        Integer idEstudiante = (Integer) session.getAttribute(ID_ESTUDIANTE_ATTR);
        if (idEstudiante == null) {
            int userId = (Integer) session.getAttribute(USER_ID_ATTR);
            idEstudiante = estudianteDAO.getEstudianteIdByUserId(userId);
            if (idEstudiante != null) {
                session.setAttribute(ID_ESTUDIANTE_ATTR, idEstudiante);
            } else {
                response.sendRedirect(request.getContextPath() + "/error.jsp?msg=Usuario+no+es+estudiante");
                return;
            }
        }
        
        // Establecer atributo común para todas las vistas
        request.setAttribute(ID_ESTUDIANTE_ATTR, idEstudiante);
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Redirigir al dashboard por defecto
            response.sendRedirect(request.getContextPath() + "/estudiante/dashboard");
            return;
        }
        
        String[] splits = pathInfo.split("/");
        String resource = splits.length > 1 ? splits[1] : "";
        
        // Determinar qué vista mostrar
        if ("dashboard".equals(resource)) {
            mostrarDashboard(request, response, idEstudiante);
        } else if ("cursos".equals(resource)) {
            mostrarCursos(request, response, idEstudiante);
        } else if ("tareas".equals(resource)) {
            mostrarTareas(request, response, idEstudiante);
        } else if ("api".equals(resource)) {
            // Manejar llamadas API
            handleApiRequest(request, response, idEstudiante, splits);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        logger.info(() -> "EstudianteControllerServlet: Recibida petición POST - Path: " + pathInfo);
        
        // Verificar sesión
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(USER_ID_ATTR) == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Obtener id del estudiante
        Integer idEstudiante = (Integer) session.getAttribute(ID_ESTUDIANTE_ATTR);
        if (idEstudiante == null) {
            int userId = (Integer) session.getAttribute(USER_ID_ATTR);
            idEstudiante = estudianteDAO.getEstudianteIdByUserId(userId);
            if (idEstudiante != null) {
                session.setAttribute(ID_ESTUDIANTE_ATTR, idEstudiante);
            } else {
                response.sendRedirect(request.getContextPath() + "/error.jsp?msg=Usuario+no+es+estudiante");
                return;
            }
        }
        
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        String[] splits = pathInfo.split("/");
        if (splits.length < 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        String resource = splits[1];
        
        if ("api".equals(resource)) {
            // Manejar llamadas API
            handleApiRequest(request, response, idEstudiante, splits);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    /**
     * Muestra el dashboard del estudiante
     */
    private void mostrarDashboard(HttpServletRequest request, HttpServletResponse response, int idEstudiante)
            throws ServletException, IOException {
        logger.info(() -> "Mostrando dashboard para estudiante ID: " + idEstudiante);
        
        // Obtener datos para el dashboard
        Map<String, Object> datosDashboard = estudianteDAO.getDatosDashboard(idEstudiante);
        request.setAttribute("datosDashboard", datosDashboard);
        
        // Obtener cursos del estudiante (limitado para mostrar en el dashboard)
        List<Map<String, Object>> cursos = estudianteDAO.getCursosEstudiante(idEstudiante);
        request.setAttribute(CURSOS_ATTR, cursos);
        
        // Obtener tareas pendientes
        List<Map<String, Object>> tareas = estudianteDAO.getTareasEstudiante(idEstudiante, null, "Pendiente");
        request.setAttribute("tareasPendientes", tareas);
        
        // Redirigir a la vista
        RequestDispatcher dispatcher = request.getRequestDispatcher("/estudianteindex.jsp");
        dispatcher.forward(request, response);
    }
    
    /**
     * Muestra la lista de cursos del estudiante
     */
    private void mostrarCursos(HttpServletRequest request, HttpServletResponse response, int idEstudiante)
            throws ServletException, IOException {
        logger.info(() -> "Mostrando cursos para estudiante ID: " + idEstudiante);
        
        // Verificar si se solicita un curso específico
        String cursoIdParam = request.getParameter("curso");
        if (cursoIdParam != null && !cursoIdParam.isEmpty()) {
            try {
                int cursoId = Integer.parseInt(cursoIdParam);
                // Buscar el curso específico
                List<Map<String, Object>> cursos = estudianteDAO.getCursosEstudiante(idEstudiante);
                Map<String, Object> cursoSeleccionado = null;
                
                for (Map<String, Object> curso : cursos) {
                    if ((Integer)curso.get("id") == cursoId) {
                        cursoSeleccionado = curso;
                        break;
                    }
                }
                
                if (cursoSeleccionado != null) {
                    // Obtener tareas de este curso
                    List<Map<String, Object>> tareasCurso = estudianteDAO.getTareasEstudiante(idEstudiante, cursoId, "Todos");
                    
                    request.setAttribute("cursoSeleccionado", cursoSeleccionado);
                    request.setAttribute("tareasCurso", tareasCurso);
                    
                    // Redirigir a vista de detalle de curso (podemos usar la misma vista de cursos)
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/estudiantecursos.jsp");
                    dispatcher.forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                logger.warning("ID de curso inválido: " + e.getMessage());
                // Continuar con la lista normal de cursos
            }
        }
        
        // Obtener todos los cursos del estudiante
        List<Map<String, Object>> cursos = estudianteDAO.getCursosEstudiante(idEstudiante);
        request.setAttribute(CURSOS_ATTR, cursos);
        
        // Redirigir a la vista
        RequestDispatcher dispatcher = request.getRequestDispatcher("/estudiantecursos.jsp");
        dispatcher.forward(request, response);
    }
    
    /**
     * Muestra la lista de tareas del estudiante, con opciones de filtrado
     */    private void mostrarTareas(HttpServletRequest request, HttpServletResponse response, int idEstudiante)
            throws ServletException, IOException {
        logger.info(() -> "Mostrando tareas para estudiante ID: " + idEstudiante);
          // Obtener parámetros de filtro
        String estadoFiltro = request.getParameter("estado");
        String cursoFiltro = request.getParameter("curso");
        
        Integer idCurso = null;
        if (cursoFiltro != null && !cursoFiltro.isEmpty() && !cursoFiltro.equals("todos")) {
            try {
                idCurso = Integer.parseInt(cursoFiltro);
            } catch (NumberFormatException e) {
                // Ignorar error y no aplicar filtro
                logger.warning("Error al convertir ID de curso: " + e.getMessage());
            }
        }
        
        // Si no hay estado de filtro, mostrar todas
        if (estadoFiltro == null || estadoFiltro.isEmpty()) {
            estadoFiltro = "Todos";
        }
        
        // Obtener tareas filtradas
        List<Map<String, Object>> tareas = estudianteDAO.getTareasEstudiante(idEstudiante, idCurso, estadoFiltro);
        
        // Log para debug
        logger.info("Estudiante ID: " + idEstudiante + 
                   ", Estado filtro: " + estadoFiltro + 
                   ", Curso filtro: " + cursoFiltro + 
                   ", Tareas encontradas: " + (tareas != null ? tareas.size() : "null"));
        
        if (tareas != null && !tareas.isEmpty()) {
            for (Map<String, Object> tarea : tareas) {
                logger.info("Tarea ID: " + tarea.get("id") + 
                           ", Título: " + tarea.get("titulo") + 
                           ", Estado: " + tarea.get("estado"));
            }
        }
        
        request.setAttribute("tareas", tareas);
        
        // Obtener cursos para el selector de filtro
        List<Map<String, Object>> cursos = estudianteDAO.getCursosEstudiante(idEstudiante);
        request.setAttribute(CURSOS_ATTR, cursos);
        
        // Conservar filtros aplicados para la vista
        request.setAttribute("estadoFiltro", estadoFiltro);
        request.setAttribute("cursoFiltro", cursoFiltro);
        
        // Redirigir a la vista
        RequestDispatcher dispatcher = request.getRequestDispatcher("/estudiantetareas.jsp");
        dispatcher.forward(request, response);
    }
    
    /**
     * Maneja solicitudes a la API interna del estudiante
     */
    private void handleApiRequest(HttpServletRequest request, HttpServletResponse response, 
                                 int idEstudiante, String[] pathParts)
            throws ServletException, IOException {
        // Configurar respuesta como JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        if (pathParts.length < 3) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL de API inválida");
            return;
        }
        
        String apiEndpoint = pathParts[2];
        
        if ("filtrarTareas".equals(apiEndpoint)) {
            // Manejar filtro de tareas
            handleFiltroTareas(request, response, idEstudiante);
        } else if ("entregarTarea".equals(apiEndpoint)) {
            // Manejar entrega de tareas
            handleEntregaTarea(request, response, idEstudiante);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint no encontrado");
        }
    }
    
    /**
     * Maneja solicitudes para filtrar tareas (endpoint de API)
     */
    private void handleFiltroTareas(HttpServletRequest request, HttpServletResponse response, int idEstudiante)
            throws ServletException, IOException {        String estadoFiltro = request.getParameter("estado");
        String cursoFiltroStr = request.getParameter("curso");
        
        Integer cursoFiltro = null;
        if (cursoFiltroStr != null && !cursoFiltroStr.isEmpty() && !cursoFiltroStr.equals("todos")) {
            try {
                cursoFiltro = Integer.parseInt(cursoFiltroStr);
            } catch (NumberFormatException e) {
                // Ignorar error
                logger.warning("Error al convertir ID de curso para filtro: " + e.getMessage());
            }
        }
        
        if (estadoFiltro == null || estadoFiltro.isEmpty()) {
            estadoFiltro = "Todos";
        }
        
        List<Map<String, Object>> tareas = estudianteDAO.getTareasEstudiante(idEstudiante, cursoFiltro, estadoFiltro);
        String jsonResponse = gson.toJson(tareas);
        
        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }
    
    /**
     * Maneja solicitudes para entregar tareas (endpoint de API)
     */
    private void handleEntregaTarea(HttpServletRequest request, HttpServletResponse response, int idEstudiante)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        
        try {
            // Obtener parámetros de la petición
            String tareaIdStr = request.getParameter("tareaId");
            String comentarios = request.getParameter("comentarios");
            
            if (tareaIdStr == null || tareaIdStr.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"ID de tarea no proporcionado\"}");
                return;
            }
            
            int tareaId;
            try {
                tareaId = Integer.parseInt(tareaIdStr);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"ID de tarea inválido\"}");
                return;
            }
            
            // Aquí se manejaría la subida de archivos si fuera necesario
            // Por ahora, solo registramos la entrega en la base de datos
            boolean resultado = estudianteDAO.registrarEntregaTarea(tareaId, idEstudiante, comentarios);
            
            if (resultado) {
                out.print("{\"success\": true, \"message\": \"Tarea entregada correctamente\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"Error al entregar la tarea\"}");
            }
        } catch (Exception e) {
            logger.severe("Error al entregar tarea: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Error interno del servidor\"}");
            e.printStackTrace();
        }
    }
}