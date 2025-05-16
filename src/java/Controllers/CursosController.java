package Controllers;

import Models.Curso;
import Models.Estudiante;
import Models.Conexion;
import DAOs.CursoDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
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
 * Controlador para gestionar los cursos del sistema
 */
@WebServlet("/CursosController/*")
public class CursosController extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(CursosController.class.getName());
    private CursoDAO cursoDAO;
    
    @Override
    public void init() throws ServletException {
        try {
            this.cursoDAO = new CursoDAO();
            logger.info("CursosController: CursoDAO inicializado correctamente");
        } catch (Exception e) {
            logger.severe("CursosController: Error al inicializar CursoDAO: " + e.getMessage());
            throw new ServletException("Error al inicializar CursoDAO", e);
        }
    }

    /**
     * Maneja las solicitudes GET para obtener cursos
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            String idParam = request.getParameter("id");
            String path = request.getPathInfo();
            
            // Nuevo endpoint de diagnóstico para verificar datos
            if (path != null && path.equals("/diagnostico")) {
                logger.info("Ejecutando diagnóstico de datos");
                
                // Verificar cursos
                logger.info("Verificando cursos...");
                List<Curso> todos = cursoDAO.readAll();
                JSONObject diagnostico = new JSONObject();
                
                // Información general
                diagnostico.put("totalCursos", todos.size());
                
                // Detalles de cursos
                JSONArray cursos = new JSONArray();
                for (Curso c : todos) {
                    JSONObject curso = new JSONObject();
                    curso.put("id", c.getId());
                    curso.put("nombre", c.getNombre());
                    curso.put("codigo", c.getCodigo());
                    curso.put("idProfesor", c.getIdProfesor());
                    
                    // Verificar tareas de este curso
                    try {
                        String sql = "SELECT COUNT(*) as total FROM tarea WHERE id_curso = " + c.getId();
                        try (Connection conn = new Conexion().crearConexion();
                             Statement stmt = conn.createStatement();
                             ResultSet rs = stmt.executeQuery(sql)) {
                            if (rs.next()) {
                                curso.put("numTareas", rs.getInt("total"));
                            }
                        }
                    } catch (Exception e) {
                        curso.put("error", "Error al contar tareas: " + e.getMessage());
                    }
                    
                    cursos.put(curso);
                }
                diagnostico.put("cursos", cursos);
                
                out.print(diagnostico.toString(2));
                return;
            }
            
            if (idParam != null && !idParam.isEmpty()) {
                try {
                    int id = Integer.parseInt(idParam);
                    logger.info("CursosController: buscando curso con ID: " + id);
                    Curso curso = read(id);
                    
                    if (curso != null) {
                        JSONObject jsonCurso = new JSONObject();
                        jsonCurso.put("id_curso", curso.getId());
                        jsonCurso.put("nombre", curso.getNombre());
                        jsonCurso.put("codigo", curso.getCodigo());
                        jsonCurso.put("descripcion", curso.getDescripcion());
                        jsonCurso.put("idProfesor", curso.getIdProfesor());
                        jsonCurso.put("profesor_nombre", curso.getProfesor_nombre());
                        
                        logger.info("CursosController: curso encontrado: " + jsonCurso.toString());
                        out.print(jsonCurso.toString());
                    } else {
                        logger.warning("CursosController: curso no encontrado para ID: " + id);
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        JSONObject error = new JSONObject();
                        error.put("error", "Curso no encontrado");
                        out.print(error.toString());
                    }
                } catch (NumberFormatException e) {
                    logger.severe("CursosController: Error al parsear ID: " + idParam + ". Error: " + e.getMessage());
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JSONObject error = new JSONObject();
                    error.put("error", "ID inválido: " + idParam);
                    out.print(error.toString());
                }
            } else {
                logger.info("CursosController: solicitando todos los cursos");
                List<Curso> cursos = readAll();
                JSONArray jsonArray = new JSONArray();
                
                for (Curso curso : cursos) {
                    JSONObject jsonCurso = new JSONObject();
                    jsonCurso.put("id_curso", curso.getId());
                    jsonCurso.put("nombre", curso.getNombre());
                    jsonCurso.put("codigo", curso.getCodigo());
                    jsonCurso.put("descripcion", curso.getDescripcion());
                    jsonCurso.put("idProfesor", curso.getIdProfesor());
                    jsonCurso.put("profesor_nombre", curso.getProfesor_nombre());
                    jsonArray.put(jsonCurso);
                }
                
                logger.info("CursosController: se encontraron " + cursos.size() + " cursos");
                out.print(jsonArray.toString());
            }
        } catch (Exception e) {
            logger.severe("CursosController: Error no controlado: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject error = new JSONObject();
            error.put("error", "Error interno del servidor: " + e.getMessage());
            out.print(error.toString());
        }
    }

    /**
     * Maneja las solicitudes POST para crear cursos
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            
            String requestBody = sb.toString();
            logger.info("CursosController: recibida solicitud POST con cuerpo: " + requestBody);
            
            JSONObject jsonRequest = new JSONObject(requestBody);
            
            // Crear objeto Curso
            Curso curso = new Curso();
            curso.setNombre(jsonRequest.getString("nombre"));
            curso.setCodigo(jsonRequest.getString("codigo"));
            curso.setDescripcion(jsonRequest.getString("descripcion"));
            
            // Usar la clave "idProfesor" en lugar de "id_profesor"
            curso.setIdProfesor(jsonRequest.getInt("idProfesor"));
            
            logger.info("CursosController: intentando crear curso: " + curso.getNombre());
            
            // Guardar el curso
            boolean exito = crearCurso(curso);
            
            if (exito) {
                logger.info("CursosController: curso creado con éxito");
                response.setStatus(HttpServletResponse.SC_CREATED);
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("mensaje", "Curso creado exitosamente");
                out.print(jsonResponse.toString());
            } else {
                logger.warning("CursosController: error al crear curso");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                JSONObject jsonError = new JSONObject();
                jsonError.put("error", "Error al crear el curso");
                out.print(jsonError.toString());
            }
        } catch (Exception e) {
            logger.severe("CursosController: Error en POST: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonError = new JSONObject();
            jsonError.put("error", "Error: " + e.getMessage());
            out.print(jsonError.toString());
        }
    }

    /**
     * Maneja las solicitudes PUT para actualizar cursos
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            String idParam = request.getParameter("id");
            logger.info("CursosController: recibida solicitud PUT. Parámetro ID: " + idParam);
            
            if (idParam == null || idParam.isEmpty()) {
                logger.warning("CursosController: ID no proporcionado en PUT");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(new JSONObject().put("error", "ID no proporcionado").toString());
                return;
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            
            String requestBody = sb.toString();
            logger.info("CursosController: cuerpo de la solicitud PUT: " + requestBody);
            
            JSONObject jsonRequest = new JSONObject(requestBody);
            Curso curso = new Curso();
            curso.setId(Integer.parseInt(idParam));
            curso.setNombre(jsonRequest.getString("nombre"));
            curso.setCodigo(jsonRequest.getString("codigo"));
            curso.setDescripcion(jsonRequest.getString("descripcion"));
            curso.setIdProfesor(jsonRequest.getInt("idProfesor"));
            
            logger.info("CursosController: intentando actualizar curso con ID: " + curso.getId());
            
            if (update(curso)) {
                logger.info("CursosController: curso actualizado con éxito");
                JSONObject success = new JSONObject();
                success.put("mensaje", "Curso actualizado exitosamente");
                out.print(success.toString());
            } else {
                logger.warning("CursosController: error al actualizar curso con ID: " + curso.getId());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(new JSONObject().put("error", "Error al actualizar el curso").toString());
            }
        } catch (NumberFormatException e) {
            logger.severe("CursosController: Error al parsear ID en PUT: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(new JSONObject().put("error", "ID inválido: " + e.getMessage()).toString());
        } catch (Exception e) {
            logger.severe("CursosController: Error en PUT: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(new JSONObject().put("error", e.getMessage()).toString());
        }
    }

    /**
     * Maneja las solicitudes DELETE para eliminar cursos
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            String idParam = request.getParameter("id");
            logger.info("CursosController: recibida solicitud DELETE. Parámetro ID: " + idParam);
            
            if (idParam == null || idParam.isEmpty()) {
                logger.warning("CursosController: ID no proporcionado en DELETE");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(new JSONObject().put("error", "ID no proporcionado").toString());
                return;
            }

            int id = Integer.parseInt(idParam);
            logger.info("CursosController: intentando eliminar curso con ID: " + id);
            
            if (delete(id)) {
                logger.info("CursosController: curso eliminado con éxito");
                out.print(new JSONObject().put("mensaje", "Curso eliminado exitosamente").toString());
            } else {
                logger.warning("CursosController: error al eliminar curso con ID: " + id);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(new JSONObject().put("error", "Error al eliminar el curso").toString());
            }
        } catch (NumberFormatException e) {
            logger.severe("CursosController: Error al parsear ID en DELETE: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(new JSONObject().put("error", "ID inválido: " + e.getMessage()).toString());
        } catch (Exception e) {
            logger.severe("CursosController: Error en DELETE: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(new JSONObject().put("error", e.getMessage()).toString());
        }
    }
    
    // Métodos auxiliares para interactuar con la base de datos
    
    private Curso read(int id) {
        return cursoDAO.read(id);
    }
    
    private List<Curso> readAll() {
        return cursoDAO.readAll();
    }
    
    private List<Curso> obtenerCursosPorProfesor(int idProfesor) {
        return cursoDAO.getCursosPorProfesor(idProfesor);
    }
    
    private List<Curso> obtenerCursosPorEstudiante(int idEstudiante) {
        return cursoDAO.getCursosPorEstudiante(idEstudiante);
    }
    
    private List<Estudiante> obtenerEstudiantesPorCurso(int idCurso) {
        return cursoDAO.getEstudiantesPorCurso(idCurso);
    }
    
    private boolean crearCurso(Curso curso) {
        return cursoDAO.create(curso);
    }
    
    private boolean update(Curso curso) {
        return cursoDAO.update(curso);
    }
    
    private boolean delete(int id) {
        return cursoDAO.delete(id);
    }
    
    private boolean asignarEstudiantesACurso(int idCurso, List<Integer> estudiantesIds) {
        return cursoDAO.asignarEstudiantes(idCurso, estudiantesIds);
    }
}