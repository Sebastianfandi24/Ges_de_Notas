package Controllers;

import DAOs.CursoDAO;
import DAOs.TareaDAO;
import Models.Curso;
import Models.Tarea;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
            logger.info("ProfesorTareasServlet inicializado correctamente");
        } catch (Exception e) {
            logger.severe("Error inicializando ProfesorTareasServlet: " + e.getMessage());
            throw new ServletException("Error inicializando servlet", e);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Verificar si es una solicitud de diagnóstico
            String pathInfo = request.getPathInfo();
            if (pathInfo != null && pathInfo.equals("/diagnostico")) {
                logger.info("Accediendo a la página de diagnóstico");
                cargarDatos(request, response);
                request.getRequestDispatcher("/diagnostico-tareas.jsp").forward(request, response);
                return;
            }
            
            // Procesamiento normal
            cargarDatos(request, response);
            
            // Detectar y manejar problema de contexto duplicado
            String contextPath = request.getContextPath();
            if (contextPath.contains("/Ges_de_Notas/Ges_de_Notas")) {
                logger.warning("Detectado contexto duplicado: " + contextPath);
                contextPath = contextPath.replace("/Ges_de_Notas/Ges_de_Notas", "/Ges_de_Notas");
                logger.info("Contexto corregido: " + contextPath);
                
                // Redirigir a la URL correcta
                response.sendRedirect(contextPath + "/profesor/tareas");
                return;
            }

            // Forward a la vista
            logger.info("ProfesorTareasServlet: Redirigiendo a la vista profesortareas.jsp con " + 
                       (request.getAttribute("tareas") != null ? ((List<Tarea>)request.getAttribute("tareas")).size() : "0") + " tareas");
            request.getRequestDispatcher("/profesortareas.jsp").forward(request, response);

        } catch (Exception e) {
            logger.severe("ProfesorTareasServlet: Error procesando solicitud: " + e.getMessage());
            e.printStackTrace(); // Imprimir stack trace para depuración
            try {
                request.setAttribute("error", "Ha ocurrido un error al procesar la solicitud. Por favor, inténtelo de nuevo.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            } catch (ServletException | IOException ex) {
                logger.severe("Error adicional al redirigir a página de error: " + ex.getMessage());
            }
        }
    }
    
    // Método para cargar los datos de cursos y tareas
    private void cargarDatos(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Obtener el ID del profesor de la sesión
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        int userId = (int) session.getAttribute("userId");
        DAOs.ProfesorDAO profesorDAO = new DAOs.ProfesorDAO();
        Integer idProfesor;
        try {
            idProfesor = profesorDAO.getProfesorIdByUsuario(userId);
            if (idProfesor == null) {
                logger.severe("No se encontró profesor para usuario ID: " + userId);
                throw new ServletException("No se encontró profesor para usuario ID: " + userId);
            }
        } catch (Exception e) {
            logger.severe("Error obteniendo ID de profesor: " + e.getMessage());
            throw new ServletException("Error obteniendo ID de profesor", e);
        }

        logger.info("ProfesorTareasServlet: Obteniendo datos para profesor ID: " + idProfesor);
        
        // Obtener cursos del profesor
        List<Curso> cursos = cursoDAO.getCursosPorProfesor(idProfesor);
        request.setAttribute("cursos", cursos);
        logger.info("ProfesorTareasServlet: Encontrados " + cursos.size() + " cursos");
        for (Curso curso : cursos) {
            logger.info("Curso encontrado: ID=" + curso.getId() + ", Nombre=" + curso.getNombre());
        }

        // Obtener todas las tareas del profesor
        logger.info("ProfesorTareasServlet: Consultando tareas para profesor ID: " + idProfesor);
        List<Tarea> tareas = tareaDAO.getTareasPorProfesor(idProfesor);
        
        // Imprimimos los detalles de las tareas para depuración
        logger.info("ProfesorTareasServlet: Resultado de getTareasPorProfesor: " + (tareas != null ? tareas.size() + " tareas" : "null"));
        if (tareas != null) {
            for (Tarea tarea : tareas) {
                logger.info("Tarea encontrada: ID=" + tarea.getId() + 
                          ", Título=" + tarea.getTitulo() + 
                          ", ID_Curso=" + tarea.getIdCurso() + 
                          ", Curso=" + (tarea.getCurso_nombre() != null ? tarea.getCurso_nombre() : "sin nombre"));
            }
        } else {
            logger.warning("La lista de tareas es NULL");
            tareas = java.util.Collections.emptyList(); // Aseguramos que no sea null
        }
        
        request.setAttribute("tareas", tareas);
        logger.info("ProfesorTareasServlet: Encontradas " + tareas.size() + " tareas");

        // Imprimir información de depuración
        logger.info("Servlet path: " + request.getServletPath());
        logger.info("Request URI: " + request.getRequestURI());
        logger.info("Context path: " + request.getContextPath());
    }
}
