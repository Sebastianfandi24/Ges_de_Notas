package Controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "TestServlet", urlPatterns = {"/test"})
public class TestServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Test Servlet</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; padding: 20px; }");
            out.println("table { border-collapse: collapse; width: 100%; margin-bottom: 20px; }");
            out.println("th, td { text-align: left; padding: 8px; border: 1px solid #ddd; }");
            out.println("th { background-color: #f2f2f2; }");
            out.println("tr:nth-child(even) { background-color: #f9f9f9; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Diagnóstico del Sistema</h1>");
            
            // Información de la solicitud
            out.println("<h2>Información de la solicitud</h2>");
            out.println("<table>");
            out.println("<tr><th>Propiedad</th><th>Valor</th></tr>");
            out.println("<tr><td>ContextPath</td><td>" + request.getContextPath() + "</td></tr>");
            out.println("<tr><td>ServletPath</td><td>" + request.getServletPath() + "</td></tr>");
            out.println("<tr><td>PathInfo</td><td>" + request.getPathInfo() + "</td></tr>");
            out.println("<tr><td>RequestURI</td><td>" + request.getRequestURI() + "</td></tr>");
            out.println("<tr><td>RequestURL</td><td>" + request.getRequestURL() + "</td></tr>");
            out.println("<tr><td>Protocol</td><td>" + request.getProtocol() + "</td></tr>");
            out.println("<tr><td>QueryString</td><td>" + request.getQueryString() + "</td></tr>");
            out.println("</table>");
            
            // Información de cabeceras
            out.println("<h2>Cabeceras de solicitud</h2>");
            out.println("<table>");
            out.println("<tr><th>Cabecera</th><th>Valor</th></tr>");
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                out.println("<tr><td>" + headerName + "</td><td>" + headerValue + "</td></tr>");
            }
            out.println("</table>");
            
            // Información de sesión
            out.println("<h2>Información de sesión</h2>");
            HttpSession session = request.getSession(false);
            if (session != null) {
                out.println("<table>");
                out.println("<tr><th>Propiedad</th><th>Valor</th></tr>");
                out.println("<tr><td>ID de sesión</td><td>" + session.getId() + "</td></tr>");
                out.println("<tr><td>Tiempo de creación</td><td>" + new java.util.Date(session.getCreationTime()) + "</td></tr>");
                out.println("<tr><td>Último acceso</td><td>" + new java.util.Date(session.getLastAccessedTime()) + "</td></tr>");
                out.println("<tr><td>Tiempo máximo inactivo</td><td>" + session.getMaxInactiveInterval() + " segundos</td></tr>");
                
                // Atributos de sesión
                out.println("<tr><td colspan='2'><strong>Atributos de sesión:</strong></td></tr>");
                Enumeration<String> attrNames = session.getAttributeNames();
                while (attrNames.hasMoreElements()) {
                    String attrName = attrNames.nextElement();
                    Object attrValue = session.getAttribute(attrName);
                    out.println("<tr><td>" + attrName + "</td><td>" + attrValue + "</td></tr>");
                }
                out.println("</table>");
            } else {
                out.println("<p>No hay sesión activa</p>");
            }
            
            // Enlaces de prueba
            out.println("<h2>Enlaces de prueba:</h2>");
            out.println("<ul>");
            out.println("<li><a href='" + request.getContextPath() + "/profesor/tareas'>Profesor Tareas</a></li>");
            out.println("<li><a href='" + request.getContextPath() + "/profesortareas.jsp'>Profesor Tareas JSP</a></li>");
            out.println("<li><a href='" + request.getContextPath() + "/profesorindex.jsp'>Profesor Index JSP</a></li>");
            out.println("</ul>");
            
            out.println("</body>");
            out.println("</html>");
        }
    }
}
