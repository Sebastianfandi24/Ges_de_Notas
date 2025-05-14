package Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "IndexServlet", urlPatterns = {"/inicio"})
public class IndexServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            // Si ya hay una sesión activa, redirigir al menú principal
            response.sendRedirect(request.getContextPath() + "/menu");
        } else {
            // Si no hay sesión, mostrar la página de inicio
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
}
