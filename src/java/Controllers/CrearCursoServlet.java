package Controllers;

import DAOs.CursoDAO;
import DAOs.ProfesorDAO;
import Models.Curso;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name="CrearCursoServlet", urlPatterns = {"/profesor/cursos/crear"})
public class CrearCursoServlet extends HttpServlet {
    private CursoDAO cursoDAO;
    private ProfesorDAO profesorDAO;

    @Override
    public void init() throws ServletException {
        try {
            cursoDAO = new CursoDAO();
            profesorDAO = new ProfesorDAO();
        } catch (Exception e) {
            throw new ServletException("Error inicializando DAO", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        int userId = (int) session.getAttribute("userId");
        Integer idProfesor = profesorDAO.getProfesorIdByUsuario(userId);
        if (idProfesor == null) {
            throw new ServletException("No se encontró profesor para usuario ID: " + userId);
        }

        String nombre = request.getParameter("nombre");
        String codigo = request.getParameter("codigo");
        String descripcion = request.getParameter("descripcion");

        Curso curso = new Curso();
        curso.setNombre(nombre);
        curso.setCodigo(codigo);
        curso.setDescripcion(descripcion);
        curso.setIdProfesor(idProfesor);

        try {
            boolean creado = cursoDAO.create(curso);
            if (creado) {
                response.sendRedirect(request.getContextPath() + "/profesor/cursos");
            } else {
                request.setAttribute("error", "No se pudo crear el curso");
                request.getRequestDispatcher("/profesorcursos.jsp").forward(request, response);
            }
        } catch (Exception e) {
            throw new ServletException("Error al crear curso", e);
        }
    }
}
