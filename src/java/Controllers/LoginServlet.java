package Controllers;

import DAOs.UsuarioDAO;
import Models.Usuario;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO;
    
    @Override
    public void init() throws ServletException {
        try {
            usuarioDAO = new UsuarioDAO();
        } catch (SQLException e) {
            throw new ServletException("Error initializing UsuarioDAO", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String correo = request.getParameter("email");
        String contraseña = request.getParameter("password");
        String mensaje = "";

        System.out.println("[LoginServlet] Intento de inicio de sesión - Email: " + correo);

        try {
            Usuario usuario = usuarioDAO.autenticarUsuario(correo, contraseña);
            if (usuario != null) {
                System.out.println("[LoginServlet] Autenticación exitosa - Usuario: " + usuario.getNombre() + ", Rol: " + usuario.getIdRol());
                // Actualizar última conexión
                usuario.setUltimaConexion(new Date(System.currentTimeMillis()));
                usuarioDAO.actualizarUltimaConexion(usuario);
                
                // Iniciar sesión
                HttpSession session = request.getSession();
                session.setAttribute("userId", usuario.getId());
                session.setAttribute("userNombre", usuario.getNombre());
                session.setAttribute("userRol", usuario.getIdRol());
                
                // Redirigir a MenuController en vez de JSP
                response.sendRedirect(request.getContextPath() + "/menu");
            } else {
                mensaje = "Credenciales inválidas";
                System.out.println("[LoginServlet] Error - Credenciales inválidas para el email: " + correo);
                request.setAttribute("error", mensaje);
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            mensaje = "Error en el servidor: " + e.getMessage();
            System.out.println("[LoginServlet] Error del servidor: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", mensaje);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("logout".equals(action)) {
            // Imprimir para depuración que se está cerrando la sesión
            HttpSession session = request.getSession(false);
            if (session != null) {
                System.out.println("[LoginServlet] Cerrando sesión - Usuario: " + session.getAttribute("userNombre"));
                
                // Invalidar la sesión para eliminar todos los atributos
                session.invalidate();
                
                // Crear una nueva sesión sin atributos para asegurarse
                session = request.getSession(true);
                session.setMaxInactiveInterval(1);  // Establecer tiempo muy corto
                session.invalidate();  // Invalidar inmediatamente
            }
            
            // Enviar cabeceras para evitar caché
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            
            // Redirigir a login
            System.out.println("[LoginServlet] Sesión cerrada correctamente, redirigiendo a login");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }
}