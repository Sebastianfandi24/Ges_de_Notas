package Controllers;

import DAOs.CursoDAO;
import DAOs.TareaDAO;
import DAOs.NotaDAO;
import DAOs.EstudianteDAO;
import Models.Curso;
import Models.Tarea;
import Models.Estudiante;
import Models.Nota;
import org.json.JSONArray;
import org.json.JSONObject;
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
import java.util.logging.Logger;

@WebServlet(name = "ProfesorControllerServlet", urlPatterns = {"/profesor/*"})
public class ProfesorControllerServlet extends HttpServlet {
    
    private CursoDAO cursoDAO;
    private TareaDAO tareaDAO;
    private final SimpleDateFormat dateFormat;
    private static final Logger logger = Logger.getLogger(ProfesorControllerServlet.class.getName());
    
    public ProfesorControllerServlet() {
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }
    
    @Override
    public void init() throws ServletException {
        try {
            this.cursoDAO = new CursoDAO();
            this.tareaDAO = new TareaDAO();
            logger.info("ProfesorControllerServlet: DAOs inicializados correctamente");
        } catch (Exception e) {
            logger.severe("ProfesorControllerServlet: Error al inicializar DAOs: " + e.getMessage());
            throw new ServletException("Error al inicializar DAOs", e);
        }
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
                            JSONArray cArr = new JSONArray();
                            for (Curso cObj : cursos) {
                                JSONObject o = new JSONObject();
                                o.put("id_curso", cObj.getId());
                                o.put("nombre", cObj.getNombre());
                                o.put("codigo", cObj.getCodigo());
                                cArr.put(o);
                            }
                            logger.info("Cursos JSON: " + cArr.toString());
                            out.print(cArr.toString());
                        } else {
                            int id = Integer.parseInt(splits[2]);
                            System.out.println("[ProfesorControllerServlet] Consultando curso ID: " + id);
                            Curso curso = cursoDAO.read(id);
                            JSONObject o = new JSONObject();
                            o.put("id_curso", curso.getId());
                            o.put("nombre", curso.getNombre());
                            o.put("codigo", curso.getCodigo());
                            logger.info("Curso JSON: " + o.toString());
                            out.print(o.toString());
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
                            JSONArray tArr = new JSONArray();
                            for (Tarea tObj : tareas) {
                                JSONObject o = new JSONObject();
                                o.put("id_tarea", tObj.getId());
                                o.put("titulo", tObj.getTitulo());
                                o.put("fecha_entrega", tObj.getFecha_entrega().toString());
                                o.put("id_curso", tObj.getIdCurso());
                                tArr.put(o);
                            }
                            logger.info("Tareas JSON: " + tArr.toString());
                            out.print(tArr.toString());
                        } else {
                            int id = Integer.parseInt(splits[2]);
                            System.out.println("[ProfesorControllerServlet] Consultando tarea ID: " + id);
                            Tarea tarea = tareaDAO.read(id);
                            JSONObject o = new JSONObject();
                            o.put("id_tarea", tarea.getId());
                            o.put("titulo", tarea.getTitulo());
                            o.put("descripcion", tarea.getDescripcion());
                            o.put("fecha_entrega", tarea.getFecha_entrega().toString());
                            o.put("id_curso", tarea.getIdCurso());
                            logger.info("Tarea JSON: " + o.toString());
                            out.print(o.toString());
                        }
                    } else {
                        System.out.println("[ProfesorControllerServlet] Error - Petición de tareas inválida");
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }
                    break;

                case "curso-estudiantes": {
                    try {
                        int idCursoEst = Integer.parseInt(request.getParameter("id_curso"));
                        logger.info("[ProfesorControllerServlet] Consultando estudiantes del curso ID: " + idCursoEst);
                        List<Estudiante> estudiantes = cursoDAO.getEstudiantesPorCurso(idCursoEst);
                        JSONArray eArr = new JSONArray();
                        for (Estudiante est : estudiantes) {
                            JSONObject o = new JSONObject();
                            o.put("id_estudiante", est.getId());
                            o.put("nombre", est.getNombre());
                            eArr.put(o);
                        }
                        logger.info("Estudiantes JSON: " + eArr.toString());
                        out.print(eArr.toString());
                    } catch (Exception ex) {
                        logger.severe("[ProfesorControllerServlet] Error al obtener estudiantes: " + ex.getMessage());
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }
                    break;
                }

                case "notas": {
                    if (splits.length > 2 && "por-tarea".equals(splits[2])) {
                        try {
                            int idTarea = Integer.parseInt(request.getParameter("id_tarea"));
                            logger.info("[ProfesorControllerServlet] Consultando notas de la tarea ID: " + idTarea);
                            List<Nota> notas = new NotaDAO().getNotasPorTarea(idTarea);
                            JSONArray nArr = new JSONArray();
                            for (Nota nt : notas) {
                                JSONObject o = new JSONObject();
                                o.put("id_nota", nt.getIdNota());
                                o.put("id_estudiante", nt.getIdEstudiante());
                                o.put("nota", nt.getNota());
                                o.put("comentario", nt.getComentario());
                                nArr.put(o);
                            }
                            logger.info("Notas JSON: " + nArr.toString());
                            out.print(nArr.toString());
                        } catch (Exception ex) {
                            logger.severe("[ProfesorControllerServlet] Error al obtener notas: " + ex.getMessage());
                            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        }
                    } else {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }
                    break;
                }
                
                case "dashboard": {
                    // Endpoint principal del dashboard
                    if (splits.length == 2) {
                        try {
                            int idProfesor = Integer.parseInt(request.getParameter("id_profesor"));
                            logger.info("[ProfesorControllerServlet] Consultando datos del dashboard para profesor ID: " + idProfesor);
                            
                            // Obtener métricas generales
                            CursoDAO cursoDAO = new CursoDAO();
                            TareaDAO tareaDAO = new TareaDAO();
                            NotaDAO notaDAO = new NotaDAO();
                            
                            // 1. Número de cursos del profesor
                            List<Curso> cursos = cursoDAO.getCursosPorProfesor(idProfesor);
                            int cursosCount = cursos.size();
                            
                            // 2. Total de estudiantes en todos los cursos del profesor
                            int estudiantesCount = 0;
                            for (Curso curso : cursos) {
                                List<Estudiante> estudiantes = cursoDAO.getEstudiantesPorCurso(curso.getId());
                                estudiantesCount += estudiantes.size();
                            }
                            
                            // 3. Tareas pendientes por calificar (tareas sin notas)
                            int tareasPendientes = 0;
                            List<Tarea> todasLasTareas = tareaDAO.getTareasPorProfesor(idProfesor);
                            for (Tarea tarea : todasLasTareas) {
                                List<Nota> notas = notaDAO.getNotasPorTarea(tarea.getId());
                                // Si hay menos notas que estudiantes, hay tareas por calificar
                                List<Estudiante> estudiantesCurso = cursoDAO.getEstudiantesPorCurso(tarea.getIdCurso());
                                if (notas.isEmpty() || notas.size() < estudiantesCurso.size()) {
                                    tareasPendientes++;
                                }
                            }
                            
                            // 4. Promedio general de todos los estudiantes
                            double promedioGeneral = 0.0;
                            int totalEstudiantesConPromedio = 0;
                            
                            for (Curso curso : cursos) {
                                List<Estudiante> estudiantes = cursoDAO.getEstudiantesPorCurso(curso.getId());
                                for (Estudiante est : estudiantes) {
                                    try {
                                        double promedio = new EstudianteDAO().calcularPromedio(est.getId());
                                        if (promedio > 0) {
                                            promedioGeneral += promedio;
                                            totalEstudiantesConPromedio++;
                                        }
                                    } catch (Exception e) {
                                        logger.warning("Error al calcular promedio para estudiante ID: " + est.getId() + " - " + e.getMessage());
                                    }
                                }
                            }
                            
                            // Calcular promedio final
                            if (totalEstudiantesConPromedio > 0) {
                                promedioGeneral /= totalEstudiantesConPromedio;
                            }
                            
                            // Crear respuesta JSON
                            JSONObject dashboardData = new JSONObject();
                            dashboardData.put("cursosCount", cursosCount);
                            dashboardData.put("estudiantesCount", estudiantesCount);
                            dashboardData.put("tareasPendientes", tareasPendientes);
                            dashboardData.put("average", promedioGeneral);
                            
                            logger.info("Dashboard JSON: " + dashboardData.toString());
                            out.print(dashboardData.toString());
                            
                        } catch (Exception ex) {
                            logger.severe("[ProfesorControllerServlet] Error al obtener datos del dashboard: " + ex.getMessage());
                            ex.printStackTrace();
                            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        }
                    } 
                    // Endpoint para cursos del dashboard
                    else if (splits.length > 2 && "cursos".equals(splits[2])) {
                        try {
                            int idProfesor = Integer.parseInt(request.getParameter("id_profesor"));
                            logger.info("[ProfesorControllerServlet] Consultando datos de cursos para dashboard, profesor ID: " + idProfesor);
                            
                            CursoDAO cursoDAO = new CursoDAO();
                            List<Curso> cursos = cursoDAO.getCursosPorProfesor(idProfesor);
                            JSONArray cursosArray = new JSONArray();
                            
                            for (Curso curso : cursos) {
                                JSONObject cursoJson = new JSONObject();
                                // Usamos nombres de propiedades consistentes con lo esperado en el frontend
                                cursoJson.put("id", curso.getId());
                                cursoJson.put("nombre", curso.getNombre());
                                
                                // Obtener estudiantes del curso
                                List<Estudiante> estudiantes = cursoDAO.getEstudiantesPorCurso(curso.getId());
                                cursoJson.put("estudiantes", estudiantes.size());
                                
                                // Calcular progreso del curso (basado en tareas completadas)
                                TareaDAO tareaDAO = new TareaDAO();
                                List<Tarea> tareas = tareaDAO.getTareasPorCurso(curso.getId());
                                NotaDAO notaDAO = new NotaDAO();
                                
                                // Si no hay tareas o no hay estudiantes, el progreso es cero
                                if (tareas.isEmpty() || estudiantes.isEmpty()) {
                                    cursoJson.put("progress", 0);
                                    cursoJson.put("progressClass", "bg-secondary");
                                } else {
                                    int totalPosiblesNotas = tareas.size() * estudiantes.size();
                                    int totalNotasAsignadas = 0;
                                    
                                    for (Tarea tarea : tareas) {
                                        List<Nota> notas = notaDAO.getNotasPorTarea(tarea.getId());
                                        totalNotasAsignadas += notas.size();
                                    }
                                    
                                    int progress = (totalNotasAsignadas * 100) / totalPosiblesNotas;
                                    cursoJson.put("progress", progress);
                                    
                                    // Clase CSS según el progreso
                                    String progressClass;
                                    if (progress < 30) {
                                        progressClass = "bg-danger";
                                    } else if (progress < 70) {
                                        progressClass = "bg-warning";
                                    } else {
                                        progressClass = "bg-success";
                                    }
                                    cursoJson.put("progressClass", progressClass);
                                }
                                
                                cursosArray.put(cursoJson);
                            }
                            
                            logger.info("Cursos dashboard JSON: " + cursosArray.toString());
                            
                            // Log detallado para depuración
                            for (int i = 0; i < cursosArray.length(); i++) {
                                JSONObject obj = cursosArray.getJSONObject(i);
                                logger.info("Curso " + i + ": id=" + obj.optInt("id") + 
                                          ", nombre='" + obj.optString("nombre") + "'" +
                                          ", estudiantes=" + obj.optInt("estudiantes") + 
                                          ", progress=" + obj.optInt("progress") + 
                                          ", progressClass='" + obj.optString("progressClass") + "'");
                            }
                            
                            out.print(cursosArray.toString());
                            
                        } catch (Exception ex) {
                            logger.severe("[ProfesorControllerServlet] Error al obtener datos de cursos para dashboard: " + ex.getMessage());
                            ex.printStackTrace();
                            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        }
                    }
                    // Endpoint para tareas pendientes
                    else if (splits.length > 2 && "tareas-pendientes".equals(splits[2])) {
                        try {
                            int idProfesor = Integer.parseInt(request.getParameter("id_profesor"));
                            logger.info("[ProfesorControllerServlet] Consultando tareas pendientes para dashboard, profesor ID: " + idProfesor);
                            
                            // Obtener todas las tareas del profesor
                            TareaDAO tareaDAO = new TareaDAO();
                            CursoDAO cursoDAO = new CursoDAO();
                            NotaDAO notaDAO = new NotaDAO();
                            
                            List<Tarea> todasLasTareas = tareaDAO.getTareasPorProfesor(idProfesor);
                            JSONArray tareasPendientesArr = new JSONArray();
                            
                            for (Tarea tarea : todasLasTareas) {
                                // Para cada tarea, verificar si tiene estudiantes sin calificar
                                List<Estudiante> estudiantes = cursoDAO.getEstudiantesPorCurso(tarea.getIdCurso());
                                List<Nota> notas = notaDAO.getNotasPorTarea(tarea.getId());
                                
                                // Si no hay notas o hay menos notas que estudiantes, hay tareas por calificar
                                if (notas.isEmpty() || notas.size() < estudiantes.size()) {
                                    JSONObject tareaJson = new JSONObject();
                                    tareaJson.put("id", tarea.getId());
                                    tareaJson.put("titulo", tarea.getTitulo());
                                    tareaJson.put("curso_id", tarea.getIdCurso());
                                    
                                    // Número de estudiantes sin calificar
                                    int pendientes = estudiantes.size() - notas.size();
                                    tareaJson.put("count", pendientes);
                                    
                                    tareasPendientesArr.put(tareaJson);
                                }
                            }
                            
                            logger.info("Tareas pendientes dashboard JSON: " + tareasPendientesArr.toString());
                            out.print(tareasPendientesArr.toString());
                            
                        } catch (Exception ex) {
                            logger.severe("[ProfesorControllerServlet] Error al obtener tareas pendientes para dashboard: " + ex.getMessage());
                            ex.printStackTrace();
                            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        }
                    } else {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }
                    break;
                }
                    
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
        // Manejar encoding de parámetros POST
        request.setCharacterEncoding("UTF-8");
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
                    if (splits.length > 2 && "actualizar".equals(splits[2])) {
                        // Log de parámetros recibidos en actualizar
                        System.out.println("[ProfesorControllerServlet] POST /tareas/actualizar - Parámetros:");
                        java.util.Enumeration<String> namesUpd = request.getParameterNames();
                        while (namesUpd.hasMoreElements()) {
                            String n = namesUpd.nextElement();
                            System.out.println("    " + n + " = " + request.getParameter(n));
                        }
                        int idTarea = Integer.parseInt(request.getParameter("id_tarea"));
                        Tarea tareaUp = tareaDAO.read(idTarea);
                        if (tareaUp != null) {
                            tareaUp.setTitulo(request.getParameter("titulo"));
                            tareaUp.setDescripcion(request.getParameter("descripcion"));
                            // Actualizar curso si cambió
                            String cursoParam = request.getParameter("cursoId");
                            if (cursoParam != null && !cursoParam.isEmpty()) {
                                tareaUp.setId_curso(Integer.parseInt(cursoParam));
                            }
                            // fecha_asignacion y fecha_entrega opcionales
                            String asig = request.getParameter("fecha_asignacion");
                            String entreg = request.getParameter("fecha_entrega");
                            try {
                                if (asig != null && !asig.isEmpty()) tareaUp.setFecha_asignacion(dateFormat.parse(asig));
                                if (entreg != null && !entreg.isEmpty()) tareaUp.setFecha_entrega(dateFormat.parse(entreg));
                            } catch (java.text.ParseException pe) {
                                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Fecha inválida");
                                return;
                            }
                            boolean ok = tareaDAO.update(tareaUp);
                            if (ok) {
                                out.print(new JSONObject().put("message","Tarea actualizada").toString());
                                out.flush();
                                return;
                            } else response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error actualizando tarea");
                        } else {
                            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Tarea no encontrada");
                        }
                        break;
                    }
                    if (splits.length > 2 && splits[2].equals("calificar")) {
                        // Log de parámetros recibidos
                        System.out.println("[ProfesorControllerServlet] POST /tareas/calificar - Parámetros:");
                        java.util.Enumeration<String> allParams = request.getParameterNames();
                        while (allParams.hasMoreElements()) {
                            String nameP = allParams.nextElement();
                            System.out.println("    " + nameP + " = " + request.getParameter(nameP));
                        }
                        int idTarea = Integer.parseInt(request.getParameter("id_tarea"));
                        NotaDAO notaDao = new NotaDAO();
                        EstudianteDAO estDao = new EstudianteDAO();
                        java.util.Enumeration<String> params = request.getParameterNames();
                        java.util.Set<Integer> procesados = new java.util.HashSet<>();
                        while (params.hasMoreElements()) {
                            String p = params.nextElement();
                            if (p.startsWith("nota_")) {
                                int idEst = Integer.parseInt(p.substring(5));
                                double valor = Double.parseDouble(request.getParameter(p));
                                String com = request.getParameter("comentario_" + idEst);
                                // Preparar nota a crear o actualizar
                                Nota nota = new Nota();
                                nota.setIdTarea(idTarea);
                                nota.setIdEstudiante(idEst);
                                nota.setNota(valor);
                                nota.setComentario(com == null ? "" : com);
                                nota.setFechaEvaluacion(new java.util.Date());
                                // Inserta o actualiza en un solo paso
                                notaDao.createOrUpdate(nota);
                                procesados.add(idEst);
                            }
                        }
                        // Actualizar promedio general del estudiante (todas las notas)
                        for (int idEst : procesados) {
                            try {
                                double prom = estDao.calcularPromedio(idEst);
                                estDao.updatePromedioAcademico(idEst, prom);
                                logger.info("Promedio académico actualizado Estudiante " + idEst + ": " + prom);
                            } catch (java.sql.SQLException sqle) {
                                logger.severe("Error actualizando promedio académico para estudiante " + idEst + ": " + sqle.getMessage());
                            }
                        }
                        out.print(new JSONObject().put("message", "Calificaciones procesadas y promedios actualizados").toString());
                        out.flush();
                        return;
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
                                out.print(new JSONObject().put("message", "Tarea creada exitosamente").toString());
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
                            
                            // También actualizar curso si se envió
                            String cursoParam = request.getParameter("cursoId");
                            if (cursoParam != null && !cursoParam.isEmpty()) {
                                tarea.setId_curso(Integer.parseInt(cursoParam));
                            }
                            
                            boolean success = tareaDAO.update(tarea);
                            if (success) {
                                out.print(new JSONObject().put("message", "Tarea actualizada exitosamente").toString());
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
                        out.print(new JSONObject().put("message", "Tarea eliminada exitosamente").toString());
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