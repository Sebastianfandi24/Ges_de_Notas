package Controllers;

import DAOs.PerfilDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "PerfilController", urlPatterns = {"/perfil"})
public class PerfilController extends HttpServlet {
    
    private PerfilDAO perfilDAO;
    
    @Override
    public void init() throws ServletException {
        try {
            perfilDAO = new PerfilDAO();
        } catch (SQLException e) {
            throw new ServletException("Error initializing PerfilDAO", e);
        }
    }
      @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        int userId = (int) session.getAttribute("userId");
        int userRol = (int) session.getAttribute("userRol");
        
        System.out.println("[PerfilController] Cargando perfil para usuario ID: " + userId + ", Rol: " + userRol);
        
        try {
            Map<String, Object> perfilUsuario = perfilDAO.obtenerPerfilUsuario(userId, userRol);
            request.setAttribute("perfil", perfilUsuario);
            
            // Depurar los datos recuperados
            System.out.println("[PerfilController] Datos recuperados del perfil: " + perfilUsuario);
            
        } catch (SQLException e) {
            System.err.println("[PerfilController] Error al cargar perfil de usuario: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Ocurrió un error al cargar tu información de perfil.");
        }
        
        request.getRequestDispatcher("/perfil.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        int userId = (int) session.getAttribute("userId");
        int userRol = (int) session.getAttribute("userRol");
        
        // Recopilar datos del formulario
        Map<String, Object> datosUsuario = new HashMap<>();
        datosUsuario.put("id_usu", userId);
        datosUsuario.put("id_rol", userRol);
        datosUsuario.put("nombre", request.getParameter("nombre"));
        datosUsuario.put("correo", request.getParameter("correo"));
        
        // Solo procesar la contraseña si se proporcionó una nueva
        String nuevaContraseña = request.getParameter("nueva_contraseña");
        if (nuevaContraseña != null && !nuevaContraseña.isEmpty()) {
            datosUsuario.put("nueva_contraseña", nuevaContraseña);
        }
        
        // Procesar datos específicos según el rol
        switch (userRol) {
            case 1: // Estudiante
                procesarDatosEstudiante(request, datosUsuario);
                break;
            case 2: // Profesor
                procesarDatosProfesor(request, datosUsuario);
                break;
            case 3: // Administrador
                procesarDatosAdministrador(request, datosUsuario);
                break;
        }
        
        try {
            boolean actualizado = perfilDAO.actualizarPerfilUsuario(datosUsuario);
            if (actualizado) {
                // Actualizar el nombre en la sesión si ha cambiado
                session.setAttribute("userNombre", datosUsuario.get("nombre"));
                request.setAttribute("mensaje", "Perfil actualizado exitosamente");
            } else {
                request.setAttribute("error", "No se pudo actualizar el perfil");
            }
            
            // Recargar los datos actualizados
            Map<String, Object> perfilUsuario = perfilDAO.obtenerPerfilUsuario(userId, userRol);
            request.setAttribute("perfil", perfilUsuario);
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar perfil de usuario: " + e.getMessage());
            request.setAttribute("error", "Ocurrió un error al actualizar tu perfil: " + e.getMessage());
        }
        
        request.getRequestDispatcher("/perfil.jsp").forward(request, response);
    }
    
    private void procesarDatosEstudiante(HttpServletRequest request, Map<String, Object> datosUsuario) {
        try {
            String fechaNacStr = request.getParameter("fecha_nacimiento");
            if (fechaNacStr != null && !fechaNacStr.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date fecha = sdf.parse(fechaNacStr);
                datosUsuario.put("fecha_nacimiento", new Date(fecha.getTime()));
            }
        } catch (ParseException e) {
            System.err.println("Error al parsear la fecha de nacimiento: " + e.getMessage());
        }
        
        datosUsuario.put("direccion", request.getParameter("direccion"));
        datosUsuario.put("telefono", request.getParameter("telefono"));
        datosUsuario.put("numero_identificacion", request.getParameter("numero_identificacion"));
    }
    
    private void procesarDatosProfesor(HttpServletRequest request, Map<String, Object> datosUsuario) {
        try {
            String fechaNacStr = request.getParameter("fecha_nacimiento");
            if (fechaNacStr != null && !fechaNacStr.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date fecha = sdf.parse(fechaNacStr);
                datosUsuario.put("fecha_nacimiento", new Date(fecha.getTime()));
            }
        } catch (ParseException e) {
            System.err.println("Error al parsear la fecha de nacimiento: " + e.getMessage());
        }
        
        datosUsuario.put("direccion", request.getParameter("direccion"));
        datosUsuario.put("telefono", request.getParameter("telefono"));
        datosUsuario.put("grado_academico", request.getParameter("grado_academico"));
        datosUsuario.put("especializacion", request.getParameter("especializacion"));
    }
    
    private void procesarDatosAdministrador(HttpServletRequest request, Map<String, Object> datosUsuario) {
        datosUsuario.put("departamento", request.getParameter("departamento"));
    }
}
