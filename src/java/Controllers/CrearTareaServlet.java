package Controllers;

import DAOs.TareaDAO;
import DAOs.NotaDAO;
import DAOs.EstudianteDAO;
import Models.Tarea;
import Models.Nota;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Logger;

@WebServlet(name="CrearTareaServlet", urlPatterns = {"/profesor/tareas/crear"})
public class CrearTareaServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(CrearTareaServlet.class.getName());    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Instanciar DAO y formateador localmente
        TareaDAO tareaDAO;
        NotaDAO notaDAO;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            tareaDAO = new TareaDAO();
            notaDAO = new NotaDAO();
            
            String titulo = request.getParameter("titulo");
            String descripcion = request.getParameter("descripcion");
            Date fechaEntrega = sdf.parse(request.getParameter("fechaEntrega"));
            int idCurso = Integer.parseInt(request.getParameter("idCurso"));

            // Crear tarea
            Tarea tarea = new Tarea();
            tarea.setTitulo(titulo);
            tarea.setDescripcion(descripcion);
            tarea.setFecha_asignacion(new Date());
            tarea.setFecha_entrega(fechaEntrega);
            tarea.setId_curso(idCurso);
            int idTarea = tareaDAO.createAndReturnId(tarea);
            logger.info(String.format("Tarea creada con ID=%d", idTarea));

            // Recorrer parámetros de notas y comentarios
            Enumeration<String> names = request.getParameterNames();
            while (names.hasMoreElements()) {
                String param = names.nextElement();
                if (param.startsWith("nota_")) {
                    int idEst = Integer.parseInt(param.substring(5));
                    String strNota = request.getParameter(param);
                    String comentario = request.getParameter("comentario_" + idEst);
                    if (strNota != null && !strNota.isEmpty()) {
                        double notaVal = Double.parseDouble(strNota);
                        Nota nota = new Nota();
                        nota.setIdTarea(idTarea);
                        nota.setIdEstudiante(idEst);
                        nota.setNota(notaVal);
                        nota.setComentario(comentario != null ? comentario : "");
                        nota.setFechaEvaluacion(new Date());
                        notaDAO.create(nota);
                        logger.info(String.format("Nota creada: tarea=%d est=%d val=%.1f com=%s", idTarea, idEst, notaVal, comentario));
                        // Calcular y actualizar promedio académico del estudiante
                        EstudianteDAO estDao = new EstudianteDAO();
                        try {
                            double prom = estDao.calcularPromedio(idEst);
                            estDao.updatePromedioAcademico(idEst, prom);
                            logger.info("Promedio académico actualizado Estudiante " + idEst + ": " + prom);
                        } catch (Exception ex) {
                            logger.severe("Error actualizando promedio académico: " + ex.getMessage());
                        }
                    }
                }
            }
            response.sendRedirect(request.getContextPath() + "/profesor/tareas");
        } catch (Exception ex) {
            logger.log(java.util.logging.Level.SEVERE, "Error en CrearTareaServlet", ex);
            throw new ServletException("Error creando tarea", ex);
        }
    }
}
