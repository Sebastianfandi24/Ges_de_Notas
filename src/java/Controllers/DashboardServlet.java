package Controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard", "/dashboard/", "/dashboard/admin/*", "/dashboard/profesor/*", "/dashboard/estudiante/*"})
public class DashboardServlet extends HttpServlet {

    // Configuración de menús por rol
    private static final Map<String, String[][]> MENU_CONFIG = new HashMap<>();
    
    static {
        // Menú para Administrador (rol 3)
        MENU_CONFIG.put("3", new String[][] {
            {"Dashboard", "bi bi-speedometer2", "/admin/index.jsp"},
            {"Gestión de Profesores", "bi bi-person-badge", "/admin/profesores.jsp"},
            {"Gestión de Estudiantes", "bi bi-people", "/admin/estudiantes.jsp"},
            {"Gestión de Cursos", "bi bi-journal-bookmark", "/admin/cursos.jsp"},
            {"Gestión de Actividades", "bi bi-list-check", "/admin/actividades.jsp"}
        });
        
        // Menú para Profesor (rol 2)
        MENU_CONFIG.put("2", new String[][] {
            {"Dashboard", "bi bi-speedometer2", "/profesor/index.jsp"},
            {"Mis Cursos", "bi bi-journal-text", "/profesor/cursos.jsp"},
            {"Gestión de Tareas", "bi bi-list-task", "/profesor/tareas.jsp"},
            {"Gestión de Notas", "bi bi-star", "/profesor/notas.jsp"}
        });
        
        // Menú para Estudiante (rol 1)
        MENU_CONFIG.put("1", new String[][] {
            {"Dashboard", "bi bi-speedometer2", "/estudiante/index.jsp"},
            {"Mis Cursos", "bi bi-journal-text", "/estudiante/cursos.jsp"},
            {"Mis Tareas", "bi bi-list-task", "/estudiante/tareas.jsp"},
            {"Mis Notas", "bi bi-star", "/estudiante/notas.jsp"}
        });
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String userRol = "";
    
        // Verificar si hay una sesión activa
        if (session != null && session.getAttribute("userRol") != null) {
            userRol = session.getAttribute("userRol").toString();
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
    
        // Obtener los elementos del menú para el rol del usuario
        String[][] menuItems = MENU_CONFIG.get(userRol);
        if (menuItems == null || menuItems.length == 0) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso no autorizado para este rol");
            return;
        }
        
        // Guardar los elementos del menú en la sesión para que estén disponibles en todas las páginas
        session.setAttribute("menuItems", menuItems);
    
        // Obtener la ruta solicitada
        String pathInfo = request.getPathInfo();
        String requestURI = request.getRequestURI();

        // Log para depuración
        System.out.println("[DashboardServlet] Path solicitado: " + pathInfo);
        System.out.println("[DashboardServlet] URI solicitada: " + requestURI);

        // Página por defecto para el rol
        String defaultPage = menuItems[0][2]; // Primera opción del menú
        String targetPage = defaultPage;
        
        // Si no hay pathInfo o es solo una barra, usar la página por defecto
        if (pathInfo == null || pathInfo.equals("/")) {
            targetPage = defaultPage;
        } else {
            // Determinar el prefijo según el rol
            String rolPrefix = "";
            
            if (userRol.equals("3")) {
                rolPrefix = "/admin";
            } else if (userRol.equals("2")) {
                rolPrefix = "/profesor";
            } else if (userRol.equals("1")) {
                rolPrefix = "/estudiante";
            }
            
            // Extraer el nombre del archivo JSP de la URI
            String requestPath = requestURI;
            String contextPath = request.getContextPath();
            String dashboardPath = contextPath + "/dashboard";
            
            // Si la URI empieza con el path del dashboard, extraerla
            if (requestPath.startsWith(dashboardPath)) {
                String relativePath = requestPath.substring(dashboardPath.length());
                
                // Construir la ruta completa para buscarla en el menú
                System.out.println("[DashboardServlet] Ruta extraída: " + relativePath);
                
                // Verificar si la ruta extraída corresponde a alguna del menú
                boolean rutaValida = false;
                
                for (String[] item : menuItems) {
                    System.out.println("[DashboardServlet] Comparando con: " + item[2]);
                    if (relativePath.equals(item[2])) {
                        targetPage = item[2];
                        rutaValida = true;
                        System.out.println("[DashboardServlet] ¡Coincidencia encontrada!: " + targetPage);
                        break;
                    }
                }
                
                // Si no hay coincidencia exacta, intentar una coincidencia parcial
                if (!rutaValida) {
                    for (String[] item : menuItems) {
                        if (relativePath.contains(item[2].substring(1))) { // Quitamos la primera barra "/"
                            targetPage = item[2];
                            rutaValida = true;
                            System.out.println("[DashboardServlet] ¡Coincidencia parcial encontrada!: " + targetPage);
                            break;
                        }
                    }
                }
                
                // Si aún no hay coincidencia, usar la página por defecto
                if (!rutaValida) {
                    System.out.println("[DashboardServlet] No se encontró coincidencia, usando página por defecto");
                }
            }
        }
        
        // Configurar atributos para la vista
        request.setAttribute("currentPath", targetPage);
        request.setAttribute("includePage", targetPage);
        System.out.println("[DashboardServlet] Estableciendo includePage: " + targetPage);
        
        // Hacer forward al layout principal
        try {
            request.getRequestDispatcher("/dashboard/index.jsp").forward(request, response);
        } catch (Exception e) {
            System.out.println("[DashboardServlet] Error al procesar la solicitud: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al procesar la solicitud");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Procesar solicitudes POST si es necesario
        doGet(request, response);
    }
}