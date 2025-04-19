package Controllers;

import DAOs.CursoDAO;
import DAOs.TareaDAO;
import Models.Curso;
import Models.Tarea;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ProfesorControllerServlet", urlPatterns = {"/profesor/*"})
public class ProfesorControllerServlet extends HttpServlet {
    
    private final CursoDAO cursoDAO;
    private final TareaDAO tareaDAO;
    private final Gson gson;
    private final SimpleDateFormat dateFormat;
    
    public ProfesorControllerServlet() {
        this.cursoDAO = new CursoDAO();
        this.tareaDAO = new TareaDAO();
        this.gson = new Gson();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        System.out.println("[ProfesorControllerServlet] Recibida petición GET - Path: " + pathInfo);
        
        try (PrintWriter out = response.getWriter()) {
            if (pathInfo == null || pathInfo.equals("/")) {
                System.out.println("[ProfesorControllerServlet] Error - Path inválido");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            String[] splits = pathInfo.split("/");
            String resource = splits[1];
            System.out.println("[ProfesorControllerServlet] Recurso solicitado: " + resource);
            
            switch (resource) {
                case "cursos":
                    if (splits.length > 2) {
                        if (splits[2].equals("mis-cursos")) {
                            int idProfesor = Integer.parseInt(request.getParameter("id_profesor"));
                            System.out.println("[ProfesorControllerServlet] Consultando cursos del profesor ID: " + idProfesor);
                            List<Curso> cursos = cursoDAO.getCursosPorProfesor(idProfesor);
                            out.print(gson.toJson(cursos));
                        } else {
                            int id = Integer.parseInt(splits[2]);
                            System.out.println("[ProfesorControllerServlet] Consultando curso ID: " + id);
                            Curso curso = cursoDAO.read(id);
                            out.print(gson.toJson(curso));
                        }
                    } else {
                        System.out.println("[ProfesorControllerServlet] Error - Petición de cursos inválida");
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }
                    break;
                    
                case "tareas":
                    if (splits.length > 2) {
                        if (splits[2].equals("por-curso")) {
                            int idCurso = Integer.parseInt(request.getParameter("id_curso"));
                            System.out.println("[ProfesorControllerServlet] Consultando tareas del curso ID: " + idCurso);
                            List<Tarea> tareas = tareaDAO.getTareasPorCurso(idCurso);
                            out.print(gson.toJson(tareas));
                        } else {
                            int id = Integer.parseInt(splits[2]);
                            System.out.println("[ProfesorControllerServlet] Consultando tarea ID: " + id);
                            Tarea tarea = tareaDAO.read(id);
                            out.print(gson.toJson(tarea));
                        }
                    } else {
                        System.out.println("[ProfesorControllerServlet] Error - Petición de tareas inválida");
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }
                    break;
                    
                default:
                    System.out.println("[ProfesorControllerServlet] Error - Recurso no encontrado: " + resource);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("[ProfesorControllerServlet] Error - ID inválido: " + e.getMessage());
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
                System.out.println("[ProfesorControllerServlet] Error - Path inválido en POST");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            String[] splits = pathInfo.split("/");
            String resource = splits[1];
            System.out.println("[ProfesorControllerServlet] Recurso POST solicitado: " + resource);
            
            switch (resource) {
                case "tareas":
                    if (splits.length > 2 && splits[2].equals("calificar")) {
                        int idTarea = Integer.parseInt(request.getParameter("id_tarea"));
                        int idEstudiante = Integer.parseInt(request.getParameter("id_estudiante"));
                        float nota = Float.parseFloat(request.getParameter("nota"));
                        String comentario = request.getParameter("comentario");
                        
                        System.out.println("[ProfesorControllerServlet] Calificando tarea ID: " + idTarea + " para estudiante ID: " + idEstudiante + " con nota: " + nota);
                        
                        boolean success = tareaDAO.asignarNota(idTarea, idEstudiante, nota, comentario);
                        if (success) {
                            System.out.println("[ProfesorControllerServlet] Nota asignada exitosamente");
                            out.print(gson.toJson("Nota asignada exitosamente"));
                        } else {
                            System.out.println("[ProfesorControllerServlet] Error al asignar nota");
                            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al asignar nota");
                        }
                    } else {
                        System.out.println("[ProfesorControllerServlet] Creando nueva tarea");
                        Tarea tarea = new Tarea();
                        tarea.setTitulo(request.getParameter("titulo"));
                        tarea.setDescripcion(request.getParameter("descripcion"));
                        tarea.setId_curso(Integer.parseInt(request.getParameter("id_curso")));
                        
                        try {
                            String fechaAsignacionStr = request.getParameter("fecha_asignacion");
                            String fechaEntregaStr = request.getParameter("fecha_entrega");
                            
                            if (fechaAsignacionStr != null && !fechaAsignacionStr.isEmpty()) {
                                tarea.setFecha_asignacion(dateFormat.parse(fechaAsignacionStr));
                            }
                            if (fechaEntregaStr != null && !fechaEntregaStr.isEmpty()) {
                                tarea.setFecha_entrega(dateFormat.parse(fechaEntregaStr));
                            }
                            
                            boolean success = tareaDAO.create(tarea);
                            if (success) {
                                System.out.println("[ProfesorControllerServlet] Tarea creada exitosamente");
                                response.setStatus(HttpServletResponse.SC_CREATED);
                                out.print(gson.toJson("Tarea creada exitosamente"));
                            } else {
                                System.out.println("[ProfesorControllerServlet] Error al crear tarea");
                                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al crear tarea");
                            }
                        } catch (ParseException e) {
                            System.out.println("[ProfesorControllerServlet] Error - Formato de fecha inválido: " + e.getMessage());
                            e.printStackTrace();
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato de fecha inválido");
                        }
                    }
                    break;
                    
                default:
                    System.out.println("[ProfesorControllerServlet] Error - Recurso no encontrado en POST: " + resource);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("[ProfesorControllerServlet] Error - Parámetro numérico inválido: " + e.getMessage());
            e.printStackTrace();
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
                case "tareas":
                    // PUT /profesor/tareas/{id}
                    Tarea tarea = tareaDAO.read(id);
                    if (tarea != null) {
                        tarea.setTitulo(request.getParameter("titulo"));
                        tarea.setDescripcion(request.getParameter("descripcion"));
                        
                        try {
                            String fechaAsignacionStr = request.getParameter("fecha_asignacion");
                            String fechaEntregaStr = request.getParameter("fecha_entrega");
                            
                            if (fechaAsignacionStr != null && !fechaAsignacionStr.isEmpty()) {
                                tarea.setFecha_asignacion(dateFormat.parse(fechaAsignacionStr));
                            }
                            if (fechaEntregaStr != null && !fechaEntregaStr.isEmpty()) {
                                tarea.setFecha_entrega(dateFormat.parse(fechaEntregaStr));
                            }
                            
                            boolean success = tareaDAO.update(tarea);
                            if (success) {
                                out.print(gson.toJson("Tarea actualizada exitosamente"));
                            } else {
                                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al actualizar tarea");
                            }
                        } catch (ParseException e) {
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato de fecha inválido");
                        }
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Tarea no encontrada");
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
            
            switch (resource) {
                case "tareas":
                    // DELETE /profesor/tareas/{id}
                    boolean success = tareaDAO.delete(id);
                    if (success) {
                        out.print(gson.toJson("Tarea eliminada exitosamente"));
                    } else {
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al eliminar tarea");
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