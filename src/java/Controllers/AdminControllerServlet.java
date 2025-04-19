package Controllers;

import DAOs.ProfesorDAO;
import DAOs.EstudianteDAO;
import DAOs.CursoDAO;
import Models.Profesor;
import Models.Estudiante;
import Models.Curso;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "AdminControllerServlet", urlPatterns = {"/api/admin/*"})
public class AdminControllerServlet extends HttpServlet {
    
    private final ProfesorDAO profesorDAO;
    private final EstudianteDAO estudianteDAO;
    private final CursoDAO cursoDAO;
    private final Gson gson;
    private final SimpleDateFormat dateFormat;
    
    public AdminControllerServlet() {
        this.profesorDAO = new ProfesorDAO();
        this.estudianteDAO = new EstudianteDAO();
        this.cursoDAO = new CursoDAO();
        this.gson = new Gson();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        System.out.println("[AdminControllerServlet] Recibida petición GET - Path: " + pathInfo);
        
        try (PrintWriter out = response.getWriter()) {
            if (pathInfo == null || pathInfo.equals("/")) {
                System.out.println("[AdminControllerServlet] Error - Path inválido");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            String[] splits = pathInfo.split("/");
            String resource = splits[1];
            System.out.println("[AdminControllerServlet] Recurso solicitado: " + resource);
            
            switch (resource) {
                case "profesores":
                    if (splits.length > 2) {
                        int id = Integer.parseInt(splits[2]);
                        System.out.println("[AdminControllerServlet] Consultando profesor ID: " + id);
                        Profesor profesor = profesorDAO.read(id);
                        out.print(gson.toJson(profesor));
                    } else {
                        System.out.println("[AdminControllerServlet] Consultando lista de profesores");
                        List<Profesor> profesores = profesorDAO.readAll();
                        out.print(gson.toJson(profesores));
                    }
                    break;
                    
                case "estudiantes":
                    if (splits.length > 2) {
                        int id = Integer.parseInt(splits[2]);
                        System.out.println("[AdminControllerServlet] Consultando estudiante ID: " + id);
                        Estudiante estudiante = estudianteDAO.read(id);
                        out.print(gson.toJson(estudiante));
                    } else {
                        System.out.println("[AdminControllerServlet] Consultando lista de estudiantes");
                        List<Estudiante> estudiantes = estudianteDAO.readAll();
                        out.print(gson.toJson(estudiantes));
                    }
                    break;
                    
                case "cursos":
                    if (splits.length > 2) {
                        int id = Integer.parseInt(splits[2]);
                        System.out.println("[AdminControllerServlet] Consultando curso ID: " + id);
                        Curso curso = cursoDAO.read(id);
                        out.print(gson.toJson(curso));
                    } else {
                        System.out.println("[AdminControllerServlet] Consultando lista de cursos");
                        List<Curso> cursos = cursoDAO.readAll();
                        out.print(gson.toJson(cursos));
                    }
                    break;
                    
                default:
                    System.out.println("[AdminControllerServlet] Error - Recurso no encontrado: " + resource);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("[AdminControllerServlet] Error - ID inválido: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            String[] splits = pathInfo.split("/");
            String resource = splits[1];
            
            switch (resource) {
                case "profesores":
                    // POST /admin/profesores
                    Profesor profesor = new Profesor();
                    profesor.setNombre(request.getParameter("nombre"));
                    profesor.setCorreo(request.getParameter("correo"));
                    profesor.setIdRol(2); // Rol de profesor
                    profesor.setDireccion(request.getParameter("direccion"));
                    profesor.setTelefono(request.getParameter("telefono"));
                    profesor.setGradoAcademico(request.getParameter("grado_academico"));
                    profesor.setEspecializacion(request.getParameter("especializacion"));
                    profesor.setEstado(request.getParameter("estado"));
                    
                    try {
                        String fechaNacStr = request.getParameter("fecha_nacimiento");
                        String fechaContratacionStr = request.getParameter("fecha_contratacion");
                        
                        if (fechaNacStr != null && !fechaNacStr.isEmpty()) {
                            profesor.setFechaNacimiento(dateFormat.parse(fechaNacStr));
                        }
                        if (fechaContratacionStr != null && !fechaContratacionStr.isEmpty()) {
                            profesor.setFechaContratacion(dateFormat.parse(fechaContratacionStr));
                        }
                        
                        boolean success = profesorDAO.create(profesor);
                        if (success) {
                            response.setStatus(HttpServletResponse.SC_CREATED);
                            out.print(gson.toJson("Profesor creado exitosamente"));
                        } else {
                            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al crear profesor");
                        }
                    } catch (ParseException e) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato de fecha inválido");
                    }
                    break;
                    
                case "estudiantes":
                    // POST /admin/estudiantes
                    Estudiante estudiante = new Estudiante();
                    estudiante.setNombre(request.getParameter("nombre"));
                    estudiante.setCorreo(request.getParameter("correo"));
                    estudiante.setIdRol(1); // Rol de estudiante
                    estudiante.setDireccion(request.getParameter("direccion"));
                    estudiante.setTelefono(request.getParameter("telefono"));
                    estudiante.setNumeroIdentificacion(request.getParameter("numero_identificacion"));
                    estudiante.setEstado(request.getParameter("estado"));
                    
                    try {
                        String fechaNacStr = request.getParameter("fecha_nacimiento");
                        String promedioStr = request.getParameter("promedio_academico");
                        
                        if (fechaNacStr != null && !fechaNacStr.isEmpty()) {
                            estudiante.setFechaNacimiento(dateFormat.parse(fechaNacStr));
                        }
                        if (promedioStr != null && !promedioStr.isEmpty()) {
                            estudiante.setPromedioAcademico(Float.parseFloat(promedioStr));
                        }
                        
                        boolean success = estudianteDAO.create(estudiante);
                        if (success) {
                            response.setStatus(HttpServletResponse.SC_CREATED);
                            out.print(gson.toJson("Estudiante creado exitosamente"));
                        } else {
                            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al crear estudiante");
                        }
                    } catch (ParseException e) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato de fecha inválido");
                    } catch (NumberFormatException e) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato de promedio inválido");
                    }
                    break;
                    
                case "cursos":
                    if (splits.length > 2 && splits[2].equals("asignar-estudiantes")) {
                        // POST /admin/cursos/asignar-estudiantes
                        int idCurso = Integer.parseInt(request.getParameter("id_curso"));
                        String[] idEstudiantesStr = request.getParameterValues("id_estudiantes");
                        List<Integer> idEstudiantes = Arrays.stream(idEstudiantesStr)
                                .map(Integer::parseInt)
                                .toList();
                        
                        boolean success = cursoDAO.asignarEstudiantes(idCurso, idEstudiantes);
                        if (success) {
                            out.print(gson.toJson("Estudiantes asignados exitosamente"));
                        } else {
                            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al asignar estudiantes");
                        }
                    } else {
                        // POST /admin/cursos
                        Curso curso = new Curso();
                        curso.setNombre(request.getParameter("nombre"));
                        curso.setCodigo(request.getParameter("codigo"));
                        curso.setDescripcion(request.getParameter("descripcion"));
                        curso.setIdProfesor(Integer.parseInt(request.getParameter("idProfesor")));
                        
                        boolean success = cursoDAO.create(curso);
                        if (success) {
                            response.setStatus(HttpServletResponse.SC_CREATED);
                            out.print(gson.toJson("Curso creado exitosamente"));
                        } else {
                            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al crear curso");
                        }
                    }
                    break;
                    
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parámetro numérico inválido");
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            String[] splits = pathInfo.split("/");
            if (splits.length < 3) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            String resource = splits[1];
            int id = Integer.parseInt(splits[2]);
            
            switch (resource) {
                case "profesores":
                    // PUT /admin/profesores/{id}
                    Profesor profesor = profesorDAO.read(id);
                    if (profesor != null) {
                        profesor.setNombre(request.getParameter("nombre"));
                        profesor.setCorreo(request.getParameter("correo"));
                        profesor.setDireccion(request.getParameter("direccion"));
                        profesor.setTelefono(request.getParameter("telefono"));
                        profesor.setGrado_academico(request.getParameter("grado_academico"));
                        profesor.setEspecializacion(request.getParameter("especializacion"));
                        profesor.setEstado(request.getParameter("estado"));
                        
                        try {
                            String fechaNacStr = request.getParameter("fecha_nacimiento");
                            String fechaContratacionStr = request.getParameter("fecha_contratacion");
                            
                            if (fechaNacStr != null && !fechaNacStr.isEmpty()) {
                                profesor.setFecha_nacimiento(dateFormat.parse(fechaNacStr));
                            }
                            if (fechaContratacionStr != null && !fechaContratacionStr.isEmpty()) {
                                profesor.setFecha_contratacion(dateFormat.parse(fechaContratacionStr));
                            }
                            
                            boolean success = profesorDAO.update(profesor);
                            if (success) {
                                out.print(gson.toJson("Profesor actualizado exitosamente"));
                            } else {
                                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al actualizar profesor");
                            }
                        } catch (ParseException e) {
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato de fecha inválido");
                        }
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Profesor no encontrado");
                    }
                    break;
                    
                case "estudiantes":
                    // PUT /admin/estudiantes/{id}
                    Estudiante estudiante = estudianteDAO.read(id);
                    if (estudiante != null) {
                        estudiante.setNombre(request.getParameter("nombre"));
                        estudiante.setCorreo(request.getParameter("correo"));
                        estudiante.setDireccion(request.getParameter("direccion"));
                        estudiante.setTelefono(request.getParameter("telefono"));
                        estudiante.setNumeroIdentificacion(request.getParameter("numero_identificacion"));
                        estudiante.setEstado(request.getParameter("estado"));
                        
                        try {
                            String fechaNacStr = request.getParameter("fecha_nacimiento");
                            String promedioStr = request.getParameter("promedio_academico");
                            
                            if (fechaNacStr != null && !fechaNacStr.isEmpty()) {
                                estudiante.setFechaNacimiento(dateFormat.parse(fechaNacStr));
                            }
                            if (promedioStr != null && !promedioStr.isEmpty()) {
                                estudiante.setPromedioAcademico(Float.parseFloat(promedioStr));
                            }
                            
                            boolean success = estudianteDAO.update(estudiante);
                            if (success) {
                                out.print(gson.toJson("Estudiante actualizado exitosamente"));
                            } else {
                                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al actualizar estudiante");
                            }
                        } catch (ParseException e) {
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato de fecha inválido");
                        } catch (NumberFormatException e) {
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato de promedio inválido");
                        }
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Estudiante no encontrado");
                    }
                    break;
                    
                case "cursos":
                    // PUT /admin/cursos/{id}
                    Curso curso = cursoDAO.read(id);
                    if (curso != null) {
                        curso.setNombre(request.getParameter("nombre"));
                        curso.setCodigo(request.getParameter("codigo"));
                        curso.setDescripcion(request.getParameter("descripcion"));
                        curso.setIdProfesor(Integer.parseInt(request.getParameter("idProfesor")));
                        
                        boolean success = cursoDAO.update(curso);
                        if (success) {
                            out.print(gson.toJson("Curso actualizado exitosamente"));
                        } else {
                            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al actualizar curso");
                        }
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Curso no encontrado");
                    }
                    break;
                    
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            String[] splits = pathInfo.split("/");
            if (splits.length < 3) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            String resource = splits[1];
            int id = Integer.parseInt(splits[2]);
            boolean success;
            
            switch (resource) {
                case "profesores":
                    // DELETE /admin/profesores/{id}
                    success = profesorDAO.delete(id);
                    if (success) {
                        out.print(gson.toJson("Profesor eliminado exitosamente"));
                    } else {
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al eliminar profesor");
                    }
                    break;
                    
                case "estudiantes":
                    // DELETE /admin/estudiantes/{id}
                    success = estudianteDAO.delete(id);
                    if (success) {
                        out.print(gson.toJson("Estudiante eliminado exitosamente"));
                    } else {
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al eliminar estudiante");
                    }
                    break;
                    
                case "cursos":
                    if (splits.length > 3 && splits[2].equals("estudiantes")) {
                        // DELETE /admin/cursos/estudiantes/{id_estudiante}
                        int idCurso = Integer.parseInt(request.getParameter("id_curso"));
                        int idEstudiante = Integer.parseInt(splits[3]);
                        success = cursoDAO.removerEstudiante(idCurso, idEstudiante);
                        if (success) {
                            out.print(gson.toJson("Estudiante removido del curso exitosamente"));
                        } else {
                            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al remover estudiante del curso");
                        }
                    } else {
                        // DELETE /admin/cursos/{id}
                        success = cursoDAO.delete(id);
                        if (success) {
                            out.print(gson.toJson("Curso eliminado exitosamente"));
                        } else {
                            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al eliminar curso");
                        }
                    }
                    break;
                    
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
}