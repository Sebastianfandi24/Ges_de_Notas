package Controllers;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Servlet para diagnosticar rutas y redirecciones
 */
@WebServlet("/rutasdiag")
public class RutasDiagnosticoServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(RutasDiagnosticoServlet.class.getName());
      @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        // Registrar información en el log
        logger.info("********* DIAGNÓSTICO DE RUTAS *********");
        logger.info("ContextPath: " + request.getContextPath());
        logger.info("ServletPath: " + request.getServletPath());
        logger.info("PathInfo: " + request.getPathInfo());
        logger.info("RequestURI: " + request.getRequestURI());
        logger.info("RequestURL: " + request.getRequestURL());
        
        // Verificar si se solicita una acción específica
        String accion = request.getParameter("accion");
        if (accion != null) {
            try {
                if (accion.equals("fixRedirect")) {
                    // Redirección corregida para tareas de profesor
                    response.sendRedirect(request.getContextPath() + "/profesor/tareas");
                    return;
                } else if (accion.equals("testTareas")) {
                    // Crear atributos de request para simular datos
                    request.setAttribute("mensaje", "Página cargada mediante diagnóstico de rutas");
                    // Forward directo a la página de tareas sin pasar por el servlet
                    request.getRequestDispatcher("/profesortareas.jsp").forward(request, response);
                    return;
                } else if (accion.equals("testIndex")) {
                    // Forward directo a la página de índice de profesor
                    request.getRequestDispatcher("/profesorindex.jsp").forward(request, response);
                    return;
                }
            } catch (IOException | ServletException e) {
                logger.severe("Error al procesar acción " + accion + ": " + e.getMessage());
                throw e;
            }
        }
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Diagnóstico de Rutas</title>");
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
            
            out.println("<h2>Enlaces directos a tareas (probar para diagnóstico)</h2>");
            out.println("<div class='link-box'>");
            // Primera opción: enlace con context path
            out.println("<a href='" + request.getContextPath() + "/profesor/tareas'>1. /profesor/tareas (con contexto)</a>");
            
            // Segunda opción: enlace absoluto desde la raíz
            out.println("<a href='/profesor/tareas'>2. /profesor/tareas (absoluto)</a>");
            
            // Tercera opción: enlace relativo
            out.println("<a href='profesor/tareas'>3. profesor/tareas (relativo)</a>");
            
            // Cuarta opción: directamente al JSP
            out.println("<a href='" + request.getContextPath() + "/profesortareas.jsp'>4. /profesortareas.jsp (directo al JSP)</a>");
            
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
