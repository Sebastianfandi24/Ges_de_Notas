package Controllers;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet para verificar el estado de la sesión de usuario
 * Utilizado por el JavaScript del cliente para validar periódicamente
 * si la sesión sigue activa y prevenir navegación no autorizada
 */
@WebServlet(name = "SessionCheckServlet", urlPatterns = {"/sessioncheck"})
public class SessionCheckServlet extends HttpServlet {

    /**
     * Maneja las peticiones GET para verificar el estado de la sesión
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Configurar headers de respuesta para evitar caché
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, private");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "-1");
        
        // Obtener la sesión actual sin crear una nueva
        HttpSession session = request.getSession(false);
        
        PrintWriter out = response.getWriter();
        
        try {
            // Verificar si existe sesión y si tiene usuario autenticado
            if (session != null && session.getAttribute("usuario") != null) {
                // Sesión válida - renovar tiempo de expiración
                session.setMaxInactiveInterval(30 * 60); // 30 minutos
                
                // Respuesta JSON indicando sesión activa
                out.write("{\"sessionActive\": true, \"message\": \"Sesión activa\"}");
                response.setStatus(HttpServletResponse.SC_OK);
                
            } else {
                // Sesión inválida o expirada
                out.write("{\"sessionActive\": false, \"message\": \"Sesión expirada\"}");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
            
        } catch (Exception e) {
            // Error en la verificación
            out.write("{\"sessionActive\": false, \"message\": \"Error en verificación\"}");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            
            // Log del error para debugging
            System.err.println("Error en SessionCheckServlet: " + e.getMessage());
            e.printStackTrace();
            
        } finally {
            out.close();
        }
    }

    /**
     * Maneja las peticiones POST (redirige a GET)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Información del servlet
     */
    @Override
    public String getServletInfo() {
        return "Servlet para verificación de estado de sesión";
    }
}
