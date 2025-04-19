package Controllers;

import DAOs.ActividadDAO;
import Models.Actividad;
import Models.Rol;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Controlador para gestionar las actividades del sistema
 */
@WebServlet("/ActividadesController")
public class ActividadesController extends HttpServlet {

    /**
     * Maneja las solicitudes GET para obtener actividades
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            String idParam = request.getParameter("id");
            
            if (idParam != null) {
                // Obtener una actividad específica
                int id = Integer.parseInt(idParam);
                Actividad actividad = obtenerActividadPorId(id);
                
                if (actividad != null) {
                    JSONObject jsonActividad = new JSONObject();
                    jsonActividad.put("id_actividad", actividad.getId());
                    jsonActividad.put("nombre", actividad.getNombre());
                    jsonActividad.put("enlace", actividad.getEnlace());
                    
                    JSONArray rolesArray = new JSONArray();
                    for (Rol rol : actividad.getRoles()) {
                        JSONObject jsonRol = new JSONObject();
                        jsonRol.put("id_rol", rol.getId());
                        jsonRol.put("nombre", rol.getNombre());
                        rolesArray.put(jsonRol);
                    }
                    jsonActividad.put("roles", rolesArray);
                    
                    out.print(jsonActividad.toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(new JSONObject().put("error", "Actividad no encontrada").toString());
                }
            } else {
                // Obtener todas las actividades
                List<Actividad> actividades = obtenerTodasActividades();
                JSONArray jsonArray = new JSONArray();
                
                for (Actividad actividad : actividades) {
                    JSONObject jsonActividad = new JSONObject();
                    jsonActividad.put("id_actividad", actividad.getId());
                    jsonActividad.put("nombre", actividad.getNombre());
                    jsonActividad.put("enlace", actividad.getEnlace());
                    
                    JSONArray rolesArray = new JSONArray();
                    for (Rol rol : actividad.getRoles()) {
                        JSONObject jsonRol = new JSONObject();
                        jsonRol.put("id_rol", rol.getId());
                        jsonRol.put("nombre", rol.getNombre());
                        rolesArray.put(jsonRol);
                    }
                    jsonActividad.put("roles", rolesArray);
                    
                    jsonArray.put(jsonActividad);
                }
                
                out.print(jsonArray.toString());
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(new JSONObject().put("error", e.getMessage()).toString());
        }
    }

    /**
     * Maneja las solicitudes POST para crear actividades
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Leer el cuerpo de la solicitud
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            
            JSONObject jsonRequest = new JSONObject(sb.toString());
            
            // Crear objeto Actividad
            Actividad actividad = new Actividad();
            actividad.setNombre(jsonRequest.getString("nombre"));
            actividad.setEnlace(jsonRequest.getString("enlace"));
            
            // Obtener roles seleccionados
            JSONArray rolesArray = jsonRequest.getJSONArray("roles");
            List<Integer> rolesIds = new ArrayList<>();
            for (int i = 0; i < rolesArray.length(); i++) {
                rolesIds.add(rolesArray.getInt(i));
            }
            
            // Guardar la actividad
            boolean exito = crearActividad(actividad, rolesIds);
            
            if (exito) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().print(new JSONObject().put("mensaje", "Actividad creada exitosamente").toString());
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().print(new JSONObject().put("error", "Error al crear la actividad").toString());
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(new JSONObject().put("error", e.getMessage()).toString());
        }
    }

    /**
     * Maneja las solicitudes PUT para actualizar actividades
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Leer el cuerpo de la solicitud
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            
            JSONObject jsonRequest = new JSONObject(sb.toString());
            
            // Crear objeto Actividad
            Actividad actividad = new Actividad();
            actividad.setId(jsonRequest.getInt("id_actividad"));
            actividad.setNombre(jsonRequest.getString("nombre"));
            actividad.setEnlace(jsonRequest.getString("enlace"));
            
            // Obtener roles seleccionados
            JSONArray rolesArray = jsonRequest.getJSONArray("roles");
            List<Integer> rolesIds = new ArrayList<>();
            for (int i = 0; i < rolesArray.length(); i++) {
                rolesIds.add(rolesArray.getInt(i));
            }
            
            // Actualizar la actividad
            boolean exito = actualizarActividad(actividad, rolesIds);
            
            if (exito) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().print(new JSONObject().put("mensaje", "Actividad actualizada exitosamente").toString());
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().print(new JSONObject().put("error", "Error al actualizar la actividad").toString());
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(new JSONObject().put("error", e.getMessage()).toString());
        }
    }

    /**
     * Maneja las solicitudes DELETE para eliminar actividades
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            String idParam = request.getParameter("id");
            
            if (idParam != null) {
                int id = Integer.parseInt(idParam);
                boolean exito = eliminarActividad(id);
                
                if (exito) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().print(new JSONObject().put("mensaje", "Actividad eliminada exitosamente").toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().print(new JSONObject().put("error", "Error al eliminar la actividad").toString());
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print(new JSONObject().put("error", "ID de actividad no proporcionado").toString());
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(new JSONObject().put("error", e.getMessage()).toString());
        }
    }
    
    // Métodos auxiliares para interactuar con la base de datos
    
    private final ActividadDAO actividadDAO;
    
    public ActividadesController() {
        this.actividadDAO = new ActividadDAO();
    }
    
    private Actividad obtenerActividadPorId(int id) throws SQLException {
        return actividadDAO.obtenerPorId(id);
    }
    
    private List<Actividad> obtenerTodasActividades() throws SQLException {
        return actividadDAO.obtenerTodas();
    }
    
    private boolean crearActividad(Actividad actividad, List<Integer> rolesIds) throws SQLException {
        return actividadDAO.crear(actividad, rolesIds);
    }
    
    private boolean actualizarActividad(Actividad actividad, List<Integer> rolesIds) throws SQLException {
        return actividadDAO.actualizar(actividad, rolesIds);
    }
    
    private boolean eliminarActividad(int id) throws SQLException {
        return actividadDAO.eliminar(id);
    }
}