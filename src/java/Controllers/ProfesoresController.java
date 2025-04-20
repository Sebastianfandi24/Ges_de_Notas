package Controllers;

import Models.Profesor;
import DAOs.ProfesorDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
 * Controlador para gestionar los profesores del sistema
 */
@WebServlet(urlPatterns = {"/ProfesoresController", "/ProfesoresController/*"})
public class ProfesoresController extends HttpServlet {

    private final ProfesorDAO profesorDAO;
    private final SimpleDateFormat dateFormat;
    private final SimpleDateFormat dateTimeFormat;
    private static final Logger LOGGER = Logger.getLogger(ProfesoresController.class.getName());

    public ProfesoresController() {
        this.profesorDAO = new ProfesorDAO();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LOGGER.info("ProfesoresController inicializado");
    }
    
    /**
     * Maneja las solicitudes GET para obtener profesores
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        LOGGER.info("ProfesoresController: recibida solicitud GET");
        
        try (PrintWriter out = response.getWriter()) {
            String idParam = request.getParameter("id");
            
            if (idParam != null && !idParam.equals("undefined") && !idParam.trim().isEmpty()) {
                try {
                    // Obtener un profesor específico
                    LOGGER.info("ProfesoresController: solicitando profesor con ID: " + idParam);
                    int id = Integer.parseInt(idParam);
                    Profesor profesor = profesorDAO.read(id);
                    
                    if (profesor != null) {
                        LOGGER.info("ProfesoresController: profesor encontrado. Convirtiendo a JSON");
                        JSONObject jsonProfesor = profesorToJson(profesor);
                        LOGGER.fine("ProfesoresController: JSON generado: " + jsonProfesor.toString());
                        out.print(jsonProfesor.toString());
                    } else {
                        LOGGER.warning("ProfesoresController: profesor con ID " + id + " no encontrado");
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print(new JSONObject().put("error", "Profesor no encontrado").toString());
                    }
                } catch (NumberFormatException e) {
                    LOGGER.warning("ProfesoresController: ID de profesor inválido: " + idParam);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(new JSONObject().put("error", "ID de profesor inválido").toString());
                }
            } else {
                // Obtener todos los profesores
                LOGGER.info("ProfesoresController: solicitando todos los profesores");
                List<Profesor> profesores = profesorDAO.readAll();
                JSONArray jsonArray = new JSONArray();
                
                LOGGER.info("ProfesoresController: se encontraron " + profesores.size() + " profesores");
                for (Profesor profesor : profesores) {
                    jsonArray.put(profesorToJson(profesor));
                }
                
                LOGGER.fine("ProfesoresController: Array JSON generado: " + jsonArray.toString());
                out.print(jsonArray.toString());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "ProfesoresController: Error en doGet", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(new JSONObject().put("error", e.getMessage()).toString());
        }
    }

    /**
     * Convierte un objeto Profesor a formato JSON
     */
    private JSONObject profesorToJson(Profesor profesor) {
        try {
            JSONObject jsonProfesor = new JSONObject();
            jsonProfesor.put("id_profesor", profesor.getId());
            jsonProfesor.put("id_usu", profesor.getId_usu());
            jsonProfesor.put("nombre", profesor.getNombre());
            jsonProfesor.put("correo", profesor.getCorreo());
            jsonProfesor.put("telefono", profesor.getTelefono() != null ? profesor.getTelefono() : "");
            jsonProfesor.put("grado_academico", profesor.getGradoAcademico() != null ? profesor.getGradoAcademico() : "");
            jsonProfesor.put("especializacion", profesor.getEspecializacion() != null ? profesor.getEspecializacion() : "");
            jsonProfesor.put("estado", profesor.getEstado() != null ? profesor.getEstado() : "");
            jsonProfesor.put("direccion", profesor.getDireccion() != null ? profesor.getDireccion() : "");

            // Formatear fechas para enviarlas en formato yyyy-MM-dd
            if (profesor.getFechaNacimiento() != null) {
                String fechaNacFormatted = dateFormat.format(profesor.getFechaNacimiento());
                jsonProfesor.put("fechaNacimiento", fechaNacFormatted);
            } else {
                jsonProfesor.put("fechaNacimiento", JSONObject.NULL);
            }
            
            if (profesor.getFechaContratacion() != null) {
                String fechaContratacionFormatted = dateFormat.format(profesor.getFechaContratacion());
                jsonProfesor.put("fechaContratacion", fechaContratacionFormatted);
            } else {
                jsonProfesor.put("fechaContratacion", JSONObject.NULL);
            }

            // Siempre mostrar '********' para la contraseña
            jsonProfesor.put("contraseña_descodificada", "********");

            return jsonProfesor;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al convertir profesor a JSON", e);
            throw e;
        }
    }

    /**
     * Maneja las solicitudes POST para crear profesores
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        LOGGER.info("ProfesoresController: recibida solicitud POST");

        try {
            // Leer el cuerpo de la solicitud
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }

            String requestBody = sb.toString();
            LOGGER.info("ProfesoresController: Cuerpo de la solicitud recibido: " + requestBody);

            JSONObject jsonRequest = new JSONObject(requestBody);
            LOGGER.info("ProfesoresController: JSON recibido: " + jsonRequest.toString());

            // Crear objeto Profesor
            Profesor profesor = new Profesor();
            profesor.setNombre(jsonRequest.getString("nombre"));
            profesor.setCorreo(jsonRequest.getString("correo"));

            // Asignar la contraseña directamente sin validación estricta
            String contrasenaRecibida = jsonRequest.optString("contrasena", null); // Cambiado a 'contrasena' sin ñ
            LOGGER.info("ProfesoresController: Contraseña recibida en JSON: " + contrasenaRecibida);
            profesor.setContraseña(contrasenaRecibida);
            LOGGER.info("ProfesoresController: Contraseña asignada directamente.");

            profesor.setTelefono(jsonRequest.optString("telefono", null));
            profesor.setGradoAcademico(jsonRequest.optString("grado_academico", null));
            profesor.setEspecializacion(jsonRequest.optString("especializacion", null));
            profesor.setEstado(jsonRequest.optString("estado", "Activo"));
            profesor.setDireccion(jsonRequest.optString("direccion", null));
            profesor.setIdRol(2); // Rol de profesor

            LOGGER.info("ProfesoresController: Objeto profesor creado: " + profesor.getNombre() + " - " + profesor.getCorreo());

            // Parse dates (corregido para usar los nombres del frontend)
            String fechaNacStr = jsonRequest.optString("fechaNacimiento", null);
            if (fechaNacStr != null && !fechaNacStr.isEmpty()) {
                try {
                    LOGGER.info("ProfesoresController: Parseando fecha de nacimiento: " + fechaNacStr);
                    profesor.setFechaNacimiento(dateFormat.parse(fechaNacStr));
                } catch (ParseException e) {
                    LOGGER.log(Level.SEVERE, "ProfesoresController: Error al parsear fecha de nacimiento", e);
                    throw new ServletException("Formato de fecha de nacimiento inválido", e);
                }
            }

            String fechaContratacionStr = jsonRequest.optString("fechaContratacion", null);
            if (fechaContratacionStr != null && !fechaContratacionStr.isEmpty()) {
                try {
                    LOGGER.info("ProfesoresController: Parseando fecha de contratación: " + fechaContratacionStr);
                    // Agregar la hora si solo se proporciona la fecha
                    if (fechaContratacionStr.length() == 10) { // formato yyyy-MM-dd
                        fechaContratacionStr += " 00:00:00";
                        LOGGER.info("ProfesoresController: Fecha contratación ajustada con hora: " + fechaContratacionStr);
                    }
                    profesor.setFechaContratacion(dateTimeFormat.parse(fechaContratacionStr));
                } catch (ParseException e) {
                    LOGGER.log(Level.SEVERE, "ProfesoresController: Error al parsear fecha de contratación", e);
                    throw new ServletException("Formato de fecha de contratación inválido. Use 'yyyy-MM-dd' o 'yyyy-MM-dd HH:mm:ss'.", e);
                }
            } else {
                LOGGER.info("ProfesoresController: Sin fecha de contratación proporcionada.");
            }

            // Guardar el profesor
            LOGGER.info("ProfesoresController: Creando profesor: " + profesor.getNombre());
            boolean exito = crearProfesor(profesor);

            if (exito) {
                LOGGER.info("ProfesoresController: Profesor creado exitosamente");
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().print(new JSONObject().put("mensaje", "Profesor creado exitosamente").toString());
            } else {
                LOGGER.severe("ProfesoresController: Error al crear el profesor");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().print(new JSONObject().put("error", "Error al crear el profesor").toString());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "ProfesoresController: Error general en doPost", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(new JSONObject().put("error", e.getMessage()).toString());
        }
    }

    /**
     * Maneja las solicitudes PUT para actualizar profesores
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        LOGGER.info("ProfesoresController: recibida solicitud PUT");

        try {
            // Leer el cuerpo de la solicitud
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }

            String requestBody = sb.toString();
            LOGGER.info("ProfesoresController: Cuerpo de la solicitud recibido: " + requestBody);

            JSONObject jsonRequest = new JSONObject(requestBody);
            LOGGER.info("ProfesoresController: JSON recibido para actualización: " + jsonRequest.toString());

            // Crear objeto Profesor
            Profesor profesor = new Profesor();
            profesor.setId(jsonRequest.getInt("id_profesor"));
            profesor.setIdUsu(jsonRequest.getInt("id_usu"));
            profesor.setNombre(jsonRequest.getString("nombre"));
            profesor.setCorreo(jsonRequest.getString("correo"));
            profesor.setTelefono(jsonRequest.optString("telefono", null));
            profesor.setGradoAcademico(jsonRequest.optString("grado_academico", null));
            profesor.setEspecializacion(jsonRequest.optString("especializacion", null));
            profesor.setEstado(jsonRequest.optString("estado", "Activo"));
            profesor.setDireccion(jsonRequest.optString("direccion", null));

            LOGGER.info("ProfesoresController: Objeto profesor creado para actualización: " + profesor.getNombre());

            // Parse dates
            String fechaNacStr = jsonRequest.optString("fechaNacimiento", null);
            if (fechaNacStr != null && !fechaNacStr.isEmpty()) {
                try {
                    LOGGER.info("ProfesoresController: Parseando fecha de nacimiento: " + fechaNacStr);
                    profesor.setFechaNacimiento(dateFormat.parse(fechaNacStr));
                } catch (ParseException e) {
                    LOGGER.log(Level.SEVERE, "ProfesoresController: Error al parsear fecha de nacimiento", e);
                    throw new ServletException("Formato de fecha de nacimiento inválido", e);
                }
            } else {
                LOGGER.info("ProfesoresController: Sin fecha de nacimiento");
            }

            String fechaContratacionStr = jsonRequest.optString("fechaContratacion", null);
            if (fechaContratacionStr != null && !fechaContratacionStr.isEmpty()) {
                try {
                    LOGGER.info("ProfesoresController: Parseando fecha de contratación: " + fechaContratacionStr);
                    // Agregar la hora si solo se proporciona la fecha
                    if (fechaContratacionStr.length() == 10) { // formato yyyy-MM-dd
                        fechaContratacionStr += " 00:00:00";
                        profesor.setFechaContratacion(dateTimeFormat.parse(fechaContratacionStr));
                        LOGGER.info("ProfesoresController: Fecha contratación con hora añadida: " + fechaContratacionStr);
                    } else {
                        profesor.setFechaContratacion(dateTimeFormat.parse(fechaContratacionStr));
                    }
                } catch (ParseException e) {
                    LOGGER.log(Level.SEVERE, "ProfesoresController: Error al parsear fecha de contratación", e);
                    throw new ServletException("Formato de fecha de contratación inválido", e);
                }
            } else {
                LOGGER.info("ProfesoresController: Sin fecha de contratación");
            }

            // Obtener nueva contraseña si se proporcionó
            String contrasenaRecibida = jsonRequest.optString("contrasena", null);
            if (contrasenaRecibida != null && !contrasenaRecibida.isEmpty()) {
                LOGGER.info("ProfesoresController: Contraseña proporcionada para actualización");
                profesor.setContraseña(contrasenaRecibida);
            }

            // Actualizar el profesor
            LOGGER.info("ProfesoresController: Actualizando profesor ID: " + profesor.getId());
            boolean exito = actualizarProfesor(profesor);

            if (exito) {
                LOGGER.info("ProfesoresController: Profesor actualizado exitosamente");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().print(new JSONObject().put("mensaje", "Profesor actualizado exitosamente").toString());
            } else {
                LOGGER.severe("ProfesoresController: Error al actualizar el profesor");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().print(new JSONObject().put("error", "Error al actualizar el profesor").toString());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "ProfesoresController: Error en doPut", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(new JSONObject().put("error", e.getMessage()).toString());
        }
    }

    /**
     * Maneja las solicitudes DELETE para eliminar profesores
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        LOGGER.info("ProfesoresController: recibida solicitud DELETE");
        
        try {
            // Obtener el ID del path
            String pathInfo = request.getPathInfo();
            LOGGER.info("ProfesoresController: Path info recibido: " + pathInfo);

            if (pathInfo == null || pathInfo.equals("/")) {
                LOGGER.warning("ProfesoresController: Path inválido");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print(new JSONObject().put("error", "Path inválido").toString());
                return;
            }
            
            // Extraer el ID del path (/123 -> 123)
            String idStr = pathInfo.substring(1);
            LOGGER.info("ProfesoresController: ID extraído del path: " + idStr);
            
            if (idStr != null && !idStr.equals("undefined") && !idStr.trim().isEmpty()) {
                try {
                    int id = Integer.parseInt(idStr);
                    LOGGER.info("ProfesoresController: Eliminando profesor ID: " + id);
                    boolean exito = eliminarProfesor(id);
                    
                    if (exito) {
                        LOGGER.info("ProfesoresController: Profesor eliminado exitosamente");
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().print(new JSONObject().put("mensaje", "Profesor eliminado exitosamente").toString());
                    } else {
                        LOGGER.severe("ProfesoresController: Error al eliminar el profesor");
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        response.getWriter().print(new JSONObject().put("error", "Error al eliminar el profesor").toString());
                    }
                } catch (NumberFormatException e) {
                    LOGGER.warning("ProfesoresController: ID de profesor inválido: " + idStr);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().print(new JSONObject().put("error", "ID de profesor inválido").toString());
                }
            } else {
                LOGGER.warning("ProfesoresController: ID de profesor no proporcionado o inválido");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print(new JSONObject().put("error", "ID de profesor no proporcionado o inválido").toString());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "ProfesoresController: Error en doDelete", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(new JSONObject().put("error", e.getMessage()).toString());
        }
    }
    
    // Métodos auxiliares para interactuar con la base de datos
    private boolean crearProfesor(Profesor profesor) {
        return profesorDAO.create(profesor);
    }

    private boolean actualizarProfesor(Profesor profesor) {
        return profesorDAO.update(profesor);
    }

    private boolean eliminarProfesor(int id) {
        return profesorDAO.delete(id);
    }
}