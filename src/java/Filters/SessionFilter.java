package Filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "SessionFilter", urlPatterns = {
    "/menu",
    "/perfil",
    "/RolesController",
    "/ProfesoresController",
    "/ProfesoresController/*",
    "/profesor",
    "/profesor/*",
    "/profesor/cursos",
    "/profesor/cursos/*",
    "/profesor/tareas",
    "/profesor/tareas/*",
    "/estudiante",
    "/estudiante/*",
    "/estudiante/cursos",
    "/estudiante/cursos/*",
    "/estudiante/dashboard",
    "/estudiante/dashboard/*",
    "/estudiante/tareas",
    "/estudiante/tareas/*",
    "/EstudiantesController",
    "/CursosController",
    "/CursosController/*",
    "/ActividadesController",
    "/api/admin/*"
})
public class SessionFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization required
    }    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;          // Headers de seguridad mejorados para evitar cache y navegación hacia atrás
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, private");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", -1);
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        
        HttpSession session = request.getSession(false);
        
        String requestPath = request.getServletPath();
        System.out.println("[SessionFilter] Verificando acceso a: " + requestPath);
        
        // Verificar si la sesión existe y contiene el atributo userId
        boolean loggedIn = (session != null && session.getAttribute("userId") != null);
        
        if (!loggedIn) {
            System.out.println("[SessionFilter] Acceso denegado - No hay sesión activa");
            // Redirect to login page if no session
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        } else {
            System.out.println("[SessionFilter] Acceso permitido para usuario: " + session.getAttribute("userNombre") + 
                              " (ID: " + session.getAttribute("userId") + ")");
            chain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        // Cleanup, if necessary
    }
}