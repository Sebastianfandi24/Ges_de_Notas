package Controllers;

import Models.Estudiante;
import DAOs.EstudianteDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private final EstudianteDAO estudianteDAO;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Para formatear fechas

    public EstudiantesController() {
        this.estudianteDAO = new EstudianteDAO();
    }

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
            System.out.println("[EstudiantesController] GET request - ID param: " + idParam);
            
            // Si no hay ID, devolver todos los estudiantes
            if (idParam == null || idParam.trim().isEmpty()) {
                List<Estudiante> estudiantes = obtenerTodosEstudiantes();
                JSONArray jsonArray = new JSONArray();
                
                System.out.println("[EstudiantesController] Obteniendo lista de estudiantes. Total: " + 
                    (estudiantes != null ? estudiantes.size() : 0));

                if (estudiantes != null) {
                    for (Estudiante estudiante : estudiantes) {
                        jsonArray.put(convertEstudianteToJson(estudiante));
                    }
                }

                out.print(jsonArray.toString());
                return;
            }

            // Si hay ID, obtener estudiante específico
            try {
                int id = Integer.parseInt(idParam.trim());
                Estudiante estudiante = obtenerEstudiantePorId(id);

                if (estudiante != null) {
                    JSONObject jsonEstudiante = convertEstudianteToJson(estudiante);
                    out.print(jsonEstudiante.toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(new JSONObject().put("error", "Estudiante no encontrado con ID: " + id).toString());
                }
            } catch (NumberFormatException e) {
                System.err.println("[EstudiantesController] Error parsing ID: " + idParam);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(new JSONObject().put("error", "ID de estudiante inválido: " + idParam).toString());
            }
        } catch (Exception e) {
            System.err.println("[EstudiantesController] Error en doGet: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(new JSONObject()
                .put("error", "Error interno del servidor: " + e.getMessage())
                .toString());
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

            // Crear objeto Estudiante y poblarlo desde JSON
            Estudiante estudiante = parseJsonToEstudiante(jsonRequest);
            // Asignar rol de estudiante por defecto si no se especifica (ajustar según lógica)
            if (estudiante.getIdRol() == 0) {
                estudiante.setIdRol(3); // Asumiendo que 3 es el ID del rol Estudiante
            }

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
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Cambiado a BAD_REQUEST para errores de parseo/datos
            response.getWriter().print(new JSONObject().put("error", "Error en la solicitud: " + e.getMessage()).toString());
            e.printStackTrace(); // Imprimir stack trace para depuración
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

            // Crear objeto Estudiante y poblarlo desde JSON
            Estudiante estudiante = parseJsonToEstudiante(jsonRequest);
            // Asegurarse de que el ID del estudiante esté presente para la actualización
            if (estudiante.getId() == 0) {
                throw new IllegalArgumentException("El ID del estudiante es requerido para actualizar.");
            }
            // Obtener el idUsuario asociado al idEstudiante para la actualización del Usuario
            Estudiante existingEstudiante = obtenerEstudiantePorId(estudiante.getId());
            if (existingEstudiante == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().print(new JSONObject().put("error", "Estudiante no encontrado para actualizar").toString());
                return;
            }
            estudiante.setIdUsuario(existingEstudiante.getIdUsuario()); // Establecer el idUsuario correcto

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
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Cambiado a BAD_REQUEST para errores de parseo/datos
            response.getWriter().print(new JSONObject().put("error", "Error en la solicitud: " + e.getMessage()).toString());
            e.printStackTrace(); // Imprimir stack trace para depuración
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
            System.out.println("[EstudiantesController] DELETE request - ID param: " + idParam);
            
            if (idParam == null || idParam.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print(new JSONObject().put("error", "ID de estudiante no proporcionado").toString());
                return;
            }
            
            try {
                int id = Integer.parseInt(idParam.trim());
                System.out.println("[EstudiantesController] Intentando eliminar estudiante con ID: " + id);
                
                // Verificar primero si el estudiante existe
                Estudiante estudiante = obtenerEstudiantePorId(id);
                if (estudiante == null) {
                    System.out.println("[EstudiantesController] Estudiante con ID: " + id + " no encontrado");
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().print(new JSONObject().put("error", "Estudiante no encontrado con ID: " + id).toString());
                    return;
                }
                
                boolean exito = eliminarEstudiante(id);

                if (exito) {
                    System.out.println("[EstudiantesController] Estudiante eliminado con éxito, ID: " + id);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().print(new JSONObject().put("mensaje", "Estudiante eliminado exitosamente").toString());
                } else {
                    System.out.println("[EstudiantesController] Error al eliminar estudiante con ID: " + id);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().print(new JSONObject().put("error", "Error al eliminar el estudiante").toString());
                }
            } catch (NumberFormatException e) {
                System.err.println("[EstudiantesController] Error al convertir ID a número: " + idParam);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print(new JSONObject().put("error", "ID de estudiante inválido: " + idParam).toString());
            }
        } catch (Exception e) {
            System.err.println("[EstudiantesController] Error en doDelete: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(new JSONObject().put("error", e.getMessage()).toString());
        }
    }

    // Métodos auxiliares

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

    // Convierte un objeto Estudiante a JSONObject incluyendo campos de Usuario
    private JSONObject convertEstudianteToJson(Estudiante estudiante) {
        JSONObject jsonEstudiante = new JSONObject();
        try {
            // Campos de Estudiante - Asegurarse de que id_estudiante esté presente
            int idEstudiante = estudiante.getId(); // Este es el ID del estudiante
            jsonEstudiante.put("id_estudiante", idEstudiante);
            
            // Log para depuración
            System.out.println("[EstudiantesController] Convirtiendo estudiante a JSON - ID: " + idEstudiante);
            
            // Resto de campos del estudiante
            jsonEstudiante.put("fecha_nacimiento", estudiante.getFechaNacimiento() != null ? dateFormat.format(estudiante.getFechaNacimiento()) : null);
            jsonEstudiante.put("direccion", estudiante.getDireccion());
            jsonEstudiante.put("telefono", estudiante.getTelefono());
            jsonEstudiante.put("numero_identificacion", estudiante.getNumeroIdentificacion());
            jsonEstudiante.put("estado", estudiante.getEstado());
            jsonEstudiante.put("promedio_academico", estudiante.getPromedioAcademico());
            
            // Campos de Usuario (heredados)
            jsonEstudiante.put("id_usuario", estudiante.getIdUsuario());
            jsonEstudiante.put("nombre", estudiante.getNombre());
            jsonEstudiante.put("correo", estudiante.getCorreo());
            jsonEstudiante.put("id_rol", estudiante.getIdRol());
            jsonEstudiante.put("fecha_creacion", estudiante.getFechaCreacion() != null ? dateFormat.format(estudiante.getFechaCreacion()) : null);
            jsonEstudiante.put("ultima_conexion", estudiante.getUltimaConexion() != null ? dateFormat.format(estudiante.getUltimaConexion()) : null);
            
        } catch (Exception e) {
            System.err.println("[EstudiantesController] Error al convertir estudiante a JSON: " + e.getMessage());
            e.printStackTrace();
        }
        return jsonEstudiante;
    }

    // Parsea un JSONObject a un objeto Estudiante
    private Estudiante parseJsonToEstudiante(JSONObject jsonRequest) throws Exception {
        Estudiante estudiante = new Estudiante();

        // Campos de Estudiante
        if (jsonRequest.has("id_estudiante")) {
            estudiante.setId(jsonRequest.getInt("id_estudiante"));
        }
        if (jsonRequest.has("fecha_nacimiento") && !jsonRequest.isNull("fecha_nacimiento")) {
            try {
                Date fechaNac = dateFormat.parse(jsonRequest.getString("fecha_nacimiento"));
                estudiante.setFechaNacimiento(fechaNac);
            } catch (Exception e) {
                System.err.println("Error al parsear fecha_nacimiento: " + jsonRequest.optString("fecha_nacimiento"));
                // Puedes decidir lanzar una excepción o asignar null
                estudiante.setFechaNacimiento(null);
            }
        }
        estudiante.setDireccion(jsonRequest.optString("direccion", null));
        estudiante.setTelefono(jsonRequest.optString("telefono", null));
        estudiante.setNumeroIdentificacion(jsonRequest.optString("numero_identificacion", null));
        estudiante.setEstado(jsonRequest.optString("estado", "Activo")); // Valor por defecto
        estudiante.setPromedioAcademico((float) jsonRequest.optDouble("promedio_academico", 0.0));

        // Campos de Usuario (heredados)
        if (jsonRequest.has("id_usuario")) { // Necesario para la actualización
            estudiante.setIdUsuario(jsonRequest.getInt("id_usuario"));
        }
        estudiante.setNombre(jsonRequest.getString("nombre")); // Campo requerido
        estudiante.setCorreo(jsonRequest.getString("correo")); // Campo requerido
        // La contraseña solo se debe establecer si se proporciona (para creación o actualización)
        // Check for 'contrasena' (lowercase, no tilde)
        if (jsonRequest.has("contrasena") && !jsonRequest.isNull("contrasena") && !jsonRequest.getString("contrasena").isEmpty()) {
            estudiante.setContraseña(jsonRequest.getString("contrasena"));
        } else if (!jsonRequest.has("id_estudiante")) {
            // Si es creación y no se provee contraseña, lanzar error
            // Updated error message to use 'contrasena'
            throw new IllegalArgumentException("La contrasena es requerida para crear un nuevo estudiante.");
        }
        estudiante.setIdRol(jsonRequest.optInt("id_rol", 3)); // Rol Estudiante por defecto

        return estudiante;
    }
}