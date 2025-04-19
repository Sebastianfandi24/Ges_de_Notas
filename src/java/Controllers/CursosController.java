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
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // Obtener todos los cursos o un curso específico por ID
                String idParam = request.getParameter("id");
                
                if (idParam != null) {
                    // Obtener un curso específico
                    int id = Integer.parseInt(idParam);
                    Curso curso = obtenerCursoPorId(id);
                    
                    if (curso != null) {
                        JSONObject jsonCurso = new JSONObject();
                        jsonCurso.put("id_curso", curso.getId());
                        jsonCurso.put("nombre", curso.getNombre());
                        jsonCurso.put("codigo", curso.getCodigo());
                        jsonCurso.put("descripcion", curso.getDescripcion());
                        jsonCurso.put("id_profesor", curso.getIdProfesor());
                        jsonCurso.put("nombre_profesor", curso.getNombreProfesor());
                        jsonCurso.put("profesor_nombre", curso.getNombreProfesor());
                        
                        out.print(jsonCurso.toString());
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print(new JSONObject().put("error", "Curso no encontrado").toString());
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
                        jsonCurso.put("id_profesor", curso.getIdProfesor());
                        jsonCurso.put("profesor_nombre", curso.getNombreProfesor()); 
                        jsonCurso.put("cantidadEstudiantes", curso.getCantidadEstudiantes());
                        
                        jsonArray.put(jsonCurso);
                    }
                    
                    out.print(jsonArray.toString());
                }
            } else if (pathInfo.startsWith("/profesor/")) {
                // Obtener cursos por profesor
                String idProfesorStr = pathInfo.substring("/profesor/".length());
                int idProfesor = Integer.parseInt(idProfesorStr);
                
                List<Curso> cursos = obtenerCursosPorProfesor(idProfesor);
                JSONArray jsonArray = new JSONArray();
                
                for (Curso curso : cursos) {
                    JSONObject jsonCurso = new JSONObject();
                    jsonCurso.put("id_curso", curso.getId());
                    jsonCurso.put("nombre", curso.getNombre());
                    jsonCurso.put("codigo", curso.getCodigo());
                    jsonCurso.put("descripcion", curso.getDescripcion());
                    jsonCurso.put("id_profesor", curso.getIdProfesor());
                    jsonCurso.put("nombre_profesor", curso.getNombreProfesor());
                    jsonCurso.put("cantidadEstudiantes", curso.getCantidadEstudiantes());
                    
                    jsonArray.put(jsonCurso);
                }
                
                out.print(jsonArray.toString());
            } else if (pathInfo.startsWith("/estudiante/")) {
                // Obtener cursos por estudiante
                String idEstudianteStr = pathInfo.substring("/estudiante/".length());
                int idEstudiante = Integer.parseInt(idEstudianteStr);
                
                List<Curso> cursos = obtenerCursosPorEstudiante(idEstudiante);
                JSONArray jsonArray = new JSONArray();
                
                for (Curso curso : cursos) {
                    JSONObject jsonCurso = new JSONObject();
                    jsonCurso.put("id_curso", curso.getId());
                    jsonCurso.put("nombre", curso.getNombre());
                    jsonCurso.put("codigo", curso.getCodigo());
                    jsonCurso.put("descripcion", curso.getDescripcion());
                    jsonCurso.put("id_profesor", curso.getIdProfesor());
                    jsonCurso.put("nombre_profesor", curso.getNombreProfesor());
                    jsonCurso.put("cantidadEstudiantes", curso.getCantidadEstudiantes());
                    
                    jsonArray.put(jsonCurso);
                }
                
                out.print(jsonArray.toString());
            } else if (pathInfo.startsWith("/estudiantes")) {
                // Obtener estudiantes por curso
                String idParam = request.getParameter("id");
                
                if (idParam != null) {
                    int idCurso = Integer.parseInt(idParam);
                    List<Estudiante> estudiantes = obtenerEstudiantesPorCurso(idCurso);
                    JSONArray jsonArray = new JSONArray();
                    
                    for (Estudiante estudiante : estudiantes) {
                        JSONObject jsonEstudiante = new JSONObject();
                        jsonEstudiante.put("id_estudiante", estudiante.getId());
                        jsonEstudiante.put("nombre", estudiante.getNombre());
                        jsonEstudiante.put("telefono", estudiante.getTelefono());
                        jsonEstudiante.put("correo", estudiante.getCorreo());
                        jsonArray.put(jsonEstudiante);
                    }
                    
                    out.print(jsonArray.toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(new JSONObject().put("error", "ID de curso no proporcionado").toString());
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(new JSONObject().put("error", e.getMessage()).toString());
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
                curso.setDescripcion(jsonRequest.getString("descripcion"));
                curso.setIdProfesor(jsonRequest.getInt("id_profesor"));
                
                // Guardar el curso
                boolean exito = crearCurso(curso);
                
                if (exito) {
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    response.getWriter().print(new JSONObject().put("mensaje", "Curso creado exitosamente").toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().print(new JSONObject().put("error", "Error al crear el curso").toString());
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
            response.getWriter().print(new JSONObject().put("error", e.getMessage()).toString());
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
            // Leer el cuerpo de la solicitud
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            
            JSONObject jsonRequest = new JSONObject(sb.toString());
            
            // Crear objeto Curso
            Curso curso = new Curso();
            curso.setId(jsonRequest.getInt("id_curso"));
            curso.setNombre(jsonRequest.getString("nombre"));
            curso.setDescripcion(jsonRequest.getString("descripcion"));
            curso.setIdProfesor(jsonRequest.getInt("id_profesor"));
            
            // Actualizar el curso
            boolean exito = actualizarCurso(curso);
            
            if (exito) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().print(new JSONObject().put("mensaje", "Curso actualizado exitosamente").toString());
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().print(new JSONObject().put("error", "Error al actualizar el curso").toString());
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(new JSONObject().put("error", e.getMessage()).toString());
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