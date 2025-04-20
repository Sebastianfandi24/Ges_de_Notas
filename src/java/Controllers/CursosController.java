package Controllers;

import Models.Curso;
import Models.Estudiante;
import DAOs.CursoDAO;
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
 * Controlador para gestionar los cursos del sistema
 */
@WebServlet("/CursosController/*")
public class CursosController extends HttpServlet {

    /**
     * Maneja las solicitudes GET para obtener cursos
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            String pathInfo = request.getPathInfo();
            String idParam = request.getParameter("id");
            
            if (idParam != null) {
                // Obtener un curso específico
                try {
                    int id = Integer.parseInt(idParam);
                    Curso curso = obtenerCursoPorId(id);
                    
                    if (curso != null) {
                        JSONObject jsonCurso = new JSONObject();
                        jsonCurso.put("id_curso", curso.getId());
                        jsonCurso.put("nombre", curso.getNombre());
                        jsonCurso.put("codigo", curso.getCodigo());
                        jsonCurso.put("descripcion", curso.getDescripcion());
                        jsonCurso.put("idProfesor", curso.getIdProfesor());
                        jsonCurso.put("profesor_nombre", curso.getNombreProfesor());
                        
                        out.print(jsonCurso.toString());
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        JSONObject error = new JSONObject();
                        error.put("error", "Curso no encontrado");
                        out.print(error.toString());
                    }
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JSONObject error = new JSONObject();
                    error.put("error", "ID de curso inválido");
                    out.print(error.toString());
                }
            } else {
                // Obtener todos los cursos
                List<Curso> cursos = obtenerTodosCursos();
                JSONArray jsonArray = new JSONArray();
                
                for (Curso curso : cursos) {
                    JSONObject jsonCurso = new JSONObject();
                    jsonCurso.put("id_curso", curso.getId());
                    jsonCurso.put("nombre", curso.getNombre());
                    jsonCurso.put("codigo", curso.getCodigo());
                    jsonCurso.put("descripcion", curso.getDescripcion());
                    jsonCurso.put("idProfesor", curso.getIdProfesor());
                    jsonCurso.put("profesor_nombre", curso.getNombreProfesor());
                    jsonCurso.put("cantidadEstudiantes", curso.getCantidadEstudiantes());
                    
                    jsonArray.put(jsonCurso);
                }
                
                out.print(jsonArray.toString());
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject error = new JSONObject();
            error.put("error", "Error interno del servidor: " + e.getMessage());
            response.getWriter().print(error.toString());
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
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // Crear un nuevo curso
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = request.getReader().readLine()) != null) {
                    sb.append(line);
                }
                
                JSONObject jsonRequest = new JSONObject(sb.toString());
                
                // Crear objeto Curso
                Curso curso = new Curso();
                curso.setNombre(jsonRequest.getString("nombre"));
                curso.setCodigo(jsonRequest.getString("codigo"));
                curso.setDescripcion(jsonRequest.getString("descripcion"));
                curso.setIdProfesor(jsonRequest.getInt("id_profesor"));
                
                // Guardar el curso
                boolean exito = crearCurso(curso);
                
                if (exito) {
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put("mensaje", "Curso creado exitosamente");
                    response.getWriter().print(jsonResponse.toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    JSONObject jsonError = new JSONObject();
                    jsonError.put("error", "Error al crear el curso");
                    response.getWriter().print(jsonError.toString());
                }
            } else if (pathInfo.equals("/estudiantes")) {
                // Asignar estudiantes a un curso
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = request.getReader().readLine()) != null) {
                    sb.append(line);
                }
                
                JSONObject jsonRequest = new JSONObject(sb.toString());
                
                int idCurso = jsonRequest.getInt("id_curso");
                JSONArray estudiantesArray = jsonRequest.getJSONArray("estudiantes");
                
                List<Integer> estudiantesIds = new ArrayList<>();
                for (int i = 0; i < estudiantesArray.length(); i++) {
                    estudiantesIds.add(estudiantesArray.getInt(i));
                }
                
                boolean exito = asignarEstudiantesACurso(idCurso, estudiantesIds);
                
                if (exito) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().print(new JSONObject().put("mensaje", "Estudiantes asignados exitosamente").toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().print(new JSONObject().put("error", "Error al asignar estudiantes").toString());
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonError = new JSONObject();
            jsonError.put("error", e.getMessage());
            response.getWriter().print(jsonError.toString());
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
        
        try {
            // Obtener el ID del curso de los parámetros de la URL
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JSONObject jsonError = new JSONObject();
                jsonError.put("error", "ID de curso no proporcionado");
                response.getWriter().print(jsonError.toString());
                return;
            }
            
            // Leer el cuerpo de la solicitud
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            
            JSONObject jsonRequest = new JSONObject(sb.toString());
            
            // Crear objeto Curso
            Curso curso = new Curso();
            curso.setId(Integer.parseInt(idParam));
            curso.setNombre(jsonRequest.getString("nombre"));
            curso.setCodigo(jsonRequest.getString("codigo"));
            curso.setDescripcion(jsonRequest.getString("descripcion"));
            curso.setIdProfesor(jsonRequest.getInt("id_profesor"));
            
            // Actualizar el curso
            boolean exito = actualizarCurso(curso);
            
            if (exito) {
                response.setStatus(HttpServletResponse.SC_OK);
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("mensaje", "Curso actualizado exitosamente");
                response.getWriter().print(jsonResponse.toString());
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                JSONObject jsonError = new JSONObject();
                jsonError.put("error", "Error al actualizar el curso");
                response.getWriter().print(jsonError.toString());
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonError = new JSONObject();
            jsonError.put("error", e.getMessage());
            response.getWriter().print(jsonError.toString());
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
        
        try {
            String idParam = request.getParameter("id");
            
            if (idParam != null) {
                int id = Integer.parseInt(idParam);
                boolean exito = eliminarCurso(id);
                
                if (exito) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().print(new JSONObject().put("mensaje", "Curso eliminado exitosamente").toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().print(new JSONObject().put("error", "Error al eliminar el curso").toString());
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print(new JSONObject().put("error", "ID de curso no proporcionado").toString());
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(new JSONObject().put("error", e.getMessage()).toString());
        }
    }
    
    // Métodos auxiliares para interactuar con la base de datos
    
    private final CursoDAO cursoDAO;
    
    public CursosController() {
        this.cursoDAO = new CursoDAO();
    }
    
    private Curso obtenerCursoPorId(int id) {
        return cursoDAO.read(id);
    }
    
    private List<Curso> obtenerTodosCursos() {
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
    
    private boolean actualizarCurso(Curso curso) {
        return cursoDAO.update(curso);
    }
    
    private boolean eliminarCurso(int id) {
        return cursoDAO.delete(id);
    }
    
    private boolean asignarEstudiantesACurso(int idCurso, List<Integer> estudiantesIds) {
        return cursoDAO.asignarEstudiantes(idCurso, estudiantesIds);
    }
}