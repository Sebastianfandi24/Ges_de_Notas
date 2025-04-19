package Controllers;

import Models.Estudiante;
import DAOs.EstudianteDAO;
import java.io.IOException;
import java.io.PrintWriter;
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
 * Controlador para gestionar los estudiantes del sistema
 */
@WebServlet("/EstudiantesController")
public class EstudiantesController extends HttpServlet {

    /**
     * Maneja las solicitudes GET para obtener estudiantes
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            String idParam = request.getParameter("id");
            
            if (idParam != null) {
                // Obtener un estudiante específico
                int id = Integer.parseInt(idParam);
                Estudiante estudiante = obtenerEstudiantePorId(id);
                
                if (estudiante != null) {
                    JSONObject jsonEstudiante = new JSONObject();
                    jsonEstudiante.put("id_estudiante", estudiante.getId());
                    jsonEstudiante.put("nombre", estudiante.getNombre());
                    jsonEstudiante.put("correo", estudiante.getCorreo());
                    jsonEstudiante.put("numero_identificacion", estudiante.getNumeroIdentificacion());
                    jsonEstudiante.put("estado", estudiante.getEstado());
                    jsonEstudiante.put("promedio_academico", estudiante.getPromedioAcademico());
                    jsonEstudiante.put("telefono", estudiante.getTelefono());
                    out.print(jsonEstudiante.toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(new JSONObject().put("error", "Estudiante no encontrado").toString());
                }
            } else {
                // Obtener todos los estudiantes
                List<Estudiante> estudiantes = obtenerTodosEstudiantes();
                JSONArray jsonArray = new JSONArray();
                
                for (Estudiante estudiante : estudiantes) {
                    JSONObject jsonEstudiante = new JSONObject();
                    jsonEstudiante.put("id_estudiante", estudiante.getId());
                    jsonEstudiante.put("nombre", estudiante.getNombre());
                    jsonEstudiante.put("correo", estudiante.getCorreo());
                    jsonEstudiante.put("numero_identificacion", estudiante.getNumeroIdentificacion());
                    jsonEstudiante.put("estado", estudiante.getEstado());
                    jsonEstudiante.put("promedio_academico", estudiante.getPromedioAcademico());
                    jsonEstudiante.put("telefono", estudiante.getTelefono());
                    jsonArray.put(jsonEstudiante);
                }
                
                out.print(jsonArray.toString());
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(new JSONObject().put("error", e.getMessage()).toString());
        }
    }

    /**
     * Maneja las solicitudes POST para crear estudiantes
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
            
            // Crear objeto Estudiante
            Estudiante estudiante = new Estudiante();
            estudiante.setNombre(jsonRequest.getString("nombre"));
            // estudiante.setApellido(jsonRequest.getString("apellido")); // Campo no existe en el modelo
            estudiante.setCorreo(jsonRequest.getString("email")); // Corregido: setEmail() -> setCorreo()
            estudiante.setTelefono(jsonRequest.getString("telefono"));
            // estudiante.setGrado(jsonRequest.getString("grado")); // Campo no existe en el modelo
            
            // Guardar el estudiante
            boolean exito = crearEstudiante(estudiante);
            
            if (exito) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().print(new JSONObject().put("mensaje", "Estudiante creado exitosamente").toString());
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().print(new JSONObject().put("error", "Error al crear el estudiante").toString());
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(new JSONObject().put("error", e.getMessage()).toString());
        }
    }

    /**
     * Maneja las solicitudes PUT para actualizar estudiantes
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
            
            // Crear objeto Estudiante
            Estudiante estudiante = new Estudiante();
            estudiante.setId(jsonRequest.getInt("id_estudiante"));
            estudiante.setNombre(jsonRequest.getString("nombre"));
            // estudiante.setApellido(jsonRequest.getString("apellido")); // Campo no existe en el modelo
            estudiante.setCorreo(jsonRequest.getString("email")); // Corregido: setEmail() -> setCorreo()
            estudiante.setTelefono(jsonRequest.getString("telefono"));
            // estudiante.setGrado(jsonRequest.getString("grado")); // Campo no existe en el modelo
            
            // Actualizar el estudiante
            boolean exito = actualizarEstudiante(estudiante);
            
            if (exito) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().print(new JSONObject().put("mensaje", "Estudiante actualizado exitosamente").toString());
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().print(new JSONObject().put("error", "Error al actualizar el estudiante").toString());
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(new JSONObject().put("error", e.getMessage()).toString());
        }
    }

    /**
     * Maneja las solicitudes DELETE para eliminar estudiantes
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
                boolean exito = eliminarEstudiante(id);
                
                if (exito) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().print(new JSONObject().put("mensaje", "Estudiante eliminado exitosamente").toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().print(new JSONObject().put("error", "Error al eliminar el estudiante").toString());
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print(new JSONObject().put("error", "ID de estudiante no proporcionado").toString());
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(new JSONObject().put("error", e.getMessage()).toString());
        }
    }
    
    // Métodos auxiliares para interactuar con la base de datos
    
    private final EstudianteDAO estudianteDAO;
    
    public EstudiantesController() {
        this.estudianteDAO = new EstudianteDAO();
    }
    
    private Estudiante obtenerEstudiantePorId(int id) {
        return estudianteDAO.read(id);
    }
    
    private List<Estudiante> obtenerTodosEstudiantes() {
        return estudianteDAO.readAll();
    }
    
    private boolean crearEstudiante(Estudiante estudiante) {
        return estudianteDAO.create(estudiante);
    }
    
    private boolean actualizarEstudiante(Estudiante estudiante) {
        return estudianteDAO.update(estudiante);
    }
    
    private boolean eliminarEstudiante(int id) {
        return estudianteDAO.delete(id);
    }
}