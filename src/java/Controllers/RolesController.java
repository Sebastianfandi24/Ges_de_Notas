package Controllers;

import DAOs.RolDAO;
import Models.Rol;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Controlador para gestionar los roles del sistema
 */
@WebServlet("/RolesController")
public class RolesController extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(RolesController.class.getName());
    private final RolDAO rolDAO;
    
    public RolesController() {
        this.rolDAO = new RolDAO();
    }

    /**
     * Maneja las solicitudes GET para obtener roles
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        LOGGER.info("RolesController: recibida solicitud GET");
        
        try (PrintWriter out = response.getWriter()) {
            String idParam = request.getParameter("id");
            
            if (idParam != null) {
                // Obtener un rol específico
                LOGGER.info("RolesController: solicitando rol con ID: " + idParam);
                int id = Integer.parseInt(idParam);
                Rol rol = obtenerRolPorId(id);
                
                if (rol != null) {
                    LOGGER.info("RolesController: rol encontrado. Convirtiendo a JSON");
                    JSONObject jsonRol = new JSONObject();
                    jsonRol.put("id_rol", rol.getId());
                    jsonRol.put("nombre", rol.getNombre());
                    out.print(jsonRol.toString());
                } else {
                    LOGGER.warning("RolesController: rol con ID " + id + " no encontrado");
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(new JSONObject().put("error", "Rol no encontrado").toString());
                }
            } else {
                // Obtener todos los roles
                LOGGER.info("RolesController: solicitando todos los roles");
                List<Rol> roles = obtenerTodosRoles();
                JSONArray jsonArray = new JSONArray();
                
                LOGGER.info("RolesController: se encontraron " + roles.size() + " roles");
                for (Rol rol : roles) {
                    JSONObject jsonRol = new JSONObject();
                    jsonRol.put("id_rol", rol.getId());
                    jsonRol.put("nombre", rol.getNombre());
                    jsonArray.put(jsonRol);
                }
                
                LOGGER.fine("RolesController: Array JSON generado: " + jsonArray.toString());
                out.print(jsonArray.toString());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "RolesController: Error en doGet", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(new JSONObject().put("error", e.getMessage()).toString());
        }
    }
    
    private Rol obtenerRolPorId(int id) throws SQLException {
        return rolDAO.obtenerPorId(id);
    }
    
    private List<Rol> obtenerTodosRoles() throws SQLException {
        return rolDAO.obtenerTodos();
    }
}