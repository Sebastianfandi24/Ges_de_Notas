package Controllers;

import DAOs.CursoDAO;
import DAOs.TareaDAO;
import Models.Curso;
import Models.Tarea;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import Models.Conexion;

@WebServlet(name = "EstudianteControllerServlet", urlPatterns = {"/estudiante/*"})
public class EstudianteControllerServlet extends HttpServlet {
    
    private final CursoDAO cursoDAO;
    private final TareaDAO tareaDAO;
    private final Gson gson;
    private final Conexion conexion;
    
    public EstudianteControllerServlet() {
        this.cursoDAO = new CursoDAO();
        this.tareaDAO = new TareaDAO();
        this.gson = new Gson();
        this.conexion = new Conexion();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        System.out.println("[EstudianteControllerServlet] Recibida petición GET - Path: " + pathInfo);
        
        try (PrintWriter out = response.getWriter()) {
            if (pathInfo == null || pathInfo.equals("/")) {
                System.out.println("[EstudianteControllerServlet] Error - Path inválido");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            String[] splits = pathInfo.split("/");
            String resource = splits[1];
            System.out.println("[EstudianteControllerServlet] Recurso solicitado: " + resource);
            
            switch (resource) {
                case "cursos":
                    if (splits.length > 2 && splits[2].equals("mis-cursos")) {
                        int idEstudiante = Integer.parseInt(request.getParameter("id_estudiante"));
                        System.out.println("[EstudianteControllerServlet] Consultando cursos del estudiante ID: " + idEstudiante);
                        List<Map<String, Object>> cursos = getMisCursos(idEstudiante);
                        out.print(gson.toJson(cursos));
                    } else {
                        System.out.println("[EstudianteControllerServlet] Error - Petición de cursos inválida");
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }
                    break;
                    
                case "tareas":
                    if (splits.length > 2) {
                        if (splits[2].equals("mis-tareas")) {
                            int idEstudiante = Integer.parseInt(request.getParameter("id_estudiante"));
                            System.out.println("[EstudianteControllerServlet] Consultando tareas del estudiante ID: " + idEstudiante);
                            List<Map<String, Object>> tareas = getMisTareas(idEstudiante);
                            out.print(gson.toJson(tareas));
                        } else if (splits[2].equals("notas")) {
                            int idEstudiante = Integer.parseInt(request.getParameter("id_estudiante"));
                            System.out.println("[EstudianteControllerServlet] Consultando notas del estudiante ID: " + idEstudiante);
                            List<Map<String, Object>> notas = getMisNotas(idEstudiante);
                            out.print(gson.toJson(notas));
                        } else {
                            System.out.println("[EstudianteControllerServlet] Error - Petición de tareas inválida");
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                        }
                    } else {
                        System.out.println("[EstudianteControllerServlet] Error - Petición de tareas sin especificar");
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }
                    break;
                    
                default:
                    System.out.println("[EstudianteControllerServlet] Error - Recurso no encontrado: " + resource);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("[EstudianteControllerServlet] Error - ID inválido: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    private List<Map<String, Object>> getMisCursos(int idEstudiante) {
        System.out.println("[EstudianteControllerServlet] Iniciando consulta de cursos para estudiante ID: " + idEstudiante);
        List<Map<String, Object>> cursos = new ArrayList<>();
        String sql = "SELECT c.*, u.nombre as profesor_nombre FROM CURSO c "
                + "INNER JOIN CURSO_ESTUDIANTE ce ON c.id_curso = ce.id_curso "
                + "LEFT JOIN PROFESOR p ON c.idProfesor = p.id_profesor "
                + "LEFT JOIN USUARIO u ON p.idUsuario = u.id_usu "
                + "WHERE ce.id_estudiante = ?";
        
        try (Connection conn = conexion.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idEstudiante);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> curso = new HashMap<>();
                curso.put("id_curso", rs.getInt("id_curso"));
                curso.put("nombre", rs.getString("nombre"));
                curso.put("codigo", rs.getString("codigo"));
                curso.put("descripcion", rs.getString("descripcion"));
                curso.put("profesor_nombre", rs.getString("profesor_nombre"));
                cursos.add(curso);
            }
            
        } catch (SQLException e) {
            System.out.println("[EstudianteControllerServlet] Error al obtener cursos del estudiante - " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("[EstudianteControllerServlet] Cursos encontrados: " + cursos.size());
        return cursos;
    }
    
    private List<Map<String, Object>> getMisTareas(int idEstudiante) {
        System.out.println("[EstudianteControllerServlet] Iniciando consulta de tareas para estudiante ID: " + idEstudiante);
        List<Map<String, Object>> tareas = new ArrayList<>();
        String sql = "SELECT t.*, c.nombre as curso_nombre, nt.nota, nt.comentario FROM TAREA t "
                + "INNER JOIN CURSO c ON t.id_curso = c.id_curso "
                + "INNER JOIN CURSO_ESTUDIANTE ce ON c.id_curso = ce.id_curso "
                + "LEFT JOIN NOTA_TAREA nt ON t.id_tarea = nt.id_tarea AND nt.id_estudiante = ce.id_estudiante "
                + "WHERE ce.id_estudiante = ? "
                + "ORDER BY t.fecha_entrega ASC";
        
        try (Connection conn = conexion.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idEstudiante);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> tarea = new HashMap<>();
                tarea.put("id_tarea", rs.getInt("id_tarea"));
                tarea.put("titulo", rs.getString("titulo"));
                tarea.put("descripcion", rs.getString("descripcion"));
                tarea.put("fecha_asignacion", rs.getDate("fecha_asignacion"));
                tarea.put("fecha_entrega", rs.getDate("fecha_entrega"));
                tarea.put("curso_nombre", rs.getString("curso_nombre"));
                tarea.put("nota", rs.getObject("nota"));
                tarea.put("comentario", rs.getString("comentario"));
                tareas.add(tarea);
            }
            
        } catch (SQLException e) {
            System.out.println("[EstudianteControllerServlet] Error al obtener tareas del estudiante - " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("[EstudianteControllerServlet] Tareas encontradas: " + tareas.size());
        return tareas;
    }
    
    private List<Map<String, Object>> getMisNotas(int idEstudiante) {
        System.out.println("[EstudianteControllerServlet] Iniciando consulta de notas para estudiante ID: " + idEstudiante);
        List<Map<String, Object>> notas = new ArrayList<>();
        String sql = "SELECT nt.*, t.titulo as tarea_titulo, c.nombre as curso_nombre FROM NOTA_TAREA nt "
                + "INNER JOIN TAREA t ON nt.id_tarea = t.id_tarea "
                + "INNER JOIN CURSO c ON t.id_curso = c.id_curso "
                + "WHERE nt.id_estudiante = ? "
                + "ORDER BY nt.fecha_evaluacion DESC";
        
        try (Connection conn = conexion.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idEstudiante);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> nota = new HashMap<>();
                nota.put("id_nota", rs.getInt("id_nota"));
                nota.put("id_tarea", rs.getInt("id_tarea"));
                nota.put("tarea_titulo", rs.getString("tarea_titulo"));
                nota.put("curso_nombre", rs.getString("curso_nombre"));
                nota.put("nota", rs.getFloat("nota"));
                nota.put("fecha_evaluacion", rs.getDate("fecha_evaluacion"));
                nota.put("comentario", rs.getString("comentario"));
                notas.add(nota);
            }
            
        } catch (SQLException e) {
            System.out.println("[EstudianteControllerServlet] Error al obtener notas del estudiante - " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("[EstudianteControllerServlet] Notas encontradas: " + notas.size());
        return notas;
    }
}