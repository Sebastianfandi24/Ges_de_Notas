package Controllers;

import DAOs.CursoDAO;
import DAOs.TareaDAO;
import Models.Curso;
import Models.Tarea;
import Models.Conexion;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Collections;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/profesor/tareas")
public class ProfesorTareasServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(ProfesorTareasServlet.class.getName());
    private transient CursoDAO cursoDAO;
    private transient TareaDAO tareaDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            cursoDAO = new CursoDAO();
            tareaDAO = new TareaDAO();
            logger.info("[ProfesorTareasServlet] Servlet inicializado correctamente");
            logger.info("[ProfesorTareasServlet] CursoDAO y TareaDAO creados");
        } catch (Exception e) {
            logger.severe("[ProfesorTareasServlet] Error inicializando servlet: " + e.getMessage());
            throw new ServletException("Error inicializando servlet", e);
        }
    }
      @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            logger.info("[ProfesorTareasServlet] Recibida solicitud GET");
            logger.info("[ProfesorTareasServlet] URI: " + request.getRequestURI());
            logger.info("[ProfesorTareasServlet] Query String: " + request.getQueryString());
            
            // Verificar si es una solicitud de diagnóstico
            String pathInfo = request.getPathInfo();
            if (pathInfo != null && pathInfo.equals("/diagnostico")) {
                logger.info("[ProfesorTareasServlet] Accediendo a la página de diagnóstico");
                cargarDatos(request, response);
                request.getRequestDispatcher("/diagnostico-tareas.jsp").forward(request, response);
                return;
            }
            
            // Procesamiento normal
            cargarDatos(request, response);
            
            // Detectar y manejar problema de contexto duplicado
            String contextPath = request.getContextPath();
            if (contextPath.contains("/Ges_de_Notas/Ges_de_Notas")) {
                logger.warning("[ProfesorTareasServlet] Detectado contexto duplicado: " + contextPath);
                contextPath = contextPath.replace("/Ges_de_Notas/Ges_de_Notas", "/Ges_de_Notas");
                logger.info("[ProfesorTareasServlet] Contexto corregido: " + contextPath);
                
                // Redirigir a la URL correcta
                response.sendRedirect(contextPath + "/profesor/tareas");
                return;
            }            // Forward a la vista
            @SuppressWarnings("unchecked")
            List<Tarea> tareas = (List<Tarea>) request.getAttribute("tareas");
            logger.info("[ProfesorTareasServlet] Redirigiendo a la vista profesortareas.jsp con " + 
                       (tareas != null ? tareas.size() : "0") + " tareas");
            logger.info("[ProfesorTareasServlet] Ruta del dispatcher: /profesortareas.jsp");
            
            // Imprimir detalles de cada tarea para diagnóstico
            if (tareas != null && !tareas.isEmpty()) {
                logger.info("[ProfesorTareasServlet] Detalles de las tareas antes de enviar a la vista:");
                for (Tarea tarea : tareas) {
                    logger.info("[ProfesorTareasServlet] - ID: " + tarea.getId() + 
                               ", Título: " + tarea.getTitulo() + 
                               ", Curso: " + tarea.getCursoNombre() + 
                               ", Estado: " + tarea.getEstado());
                }
            } else {
                logger.warning("[ProfesorTareasServlet] No hay tareas para mostrar en la vista");
                // Crear una tarea de prueba para diagnóstico de la vista
                if (request.getParameter("testview") != null) {
                    tareas = new java.util.ArrayList<>();
                    Tarea tareaPrueba = new Tarea();
                    tareaPrueba.setId(999);
                    tareaPrueba.setTitulo("Tarea de Prueba");
                    tareaPrueba.setDescripcion("Esta es una tarea de prueba para diagnosticar la vista");
                    tareaPrueba.setFechaAsignacion(new java.util.Date());
                    tareaPrueba.setFechaEntrega(new java.util.Date());
                    tareaPrueba.setIdCurso(1);
                    tareaPrueba.setCursoNombre("Curso de Prueba");
                    tareaPrueba.setEstado("Activa");
                    tareas.add(tareaPrueba);
                    request.setAttribute("tareas", tareas);
                    logger.info("[ProfesorTareasServlet] Añadida tarea de prueba para diagnóstico");
                }
            }
            
            request.getRequestDispatcher("/profesortareas.jsp").forward(request, response);        } catch (Exception e) {
            logger.severe("[ProfesorTareasServlet] Error procesando solicitud: " + e.getMessage());
            e.printStackTrace();
            try {
                // Añadir detalles más específicos sobre el error para ayudar en la depuración
                String errorMessage = "Ha ocurrido un error al procesar la solicitud: " + 
                                     (e.getMessage() != null ? e.getMessage() : "Error desconocido");
                
                // Código de diagnóstico adicional
                request.setAttribute("errorDetails", e.getClass().getName());
                request.setAttribute("errorStackTrace", e.getStackTrace().length > 0 ? 
                                    e.getStackTrace()[0].toString() : "No disponible");
                request.setAttribute("error", errorMessage);
                
                // Registrar más detalles en el log
                logger.severe("[ProfesorTareasServlet] Detalles del error: " + 
                             "Tipo: " + e.getClass().getName() + ", " +
                             "Mensaje: " + e.getMessage());
                
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            } catch (ServletException | IOException ex) {
                logger.severe("[ProfesorTareasServlet] Error adicional al redirigir a página de error: " + ex.getMessage());
            }
        }
    }
    
    // Método para cargar los datos de cursos y tareas
    private void cargarDatos(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        logger.info("[ProfesorTareasServlet] Iniciando carga de datos");
        
        // Validar conexión a la base de datos
        if (!Conexion.testConnection()) {
            logger.severe("[ProfesorTareasServlet] No se pudo establecer conexión con la base de datos");
            throw new ServletException("Error de conexión con la base de datos");
        }
        
        // Obtener el ID del profesor de la sesión
        HttpSession session = request.getSession(false);
        logger.info("[ProfesorTareasServlet] Sesión obtenida: " + (session != null ? "válida" : "nula"));
        
        if (session == null || session.getAttribute("userId") == null) {
            logger.warning("[ProfesorTareasServlet] No hay sesión activa o userId no encontrado");
            logger.info("[ProfesorTareasServlet] Redirigiendo a: " + request.getContextPath() + "/login.jsp");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        int userId = (int) session.getAttribute("userId");
        logger.info("[ProfesorTareasServlet] UserID obtenido de la sesión: " + userId);
        
        DAOs.ProfesorDAO profesorDAO = new DAOs.ProfesorDAO();
        Integer idProfesor;
        try {
            idProfesor = profesorDAO.getProfesorIdByUsuario(userId);
            if (idProfesor == null) {
                logger.severe("[ProfesorTareasServlet] No se encontró profesor para usuario ID: " + userId);
                throw new ServletException("No se encontró profesor para usuario ID: " + userId);
            }
            logger.info("[ProfesorTareasServlet] ID de profesor encontrado: " + idProfesor);
        } catch (Exception e) {
            logger.severe("[ProfesorTareasServlet] Error obteniendo ID de profesor: " + e.getMessage());
            throw new ServletException("Error obteniendo ID de profesor", e);
        }

        logger.info("[ProfesorTareasServlet] Obteniendo datos para profesor ID: " + idProfesor);
        
        // Obtener cursos del profesor
        List<Curso> cursos = cursoDAO.getCursosPorProfesor(idProfesor);
        logger.info("[ProfesorTareasServlet] Cursos encontrados: " + cursos.size());
        logger.info("[ProfesorTareasServlet] Detalles de cursos:");
        for (Curso curso : cursos) {
            logger.info("[ProfesorTareasServlet] - Curso ID: " + curso.getId() + 
                       ", Nombre: " + curso.getNombre() + 
                       ", Código: " + curso.getCodigo());
        }
        request.setAttribute("cursos", cursos);
        
        // Obtener todas las tareas del profesor
        logger.info("[ProfesorTareasServlet] Consultando tareas para profesor ID: " + idProfesor);
        List<Tarea> tareas = tareaDAO.getTareasPorProfesor(idProfesor);
        
        // Imprimimos los detalles de las tareas para depuración
        logger.info("[ProfesorTareasServlet] Resultado de getTareasPorProfesor: " + 
                   (tareas != null ? tareas.size() + " tareas" : "null"));
        
        if (tareas != null) {
            // Verificar si la lista está vacía
            if (tareas.isEmpty()) {
                logger.warning("[ProfesorTareasServlet] La lista de tareas está vacía");
            } else {
                // Si hay tareas, verificamos cada una
                for (Tarea tarea : tareas) {                    logger.info(String.format("[ProfesorTareasServlet] Tarea cargada: ID=%d, Título='%s', Curso='%s', Estado='%s'",
                        tarea.getId(),
                        tarea.getTitulo(),
                        tarea.getCursoNombre(),
                        tarea.getEstado()
                    ));
                }
            }
        }
        if (tareas != null) {
            logger.info("[ProfesorTareasServlet] Detalles de tareas:");
            for (Tarea tarea : tareas) {
                logger.info("[ProfesorTareasServlet] - Tarea ID: " + tarea.getId() + 
                          "\n    Título: " + tarea.getTitulo() +                          "\n    Curso ID: " + tarea.getIdCurso() + 
                          "\n    Curso Nombre: " + tarea.getCursoNombre() +
                          "\n    Fecha Asignación: " + tarea.getFechaAsignacion() + 
                          "\n    Fecha Entrega: " + tarea.getFechaEntrega() +
                          "\n    Estado: " + tarea.getEstado());
            }
        } else {
            logger.warning("[ProfesorTareasServlet] La lista de tareas es NULL");
            tareas = java.util.Collections.emptyList(); // Aseguramos que no sea null
        }
        
        request.setAttribute("tareas", tareas != null ? tareas : Collections.<Tarea>emptyList());
        logger.info("[ProfesorTareasServlet] Tareas cargadas y asignadas al request");
        
        // Imprimir información de depuración
        logger.info("[ProfesorTareasServlet] Servlet path: " + request.getServletPath());
        logger.info("[ProfesorTareasServlet] Request URI: " + request.getRequestURI());
        logger.info("[ProfesorTareasServlet] Context path: " + request.getContextPath());
        
        // Añadir información de diagnóstico al request
        Map<String, Object> diagnostico = new HashMap<>();
        diagnostico.put("sesionValida", session != null);
        diagnostico.put("userId", userId);
        diagnostico.put("idProfesor", idProfesor);
        diagnostico.put("numCursos", cursos != null ? cursos.size() : 0);
        diagnostico.put("numTareas", tareas != null ? tareas.size() : 0);
        diagnostico.put("timestamp", new Date());
        request.setAttribute("diagnostico", diagnostico);
    }
    
    /**
     * Maneja las solicitudes POST a /profesor/tareas
     * Redirige la solicitud a CrearTareaServlet
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("[ProfesorTareasServlet] Recibida solicitud POST - Redirigiendo a CrearTareaServlet");
        try {
            // Redireccionar a CrearTareaServlet
            request.getRequestDispatcher("/profesor/tareas/crear").forward(request, response);
        } catch (Exception e) {
            logger.severe("[ProfesorTareasServlet] Error redirigiendo solicitud POST: " + e.getMessage());
            throw new ServletException("Error procesando formulario de creación de tarea", e);
        }
    }
}
