package Controllers;

import DAOs.ActividadDAO;
import Models.Actividad;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "MenuController", urlPatterns = {"/menu"})
public class MenuController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userRol") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        int userRol = (int) session.getAttribute("userRol");
        try {
            ActividadDAO dao = new ActividadDAO();
            List<Actividad> actividades = dao.obtenerPorRol(userRol);
            request.setAttribute("actividades", actividades);
            // Enlace por defecto para el iframe, primera actividad si existe
            String defaultEnlace = actividades.isEmpty() ? "login.jsp" : actividades.get(0).getEnlace();
            request.setAttribute("defaultEnlace", defaultEnlace);
        } catch (SQLException e) {
            throw new ServletException("Error cargando actividades por rol", e);
        }
        RequestDispatcher rd = request.getRequestDispatcher("/menuprincipal.jsp");
        rd.forward(request, response);
    }
}